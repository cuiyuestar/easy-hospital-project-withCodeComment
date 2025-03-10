package com.easy.hospital.service;

import com.easy.hospital.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface WeChatService {
    OrderPaymentVO generatePrepayId(PrepayDTO prepayDTO);

    void payNotify(HttpServletRequest request);

    WeChatQueryOrderVO queryPayOrder(WeChatQueryOrderDTO weChatQueryOrderDTO);
}
