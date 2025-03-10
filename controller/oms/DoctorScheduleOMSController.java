package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.DoctorScheduleDTO;
import com.easy.hospital.dto.DoctorScheduleListReq;
import com.easy.hospital.dto.DoctorScheduleVO;
import com.easy.hospital.service.DoctorScheduleService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/easy-online-hospital/schedule")
public class DoctorScheduleOMSController {
    @Resource
    DoctorScheduleService doctorScheduleService;

    /**
     * 获取排班详情
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public RespResult<DoctorScheduleVO> getDetail(@RequestParam("id") Long id){
        return RespUtils.success(doctorScheduleService.getDetail(id));
    }

    /**
     * 分页查询排班表
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<DoctorScheduleVO>> list(@RequestBody DoctorScheduleListReq req){
        return RespUtils.success(doctorScheduleService.list(req));
    }

    /**
     * 创建或更新排班
     * @param dto
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public RespResult<Void> saveOrUpdate(@RequestBody DoctorScheduleDTO dto){
        doctorScheduleService.saveOrUpdate(dto);
        return RespUtils.success();
    }

    @PostMapping("/delete")
    public RespResult<Void> delete(@RequestBody DoctorScheduleDTO dto){
        doctorScheduleService.delete(dto);
        return RespUtils.success();
    }
}
