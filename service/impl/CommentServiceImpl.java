package com.easy.hospital.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.easy.hospital.dao.model.Comment;
import com.easy.hospital.dao.model.Doctor;
import com.easy.hospital.dao.repository.CommentRepository;
import com.easy.hospital.dao.repository.DoctorRepository;
import com.easy.hospital.dto.CommentDTO;
import com.easy.hospital.dto.CommentVO;
import com.easy.hospital.model.bo.*;
import com.easy.hospital.service.AccountService;
import com.easy.hospital.service.CommentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentRepository commentRepository;
    @Resource
    private DoctorRepository doctorRepository;
    @Autowired
    private AccountService accountService;

    /**
     * 添加医生评价（似乎需要消耗V币？？）
     * @param dto
     */
    @Override
    public void addDoctorComment(CommentDTO dto) {
        try {
            CallResponse callResponse = accountService.getV(new AccountGetVInputBO(
                    new BigInteger(1, dto.getOpenid().getBytes())
            ));
            log.info("链上getV返回:{}", callResponse);
            if (((BigInteger) callResponse.getReturnObject().getFirst()).intValue() == 0) {
                return;
            }
        } catch (Exception e) {
            log.error("链上获取v币数量失败");
            throw new RuntimeException("链上获取v币数量失败", e);
        }

        log.info("添加评论:{}", JSON.toJSON(dto));
        Comment comment = BeanUtil.copyProperties(dto, Comment.class);
        comment.setGmtCreated(new Date());
        comment.setGmtModified(new Date());
        commentRepository.save(comment);

        try {
            TransactionResponse transactionResponse = accountService.vote(new AccountVoteInputBO(
                    new BigInteger(1, comment.getOpenid().getBytes()),
                    BigInteger.valueOf(comment.getDoctorId()),
                    BigInteger.valueOf((long) (comment.getStar() * 20))
            ));
            log.info("链上vote返回:{}", transactionResponse);
        } catch (Exception e) {
            log.error("链上投票失败");
            throw new RuntimeException("链上投票失败", e);
        }

        // 医生星级发生变化
        //List<Comment> comments = commentRepository.listByDoctorId(dto.getDoctorId());
        //double star = comments.stream().mapToDouble(Comment::getStar).average().orElse(0);
        //Doctor doctor = new Doctor();
        //doctor.setId(dto.getDoctorId());
        //doctor.setStar(star);
        try {
            CallResponse callResponse = accountService.getDoctorScore(new AccountGetDoctorScoreInputBO(
                    BigInteger.valueOf(comment.getDoctorId())
            ));
            log.info("链上getDoctorScore返回:{}", callResponse);
            Doctor doctor = new Doctor();
            doctor.setId(dto.getDoctorId());
            doctor.setStar(((BigInteger) callResponse.getReturnObject().getFirst()).doubleValue() / 20);
            doctorRepository.updateById(doctor);
        } catch (Exception e) {
            log.error("链上获取评分失败");
            throw new RuntimeException("链上获取评分失败", e);
        }
    }

    /**
     * 分页展示医生评价
     * @param doctorId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<CommentVO> listDoctorCommentPage(Long doctorId, Integer pageNum, Integer pageSize) {
        log.info("分页查询评论:{}", doctorId);
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> comments = commentRepository.listByDoctorId(doctorId);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        List<CommentVO> vos = comments.stream().map(comment -> BeanUtil.copyProperties(comment, CommentVO.class)).toList();
        PageInfo<CommentVO> resultInfo = new PageInfo<>();
        resultInfo.setList(vos);
        resultInfo.setTotal(pageInfo.getTotal());
        return resultInfo;
    }

    /**
     * 医生评价详情
     * @param id
     * @return
     */
    @Override
    public CommentVO doctorCommentDetail(Long id) {
        return BeanUtil.copyProperties(commentRepository.getById(id), CommentVO.class);
    }
}
