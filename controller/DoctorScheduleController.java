package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.DoctorScheduleDTO;
import com.easy.hospital.dto.DoctorScheduleVO;
import com.easy.hospital.service.DoctorScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序端-医生排班信息
 */
@RestController
@RequestMapping("/api/wx/schedule")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DoctorScheduleController {
    private final DoctorScheduleService doctorScheduleService;

    /**
     * 查询医生排班信息
     * @param dto
     * @return
     */
    @PostMapping("/getSchedule")
    public RespResult<List<DoctorScheduleVO>> getSchedule(@RequestBody DoctorScheduleDTO dto){
        return RespUtils.success(doctorScheduleService.getSchedule(dto));
    }
}
