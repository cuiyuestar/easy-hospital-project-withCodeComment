package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.DoctorMapper;
import com.easy.hospital.dao.model.Doctor;
import com.easy.hospital.dto.DoctorListReq;
import com.easy.hospital.dto.DoctorUpdateDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorRepository extends ServiceImpl<DoctorMapper, Doctor> {
    public List<Doctor> listOnCondition(DoctorListReq req) {
        QueryWrapper<Doctor> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNoneBlank(req.getHospitalName()), "hospital_name", req.getHospitalName());
        queryWrapper.like(StringUtils.isNoneBlank(req.getDepartmentName()), "department_name", req.getDepartmentName());
        queryWrapper.like(StringUtils.isNoneBlank(req.getDoctorName()), "doctor_name", req.getDoctorName());
        queryWrapper.eq("is_deleted", 0);
        return list(queryWrapper);
    }

    public void updateStatusById(Long id, Integer status) {
        update(new Doctor().setStatus(status), new QueryWrapper<Doctor>().eq("id", id));
    }

    public void deleteLogic(Long id) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setIsDeleted(1);
        updateById(doctor);
    }

    public Doctor getByPhoneAndPassword(String phone, String password) {
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.eq("password", password);
        return getOne(wrapper);
    }

    public List<Doctor> listAllDoctor() {
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
                .eq("is_deleted", 0);
        return list(wrapper);
    }

    public boolean updateById(DoctorUpdateDTO dto) {
        LambdaUpdateWrapper<Doctor> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Doctor::getId, dto.getId())
                .set(Doctor::getDoctorName, dto.getDoctorName())
                .set(Doctor::getDoctorIntroduction, dto.getDoctorIntroduction())
                .set(Doctor::getVerifyImage, dto.getVerifyImage())
                .set(Doctor::getImageUrl, dto.getImageUrl())
                .set(Doctor::getBank, dto.getBank())
                .set(Doctor::getBankCard, dto.getBankCard());
        return update(wrapper);
    }
}
