package com.easy.hospital.service.impl;

import com.easy.hospital.common.enums.AppointmentStatusEnum;
import com.easy.hospital.common.enums.OrderStatusEnum;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.utils.BizUtils;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.common.utils.WeChatUtils;
import com.easy.hospital.dao.model.Appointment;
import com.easy.hospital.dao.model.Order;
import com.easy.hospital.dao.repository.AppointmentRepository;
import com.easy.hospital.dao.repository.DoctorScheduleRepository;
import com.easy.hospital.dao.repository.OrderRepository;
import com.easy.hospital.dto.OrderDTO;
import com.easy.hospital.dto.OrderListReq;
import com.easy.hospital.dto.OrderVO;
import com.easy.hospital.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    public final Lock lock = new ReentrantLock();
    @Resource
    private AppointmentRepository appointmentRepository;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private DoctorScheduleRepository doctorScheduleRepository;
    @Resource
    private WeChatUtils weChatUtils;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class) //设置回滚机制：一旦抛出异常，则回滚事务
    public OrderVO createOrder(OrderDTO orderDTO) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        String appointmentId = BizUtils.generateAppointmentId();
        Order order;
        try {
            order = createOrderInner(orderDTO, openid, appointmentId);
            //在五分钟之内如果订单状态没变化（即没支付），则支付失败
            scheduler.schedule(() -> checkOrderStatusAndTimeOut(order), 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("创建订单失败");
            throw new RuntimeException("创建订单失败");
        }
        return new OrderVO().setOrderNo(order.getOrderNo());
    }

    private void checkOrderStatusAndTimeOut(Order order) {
        String orderNo = order.getOrderNo();
        Transaction transaction = weChatUtils.queryPayOrder(orderNo);
        if (transaction.getTradeState() == Transaction.TradeStateEnum.NOTPAY) {//检测到未支付
            weChatUtils.closePayOrder(orderNo); //关闭订单支付
            //更新数据库中对应订单的支付状态（更新为未支付状态）
            orderRepository.updateStatus(orderNo, OrderStatusEnum.CANCELLED.getCode());
        }
    }

    /**
     * 创建订单
     * @param orderDTO
     * @param openid
     * @param appointmentId
     * @return
     */
    private Order createOrderInner(OrderDTO orderDTO, String openid, String appointmentId) {
        //在多线程环境下安全地检查并更新医生排班的可用名额
        try {
            lock.lock();
            //查看医生排班表，如果可用的名额为0，则抛出异常
            if (doctorScheduleRepository.getById(orderDTO.getDoctorScheduleId()).getAvailableSlots() == 0) {
                throw new Exception();
            }
            updateScheduleRemain(orderDTO.getDoctorScheduleId(), 0);
        } catch (Exception e) { //捕获上面抛出的异常，并抛出RuntimeException异常，告诉用户排班人数不足
            log.error("排班人数不足");
            throw new RuntimeException("排班人数不足");
        } finally {
            lock.unlock();
        }
        Order order = new Order()
                .setOrderNo(BizUtils.generateOrderNumber())
                .setIdentityId(orderDTO.getIdentityId())
                .setStatus(OrderStatusEnum.UNPAID.getCode())
                .setCreateTime(new Date())
                .setPayTime(null)
                .setAmount(orderDTO.getAmount())
                .setPaymentAmount(null)
                .setDoctorId(orderDTO.getDoctorId())
                .setDoctorScheduleId(orderDTO.getDoctorScheduleId())
                .setHospitalId(orderDTO.getHospitalId())
                .setOpenid(openid)
                .setAppointmentId(appointmentId)
                .setDepartmentId(orderDTO.getDepartmentId())
                .setAppointmentDate(orderDTO.getAppointmentDate())
                .setAppointmentTime(orderDTO.getAppointmentTime());
        orderRepository.save(order);
        return order;
    }


    /**
     * 查询用户订单
     * @param req
     * @return
     */
    @Override
    public PageInfo<OrderVO> listOrder(OrderListReq req) {
        String token = LoginUserHolder.getToken(); //从本地线程池取得token
        String openid = JWTUtils.parseToken(token);//解析token并获得其中的openid
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<Order> orders = orderRepository.listOnOpenid(openid); //根据用户openid查询该用户的全部订单并封装到orders对象里
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        List<OrderVO> collect = orders.stream().map(OrderVO::ofOrder).toList();//转为前端传输对象并存入新列表
        PageInfo<OrderVO> resultInfo = new PageInfo<>();
        resultInfo.setTotal(pageInfo.getTotal());
        resultInfo.setList(collect);
        return resultInfo;
    }

    /**
     * 更新排班剩余人数
     * @param doctorScheduleId 排班ID
     * @param type             0减少 1增加
     */
    @Override
    public void updateScheduleRemain(Long doctorScheduleId, Integer type) {
        if (type == 0) {
            doctorScheduleRepository.reduceAppointmentCount(doctorScheduleId);
        } else {
            doctorScheduleRepository.increaseAppointmentCount(doctorScheduleId);
        }
    }
}
