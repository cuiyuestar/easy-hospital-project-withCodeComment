package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.PatientMapper;
import com.easy.hospital.dao.model.Patient;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepository extends ServiceImpl<PatientMapper, Patient> {
}
