package com.easy.hospital.service;

import com.easy.hospital.dto.DoctorScheduleDTO;
import com.easy.hospital.dto.DoctorScheduleListReq;
import com.easy.hospital.dto.DoctorScheduleVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DoctorScheduleService {
    List<DoctorScheduleVO> getSchedule(DoctorScheduleDTO dto);

    PageInfo<DoctorScheduleVO> list(DoctorScheduleListReq req);

    void saveOrUpdate(DoctorScheduleDTO dto);

    void delete(DoctorScheduleDTO dto);

    DoctorScheduleVO getDetail(Long id);
}
