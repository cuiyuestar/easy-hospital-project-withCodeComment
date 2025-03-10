package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.OrderMapper;
import com.easy.hospital.dao.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository extends ServiceImpl<OrderMapper, Order> {
    public Order getByOrderNo(String orderNo) {
        return lambdaQuery().eq(Order::getOrderNo, orderNo).one();
    }

    public void updateStatus(String orderNo, Integer status) {
        lambdaUpdate().eq(Order::getOrderNo, orderNo).set(Order::getStatus, status).update();
    }

    public List<Order> listOnOpenid(String openid) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    public Order getByAppointmentId(String appointmentId){
        return lambdaQuery().eq(Order::getAppointmentId, appointmentId).one();
    }
}
