package com.easy.hospital.controller;

import com.alibaba.fastjson.JSON;
import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespSystemCode;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.model.WXUser;
import com.easy.hospital.dto.WXUserLoginDTO;
import com.easy.hospital.dto.WXUserLoginVO;
import com.easy.hospital.dto.WXUserUpdateDTO;
import com.easy.hospital.service.WXUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序用户接口
 */
@Slf4j
@RestController
@RequestMapping("/api/wx/user")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final WXUserService wxUserService;

    /**
     * 小程序用户登录
     *
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public RespResult<WXUserLoginVO> login(@RequestBody WXUserLoginDTO dto) {
        log.info("小程序用户登录:[{}]", JSON.toJSON(dto));
        WXUser wxUser = wxUserService.wxLogin(dto);
        String token = JWTUtils.getTokenWithOpenid(wxUser.getOpenid());
        return RespUtils.success(new WXUserLoginVO().setOpenid(wxUser.getOpenid()).setToken(token));
    }

    /**
     * 查询个人信息
     */
    @GetMapping("/info")
    public RespResult<WXUser> getInfo() {
        return RespUtils.success(wxUserService.getInfo());
    }

    /**
     * 更新个人信息
     */
    @PostMapping("/save")
    public RespResult<Void> saveInfo(@RequestBody WXUserUpdateDTO dto) {
        String msg = wxUserService.saveInfo(dto);
        return msg.equals("success") ? RespUtils.success() : RespUtils.fail("400", msg);
    }

    /**
     * 获取用户T币数
     */
    @GetMapping("/getT")
    public RespResult<Integer> getT(){
        return RespUtils.success(wxUserService.getT());
    }
}
