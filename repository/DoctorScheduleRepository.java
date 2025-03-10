package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.DoctorScheduleMapper;
import com.easy.hospital.dao.model.DoctorSchedule;
import com.easy.hospital.dto.DoctorScheduleListReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Repository
public class DoctorScheduleRepository extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> {
    public DoctorSchedule getByDateTimeAnDoctorId(String date, String time, Long id) {
        return lambdaQuery().eq(DoctorSchedule::getDoctorId, id)
                .eq(DoctorSchedule::getStartTime, time)
                .eq(DoctorSchedule::getDate, date)
                .one();
    }

    public void reduceAppointmentCount(Long doctorScheduleId) {
        lambdaUpdate().eq(DoctorSchedule::getId, doctorScheduleId)
                .setSql("available_slots = available_slots - 1")
                .update();
    }

    public void increaseAppointmentCount(Long doctorScheduleId) {
        lambdaUpdate().eq(DoctorSchedule::getId, doctorScheduleId)
                .setSql("available_slots = available_slots + 1")
                .update();
    }

    public List<DoctorSchedule> listByDoctorIdAndDate(Long doctorId, String date) {
        return lambdaQuery().eq(DoctorSchedule::getDoctorId, doctorId)
                .eq(date != null, DoctorSchedule::getDate, date)
                .eq(DoctorSchedule::getStatus, 1) //状态1可能是医生“正在工作”的状态
                .list();
    }

    public List<DoctorSchedule> listOnCondition(DoctorScheduleListReq req) {
        return lambdaQuery()
                .eq(DoctorSchedule::getDoctorId,req.getDoctorId())
                .eq(StringUtils.isNoneBlank(req.getDate()), DoctorSchedule::getDate, req.getDate())
                .eq(DoctorSchedule::getStatus, 1)
                .list();
    }

    public void deleteLogic(Long id) {
        lambdaUpdate().eq(DoctorSchedule::getId, id)
                .set(DoctorSchedule::getStatus, 0) //将医生状态置零，从逻辑上将其从日程表删除
                .update();
    }
}
