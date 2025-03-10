package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.DoctorWXListReq;
import com.easy.hospital.dto.RecommendDoctorVO;
import com.easy.hospital.service.DoctorService;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wx/doctor")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DoctorController {
    private final DoctorService doctorService;

    /**
     *推送10个医生
     * @return
     */
    @GetMapping("/recommend")
    public RespResult<List<RecommendDoctorVO>> recommendDoctor() {
        return RespUtils.success(doctorService.recommendDoctor());
    }


    /**
     * 分页查询医生
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<RecommendDoctorVO>> listDoctor(@RequestBody DoctorWXListReq req) {
        return RespUtils.success(doctorService.listDoctor(req));
    }

    /**
     * 获取医生详情
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public RespResult<RecommendDoctorVO> detail(@RequestParam("id") Long id) {
        return RespUtils.success(doctorService.doctorDeatil(id));
    }
}
