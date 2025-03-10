package com.easy.hospital.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.easy.hospital.common.enums.AppointmentStatusEnum;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.mapper.AppointmentMapper;
import com.easy.hospital.dao.model.Appointment;
import com.easy.hospital.dao.model.DoctorSchedule;
import com.easy.hospital.dao.model.Order;
import com.easy.hospital.dao.repository.*;
import com.easy.hospital.dto.*;
import com.easy.hospital.model.bo.AccountPayInputBO;
import com.easy.hospital.service.AccountService;
import com.easy.hospital.service.AppointmentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Resource
    private DoctorScheduleRepository doctorScheduleRepository;
    @Resource
    private AppointmentMapper appointmentMapper;
    @Resource
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private DoctorWalletRepository doctorWalletRepository;
    @Autowired
    private OrderRepository orderRepository;

    /**
     * 获取预约价格
     * @param dto
     * @return
     */
    @Override
    public AppointmentVO getPrice(AppointmentDTO dto) {
        //需要通过预约日期时间和医生id才能准确确定一个预约，并获取该预约对应的价格（可能同一个医生，不同时段会有不同价格，或者相反）
        DoctorSchedule doctorSchedule = doctorScheduleRepository.getByDateTimeAnDoctorId(dto.getDate(), dto.getTime(), dto.getId());
        //如果没有找到对应的预约，返回空值
        if (doctorSchedule == null) {
            return null;
        }
        return new AppointmentVO().setTotal(doctorSchedule.getPrice()).setDoctorScheduleId(doctorSchedule.getId());
    }

    /**
     * 分页展示当前医生的预约列表
     * @param req
     * @return
     */
    @Override
    public PageInfo<AppointmentDoctorVO> list(AppointmentListReq req) {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        Long doctorIdLong = Long.parseLong(doctorId);
        req.setDoctorId(doctorIdLong);
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<AppointmentDoctorVO> appointments = appointmentMapper.listOnCondition(req);
        return new PageInfo<>(appointments);
    }

    /**
     * 修改预约状态？
     * @param req
     * @return
     */
    @Override
    public boolean changeStatus(AppointmentChangeStatusReq req) {
        if (Objects.equals(req.getStatus(), AppointmentStatusEnum.COMPLETED.getCode())) {
            Appointment appointment = appointmentRepository.getById(req.getAppointmentId());
            try {
                TransactionResponse transactionResponse = accountService.pay(new AccountPayInputBO(
                        new BigInteger(1, appointment.getOpenid().getBytes()),
                        BigInteger.valueOf(1)
                ));
                log.info("链上pay返回:{}", transactionResponse);
            } catch (Exception e) {
                log.error("链上添加v币失败");
                throw new RuntimeException("链上添加v币失败", e);
            }
            Order order = orderRepository.getByAppointmentId(appointment.getAppointmentId());
            doctorWalletRepository.updateMoneyByDoctorId(order.getDoctorId(), order.getAmount());
        }
        return appointmentRepository.updateById(req);
    }

    /**
     * 分页查询用户预约列表
     * @param req
     * @return
     */
    @Override
    public PageInfo<AppointmentWXUserVO> listWX(AppointmentWXUserListReq req) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        req.setOpenid(openid);
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<AppointmentWXUserVO> appointments = appointmentMapper.listOnConditionWX(req);
        return new PageInfo<>(appointments);
    }

    /**
     * 修改预约评价状态？
     * @param req
     */
    @Override
    public void changeCommentStatus(AppointmentChangeCommentStatusReq req) {
        appointmentRepository.updateById(req);
    }

    @Override
    public Integer getUncommentCount() {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        return appointmentRepository.getUncommentCount(openid);
    }
}
