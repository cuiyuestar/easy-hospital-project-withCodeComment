package com.easy.hospital.controller.oms;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespSystemCode;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.model.Doctor;
import com.easy.hospital.dto.*;
import com.easy.hospital.service.DoctorService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/easy-online-hospital/doctor")
public class DoctorOMSController {
    @Resource
    private DoctorService doctorService;

    /**
     * 分页查询医生列表
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<DoctorOMSVO>> listDoctor(@RequestBody DoctorListReq req) {
        return RespUtils.success(doctorService.list(req));
    }

    /**
     * 修改医生状态
     * @param id
     * @param status
     * @return
     */
    @GetMapping("/status")
    public RespResult<Void> changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        doctorService.changeStatus(id, status);
        return RespUtils.success();
    }

    /**
     * 截取请求路径的id来获取医生详情
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public RespResult<Doctor> detail(@RequestParam("id") Long id) {
        return RespUtils.success(doctorService.detail(id));
    }

    @GetMapping("/deleted")
    public RespResult<Void> deleted(@RequestParam("id") Long id) {
        doctorService.deleted(id);
        return RespUtils.success();
    }

    @PostMapping("/saveOrUpdate")
    public RespResult<Void> saveOrUpdate(@RequestBody DoctorSaveDTO doctor) {
        doctorService.saveOrUpdate(doctor);
        return RespUtils.success();
    }

    @PostMapping("/login")
    public RespResult<String> login(@RequestBody DoctorLoginReq req) {
        return RespUtils.success(doctorService.login(req));
    }

    /**
     * 医生注册
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    public RespResult<Void> register(@RequestBody DoctorRegisterDTO registerDTO) {
        doctorService.register(registerDTO);
        return RespUtils.success();
    }

    /**
     * 重置密码
     * @param doctorId
     * @return
     */
    @GetMapping("/resetPassword")
    public RespResult<Void> resetPassword(@RequestParam("doctorId") Long doctorId) {
        doctorService.resetPassword(doctorId);
        return RespUtils.success();
    }

    /**
     * 医生详情（医生端）
     * @return
     */
    @GetMapping("/detailForDoctor")
    public RespResult<Doctor> detailForDoctor() {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        Long doctorIdLong = Long.parseLong(doctorId);
        return RespUtils.success(doctorService.detail(doctorIdLong));
    }

    @PostMapping("/updateForDoctor")
    public RespResult<Void> updateForDoctor(@RequestBody DoctorUpdateDTO dto) {
        return doctorService.updateForDoctor(dto) ?
                RespUtils.success() :
                RespUtils.fail(RespSystemCode.REQUEST_FAIL);
    }

    @PostMapping("/setPassword")
    public RespResult<Void> setPassword(@RequestBody SetPasswordReq req){
        doctorService.setPassword(req);
        return RespUtils.success();
    }

    @GetMapping("/getT")
    public RespResult<Integer> getT(){
        return RespUtils.success(doctorService.getT());
    }
}
