package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.DepartmentVO;
import com.easy.hospital.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序-科室控制器
 */
@RestController
@RequestMapping("/api/wx/department")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DepartmentController {
    private final DepartmentService departmentService;

    /**
     * 分页查询科室列表
     * @param hospitalId
     * @param departmentName
     * @return
     */
    @GetMapping("/list")
    public RespResult<List<DepartmentVO>> listDepartment(@RequestParam(value = "hospitalId") Long hospitalId,
                                                         @RequestParam(value = "departmentName", required = false) String departmentName){
        return RespUtils.success(departmentService.listByHospitalIdAndDepartmentName(hospitalId, departmentName));
    }
}
