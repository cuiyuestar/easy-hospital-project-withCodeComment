package com.easy.hospital.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.response.ServiceException;
import com.easy.hospital.common.config.JwtProperties;
import com.easy.hospital.common.config.WechatProperties;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.model.WXUser;
import com.easy.hospital.dao.repository.WXUserRepository;
import com.easy.hospital.dto.WXUserLoginDTO;
import com.easy.hospital.dto.WXUserUpdateDTO;
import com.easy.hospital.dto.WeChatLoginResp;
import com.easy.hospital.model.bo.AccountAddPatientInputBO;
import com.easy.hospital.model.bo.AccountGetTInputBO;
import com.easy.hospital.service.AccountService;
import com.easy.hospital.service.WXUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Date;

@Slf4j
@Service
public class WXUserServiceImpl implements WXUserService {
    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private WXUserRepository wxUserRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private AccountService accountService;

    /**
     * 微信小程序登录？
     * @param dto
     * @return
     */
    @Override
    public WXUser wxLogin(WXUserLoginDTO dto) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatProperties.getAppId(), wechatProperties.getSecret(), dto.getCode()
        );
        String forObject = restTemplate.getForObject(url, String.class);
        log.info("微信登录返回结果:{}", forObject);
        WeChatLoginResp weChatLoginResp = JSONObject.parseObject(forObject, WeChatLoginResp.class);
        if (weChatLoginResp == null || weChatLoginResp.getErrcode() != null) {
            throw new ServiceException("400", "微信登录失败");
        }

        String openid = weChatLoginResp.getOpenid();
        WXUser wxUser = wxUserRepository.getByOpenid(openid);
        if (wxUser == null) {
            wxUser = new WXUser().setOpenid(openid).setGmtCreated(new Date());
            wxUserRepository.save(wxUser);
            try {
                TransactionResponse transactionResponse = accountService.addPatient(new AccountAddPatientInputBO(
                        wxUser.getName(),
                        new BigInteger(1, wxUser.getOpenid().getBytes())
                ));
                log.info("链上addPatient返回：{}", transactionResponse);
            } catch (Exception e) {
                log.error("链上添加患者失败");
                throw new RuntimeException("链上添加患者失败", e);
            }
        }
        return wxUser;
    }

    /**
     * 根据openid获取用户信息
     * @return
     */
    @Override
    public WXUser getInfo() {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        return wxUserRepository.getByOpenid(openid);
    }

    /**
     * 修改用户信息
     * @param dto
     * @return
     */
    @Override
    public String saveInfo(WXUserUpdateDTO dto) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        dto.setOpenid(openid);
        log.info("wx修改个人信息:{}", JSON.toJSON(dto));
        // 身份证校验
        if (dto.getIdentityId() == null || !dto.getIdentityId().matches("[1-9]\\d{5}(?:18|19|20)\\d{2}(?:0[1-9]|10|11|12)(?:0[1-9]|[1-2]\\d|30|31)\\d{3}[\\dXx]")) {
            return "身份证格式错误";
        }
        // 手机号校验
        if (dto.getPhone() == null || !dto.getPhone().matches("1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}")) {
            return "手机号格式错误";
        }
        // 性别校验
        if (dto.getGender() != 1 && dto.getGender() != 2) {
            return "性别有误";
        }
        if (!wxUserRepository.updateByOpenId(dto)) {
            return "fail";
        }
        return "success";
    }

    @Override
    public Integer getT() {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        try {
            CallResponse callResponse = accountService.getT(new AccountGetTInputBO(
                    new BigInteger(1, openid.getBytes())
            ));
            log.info("链上getT返回:{}", callResponse);
            return ((BigInteger) callResponse.getReturnObject().getFirst()).intValue();
        } catch (Exception e) {
            log.error("链上获取T币数失败");
            throw new RuntimeException("链上获取T币数失败", e);
        }
    }

}
