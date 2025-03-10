package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dao.model.Hospital;
import com.easy.hospital.dto.HospitalListReq;
import com.easy.hospital.service.HospitalService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/easy-online-hospital/hospital")
public class HospitalOMSController {
    @Resource
    private HospitalService hospitalService;

    @PostMapping("/list")
    public RespResult<PageInfo<Hospital>> list(@RequestBody HospitalListReq req){
        return RespUtils.success(hospitalService.list(req));
    }

    @PostMapping("/saveOrUpdate")
    public RespResult<Void> saveOrUpdate(@RequestBody Hospital hospital){
        hospitalService.saveOrUpdate(hospital);
        return RespUtils.success();
    }

    @GetMapping("/delete")
    public RespResult<Void> delete(@RequestParam("id") Long id){
        hospitalService.deleteLogic(id);
        return RespUtils.success();
    }
}
