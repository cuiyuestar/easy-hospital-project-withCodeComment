package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.common.enums.AppointmentCommonStatusEnum;
import com.easy.hospital.common.enums.AppointmentStatusEnum;
import com.easy.hospital.dao.mapper.AppointmentMapper;
import com.easy.hospital.dao.model.Appointment;
import com.easy.hospital.dto.AppointmentChangeCommentStatusReq;
import com.easy.hospital.dto.AppointmentChangeStatusReq;
import com.easy.hospital.dto.AppointmentListReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppointmentRepository extends ServiceImpl<AppointmentMapper, Appointment> {
    public boolean updateById(AppointmentChangeStatusReq req) {
        UpdateWrapper<Appointment> wrapper = new UpdateWrapper<>();
        wrapper.eq("appointment_id", req.getAppointmentId())
                .set("status", req.getStatus());
        return update(wrapper);
    }

    public Appointment getById(String appointmentId) {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("appointment_id", appointmentId);
        return getOne(wrapper);
    }

    public void updateById(AppointmentChangeCommentStatusReq req) {
        UpdateWrapper<Appointment> wrapper = new UpdateWrapper<>();
        wrapper.eq("appointment_id", req.getAppointmentId())
                .set("comment_status", req.getCommentStatus());
        update(wrapper);
    }

    public Integer getUncommentCount(String openid) {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid)
                .eq("comment_status", AppointmentCommonStatusEnum.UNCOMMENTED.getCode())
                .eq("status", AppointmentStatusEnum.COMPLETED.getCode());
        return (int) count(wrapper);
    }
}
