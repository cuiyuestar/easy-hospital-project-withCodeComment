package com.easy.hospital.service.impl;

import com.easy.hospital.dao.mapper.DepartmentMapper;
import com.easy.hospital.dao.model.Department;
import com.easy.hospital.dao.repository.DepartmentRepository;
import com.easy.hospital.dto.DepartmentListReq;
import com.easy.hospital.dto.DepartmentVO;
import com.easy.hospital.service.DepartmentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Resource
    private DepartmentRepository departmentRepository;
    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 分页展示科室列表
     * @param req
     * @return
     */
    @Override
    public PageInfo<DepartmentVO> list(DepartmentListReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<DepartmentVO> departments = departmentMapper.listOnCondition(req);
        return new PageInfo<>(departments);
    }

    /**
     * 添加或修改科室信息
     * @param department
     */
    @Override
    public void saveOrUpdate(Department department) {
        if (Objects.isNull(department.getId())){
            departmentRepository.save(department);
        } else {
            departmentRepository.updateById(department);
        }
    }

    @Override
    public void delete(Long id) {
        departmentRepository.removeById(id);
    }

    /**
     * 根据医院id和科室名称查询科室信息
     * @param hospitalId
     * @param departmentName
     * @return
     */
    @Override
    public List<DepartmentVO> listByHospitalIdAndDepartmentName(Long hospitalId, String departmentName) {
        DepartmentListReq departmentListReq = new DepartmentListReq();
        departmentListReq.setHospitalId(hospitalId);
        departmentListReq.setDepartmentName(departmentName);
        return departmentMapper.listOnCondition(departmentListReq);
    }
}
