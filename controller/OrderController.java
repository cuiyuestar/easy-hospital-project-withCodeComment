package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.OrderDTO;
import com.easy.hospital.dto.OrderListReq;
import com.easy.hospital.dto.OrderVO;
import com.easy.hospital.service.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api/wx/order")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class OrderController {
    private final OrderService orderService;

    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    @PostMapping("/createOrder")
    public RespResult<OrderVO> createOrder(@RequestBody OrderDTO orderDTO){
        return RespUtils.success(orderService.createOrder(orderDTO));
    }

    /**
     * 查看订单
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<OrderVO>> listOrder(@RequestBody OrderListReq req){
        return RespUtils.success(orderService.listOrder(req));
    }
}
