package com.easy.hospital.service;

import com.easy.hospital.dao.model.Hospital;
import com.easy.hospital.dto.HospitalListReq;
import com.easy.hospital.dto.HospitalWXListReq;
import com.easy.hospital.dto.RecommendHospitalVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface HospitalService {
    PageInfo<Hospital> list(HospitalListReq req);

    void saveOrUpdate(Hospital hospital);

    void deleteLogic(Long id);

    List<RecommendHospitalVO> recommendHospital();

    PageInfo<RecommendHospitalVO> listHospitalWX(HospitalWXListReq req);

    RecommendHospitalVO doctorDeatil(Long id);
}
