package com.easy.hospital.service.impl;

import com.alibaba.fastjson.JSON;
import com.easy.hospital.common.enums.AppointmentCommonStatusEnum;
import com.easy.hospital.common.enums.AppointmentStatusEnum;
import com.easy.hospital.common.enums.OrderStatusEnum;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.common.utils.WeChatUtils;
import com.easy.hospital.dao.model.Appointment;
import com.easy.hospital.dao.model.Order;
import com.easy.hospital.dao.repository.AppointmentRepository;
import com.easy.hospital.dao.repository.DoctorWalletRepository;
import com.easy.hospital.dao.repository.OrderRepository;
import com.easy.hospital.dto.OrderPaymentVO;
import com.easy.hospital.dto.PrepayDTO;
import com.easy.hospital.dto.WeChatQueryOrderDTO;
import com.easy.hospital.dto.WeChatQueryOrderVO;
import com.easy.hospital.model.bo.AccountTTransInputBO;
import com.easy.hospital.service.AccountService;
import com.easy.hospital.service.OrderService;
import com.easy.hospital.service.WeChatService;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class WeChatServiceImpl implements WeChatService {
    private final static Lock lock = new ReentrantLock();
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private WeChatUtils weChatUtils;
    @Resource
    private DoctorWalletRepository doctorWalletRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AccountService accountService;

    /**
     * 创建预支付信息
     * @param prepayDTO
     * @return
     */
    @Override
    public OrderPaymentVO generatePrepayId(PrepayDTO prepayDTO) { //注：PrepayDTO仅包含订单编号orderNo
        Order order = orderRepository.getByOrderNo(prepayDTO.getOrderNo()); //根据订单编号查到订单
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        //Map是以订单号orderNo为键，预支付响应response对象为值的关系表
        //这里调用的payment方法，它先将传入的参数封装为requset对象，传入微信提供的prepay方法，得到响应对象response
        //并以orderNo和response对象构建键值对，存入map中并返回出来
        Map<String, Object> result = weChatUtils.payment(order.getAmount(), order.getOrderNo(), openid);
        log.info("预支付返回结果:{}", JSON.toJSONString(result));
        return OrderPaymentVO.builder()
                .timeStamp(result.get("timeStamp").toString()) //支付时间戳
                .nonceStr(result.get("nonceStr").toString())
                .packageStr(result.get("package").toString()) //支付包数据
                .signType(result.get("signType").toString())
                .paySign(result.get("paySign").toString()) //支付签名（用于验证数据完整）
                .build();
    }

    /**
     * 支付回调（发生支付异常时调用——支付异常表现为：微信支付状态不符合任何一种既定的支付状态）
     * @param request
     */
    @Override
    @Transactional //支付回调这种敏感操作必须通过事务保证原子性
    public void payNotify(HttpServletRequest request) {
        //log.info("支付回调:{}", weChatUtils.getRequestBody(request));
        Transaction parse = weChatUtils.payNotify(request); //将微信支付回调请求内的支付信息封装到Transaction类对象parse
        String orderNo = parse.getOutTradeNo(); //从parse对象内获取订单号
        Transaction.TradeStateEnum tradeState = parse.getTradeState(); //从parse对象内获取交易状态
        Integer status = null;
        Order order = orderRepository.getByOrderNo(orderNo);
        if (tradeState == Transaction.TradeStateEnum.SUCCESS) { //如果该订单交易成功
            status = OrderStatusEnum.PAID.getCode(); //则将订单状态设置为已支付
            Appointment appointment = new Appointment()
                    .setOpenid(order.getOpenid())
                    .setAppointmentId(order.getAppointmentId())
                    .setDoctorId(order.getDoctorId())
                    .setPrice(order.getAmount())
                    .setAppointmentDate(order.getAppointmentDate())
                    .setDoctorScheduleId(order.getDoctorScheduleId())
                    .setStatus(AppointmentStatusEnum.PENDING.getCode()) //支付成功后，将预约状态设置为待处理，表示有资格参与预约了
                    .setGmtCreated(new Date())
                    .setGmtModified(new Date())
                    .setCommentStatus(AppointmentCommonStatusEnum.UNCOMMENTED.getCode());
            appointmentRepository.save(appointment);
            //doctorWalletRepository.updateMoneyByDoctorId(order.getDoctorId(), order.getAmount());
            try {
                TransactionResponse transactionResponse = accountService.tTrans(new AccountTTransInputBO(
                        new BigInteger(1, order.getOpenid().getBytes()),
                        BigInteger.valueOf(order.getDoctorId()),
                        BigInteger.valueOf(order.getAmount())
                ));
                log.info("链上tTrans返回:{}", transactionResponse);
            } catch (Exception e) {
                log.error("链上T币交易失败");
                throw new RuntimeException("链上T币交易失败", e);
            }
            //根据不同的支付状态，设置相应的订单状态
        } else if (tradeState == Transaction.TradeStateEnum.REFUND) {
            status = OrderStatusEnum.REFUNDED.getCode();
        } else if (tradeState == Transaction.TradeStateEnum.NOTPAY) {
            status = OrderStatusEnum.UNPAID.getCode();
        } else if (tradeState == Transaction.TradeStateEnum.CLOSED) {
            status = OrderStatusEnum.CANCELLED.getCode();
        } else { //如果都不符合以上情况，则说明发生了异常，需要进行支付回调
            // todo 其他情况
            log.info("支付回调:{}", tradeState);
            status = OrderStatusEnum.CANCELLED.getCode();
        }
        if (!Objects.equals(status, OrderStatusEnum.PAID.getCode())) {
            try {
                lock.lock();
                orderService.updateScheduleRemain(order.getDoctorScheduleId(), 1);
            } catch (Exception e) {
                log.error("返回人数失败");
                throw new RuntimeException("返回人数失败");
            } finally {
                lock.unlock();
            }
        }
        orderRepository.updateStatus(orderNo, status);
    }

    /**
     * 查询支付订单（包含：订单号、订单状态）
     * @param weChatQueryOrderDTO
     * @return
     */
    @Override
    public WeChatQueryOrderVO queryPayOrder(WeChatQueryOrderDTO weChatQueryOrderDTO) { //注：WeChatQueryOrderDTO仅包含订单编号orderNo
        Transaction transaction = weChatUtils.queryPayOrder(weChatQueryOrderDTO.getOrderNo());
        Integer status = null;
        Order order = orderRepository.getByOrderNo(weChatQueryOrderDTO.getOrderNo());
        if (transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
            status = OrderStatusEnum.PAID.getCode();
            //doctorWalletRepository.updateMoneyByDoctorId(order.getDoctorId(), order.getAmount());
        } else if (transaction.getTradeState() == Transaction.TradeStateEnum.REFUND) {
            status = OrderStatusEnum.REFUNDED.getCode();
        } else if (transaction.getTradeState() == Transaction.TradeStateEnum.NOTPAY) {
            status = OrderStatusEnum.UNPAID.getCode();
        } else if (transaction.getTradeState() == Transaction.TradeStateEnum.CLOSED) {
            status = OrderStatusEnum.CANCELLED.getCode();
        } else {
            // todo 其他情况
            log.info("支付回调:{}", transaction.getTradeState());
            status = OrderStatusEnum.CANCELLED.getCode();
        }
        if (!Objects.equals(status, OrderStatusEnum.PAID.getCode())) {
            try {
                lock.lock();
                orderService.updateScheduleRemain(order.getDoctorScheduleId(), 1);
            } catch (Exception e) {
                log.error("返回人数失败");
                throw new RuntimeException("返回人数失败");
            } finally {
                lock.unlock();
            }
        }
        orderRepository.updateStatus(weChatQueryOrderDTO.getOrderNo(), status);
        return WeChatQueryOrderVO.builder()
                .orderNo(transaction.getOutTradeNo())
                .status(status)
                .build();
    }
}
