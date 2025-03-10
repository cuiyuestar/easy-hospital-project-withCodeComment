package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.CommentMapper;
import com.easy.hospital.dao.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository extends ServiceImpl<CommentMapper, Comment> {
    public List<Comment> listByDoctorId(Long doctorId) {
        return lambdaQuery().eq(Comment::getDoctorId, doctorId).list();
    }
}
