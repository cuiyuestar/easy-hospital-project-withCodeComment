package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dto.OrderPaymentVO;
import com.easy.hospital.dto.PrepayDTO;
import com.easy.hospital.dto.WeChatQueryOrderDTO;
import com.easy.hospital.dto.WeChatQueryOrderVO;
import com.easy.hospital.service.WeChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信支付控制器
 */
@RestController
@RequestMapping("/api/wx/pay")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WeChatPayController {
    private final WeChatService weChatService;

    /**
     * 生成预支付订单
     * @param prepayDTO
     * @return
     */
    @PostMapping("/prepay")
    public RespResult<OrderPaymentVO> generatePrepayId(@RequestBody PrepayDTO prepayDTO){
        return RespUtils.success(weChatService.generatePrepayId(prepayDTO));
    }

    /**
     * 支付成功回调
     * @param request
     * @return
     */
    @PostMapping("/notify/paySuccess")
    public RespResult<Void> payNotify(HttpServletRequest request){
        weChatService.payNotify(request);
        return RespUtils.success();
    }

    /**
     * 查询支付订单
     * @param weChatQueryOrderDTO
     * @return
     */
    @PostMapping("/queryPayOrder")
    public RespResult<WeChatQueryOrderVO> queryPayOrder(@RequestBody WeChatQueryOrderDTO weChatQueryOrderDTO){
        return RespUtils.success(weChatService.queryPayOrder(weChatQueryOrderDTO));
    }
}
