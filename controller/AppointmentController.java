package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dto.*;
import com.easy.hospital.service.AppointmentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/wx/appointment")
public class AppointmentController {
    @Resource
    private AppointmentService appointmentService;

    /**
     * 预约挂号查询价格
     * @param dto
     * @return
     */
    @PostMapping("/price")
    public RespResult<AppointmentVO> getPrice(@RequestBody AppointmentDTO dto){
        return RespUtils.success(appointmentService.getPrice(dto));
    }

    /**
     * 分页查询预约列表（用户端）
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<AppointmentWXUserVO>> list(@RequestBody AppointmentWXUserListReq req){
        return RespUtils.success(appointmentService.listWX(req));
    }

    /**
     * 修改预约状态
     * @param req
     * @return
     */
    @PostMapping("/changeCommentStatus")
    public RespResult<Void> changeCommentStatus(@RequestBody AppointmentChangeCommentStatusReq req){
        appointmentService.changeCommentStatus(req);
        return RespUtils.success();
    }

    @GetMapping("/getUncommentCount")
    public RespResult<Integer> getUncommentCount(){
        return RespUtils.success(appointmentService.getUncommentCount());
    }
}
