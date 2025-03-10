package com.easy.hospital.service;

import com.easy.hospital.dto.CommentDTO;
import com.easy.hospital.dto.CommentVO;
import com.github.pagehelper.PageInfo;

public interface CommentService {
    void addDoctorComment(CommentDTO dto);

    PageInfo<CommentVO> listDoctorCommentPage(Long doctorId, Integer pageNum, Integer pageSize);

    CommentVO doctorCommentDetail(Long id);
}
