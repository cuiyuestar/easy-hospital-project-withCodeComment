package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dao.model.Department;
import com.easy.hospital.dto.DepartmentListReq;
import com.easy.hospital.dto.DepartmentVO;
import com.easy.hospital.service.DepartmentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/easy-online-hospital/department")
public class DepartmentOMSController {
    @Resource
    private DepartmentService departmentService;

    /**
     * 分页查询部门列表
     * @param req
     * @return
     */
    @PostMapping("/list")
    public RespResult<PageInfo<DepartmentVO>> list(@RequestBody DepartmentListReq req) {
        return RespUtils.success(departmentService.list(req));
    }

    /**
     * 创建或更新部门信息
     * @param department
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public RespResult<Void> saveOrUpdate(@RequestBody Department department) {
        departmentService.saveOrUpdate(department);
        return RespUtils.success();
    }

    @GetMapping("/delete")
    public RespResult<Void> delete(@RequestParam("id") Long id) {
        departmentService.delete(id);
        return RespUtils.success();
    }
}
