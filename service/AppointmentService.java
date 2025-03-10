package com.easy.hospital.service;

import com.easy.hospital.dto.*;
import com.github.pagehelper.PageInfo;

public interface AppointmentService {
    AppointmentVO getPrice(AppointmentDTO dto);

    PageInfo<AppointmentDoctorVO> list(AppointmentListReq req);

    boolean changeStatus(AppointmentChangeStatusReq req);

    PageInfo<AppointmentWXUserVO> listWX(AppointmentWXUserListReq req);

    void changeCommentStatus(AppointmentChangeCommentStatusReq req);

    Integer getUncommentCount();
}
