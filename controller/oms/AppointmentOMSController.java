package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespSystemCode;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.AppointmentChangeStatusReq;
import com.easy.hospital.dto.AppointmentDoctorVO;
import com.easy.hospital.dto.AppointmentListReq;
import com.easy.hospital.service.AppointmentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/easy-online-hospital/appointment")
public class AppointmentOMSController {
    @Resource
    AppointmentService appointmentService;

    /**
     * 查看预约列表数据
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<AppointmentDoctorVO>> list(@RequestBody AppointmentListReq req) {
        return RespUtils.success(appointmentService.list(req));
    }

    /**
     * 修改预约状态
     * @param req
     * @return
     */
    @PostMapping("/changeStatus")
    public RespResult<Void> changeStatus(@RequestBody AppointmentChangeStatusReq req) {
        return appointmentService.changeStatus(req) ?
                RespUtils.success() :
                RespUtils.fail(RespSystemCode.REQUEST_FAIL);
    }
}
