package com.easy.hospital.service;

import com.easy.hospital.dto.OrderDTO;
import com.easy.hospital.dto.OrderListReq;
import com.easy.hospital.dto.OrderVO;
import com.github.pagehelper.PageInfo;

public interface OrderService {
    OrderVO createOrder(OrderDTO orderDTO);

    PageInfo<OrderVO> listOrder(OrderListReq req);

    void updateScheduleRemain(Long doctorScheduleId, Integer type);
}
