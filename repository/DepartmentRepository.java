package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.DepartmentMapper;
import com.easy.hospital.dao.model.Department;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepository extends ServiceImpl<DepartmentMapper, Department> {
}
