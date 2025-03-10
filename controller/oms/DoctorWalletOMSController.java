package com.easy.hospital.controller.oms;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dto.DoctorWalletVO;
import com.easy.hospital.dto.WithdrawDTO;
import com.easy.hospital.service.DoctorWalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 医生钱包控制器
 */
@RestController
@RequestMapping("/api/easy-online-hospital/doctorWallet")
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DoctorWalletOMSController {
    private final DoctorWalletService doctorWalletService;

    /**
     * 查询医生钱包
     * @return
     */
    @GetMapping("/getDoctorWallet")
    public RespResult<DoctorWalletVO> getDoctorWallet() {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        Long doctorIdLong = Long.parseLong(doctorId);
        log.info("获取医生钱包:{}", doctorIdLong);
        return RespUtils.success(doctorWalletService.getWalletInfo(doctorIdLong));
    }

    /**
     * 提现
     * @return
     */
    @PostMapping("/withdraw")
    public RespResult<Void> withdraw(@RequestBody WithdrawDTO dto) {
        log.info("医生：{}, 提现:{}", dto.getDoctorId(), dto.getAmount());
        doctorWalletService.withdraw(dto);
        return RespUtils.success();
    }
}
