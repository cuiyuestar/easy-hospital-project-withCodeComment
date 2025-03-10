package com.easy.hospital.service;

import com.easy.hospital.dao.model.Doctor;
import com.easy.hospital.dto.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DoctorService {
    PageInfo<DoctorOMSVO> list(DoctorListReq req);

    void changeStatus(Long id, Integer status);

    Doctor detail(Long id);

    void deleted(Long id);

    void saveOrUpdate(DoctorSaveDTO doctor);

    String login(DoctorLoginReq req);

    List<RecommendDoctorVO> recommendDoctor();

    PageInfo<RecommendDoctorVO> listDoctor(DoctorWXListReq req);

    RecommendDoctorVO doctorDeatil(Long id);

    void register(DoctorRegisterDTO registerDTO);

    void resetPassword(Long doctorId);

    boolean updateForDoctor(DoctorUpdateDTO dto);

    void setPassword(SetPasswordReq req);

    Integer getT();
}
