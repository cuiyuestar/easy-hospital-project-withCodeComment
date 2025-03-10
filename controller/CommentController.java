package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.CommentDTO;
import com.easy.hospital.dto.CommentVO;
import com.easy.hospital.service.CommentService;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 评论接口
 */
@Slf4j
@RestController
@RequestMapping("/api/wx/comment")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CommentController {
    private final CommentService commentService;

    /**
     * 添加评论
     * @param dto
     * @return
     */
    @PostMapping("/doctor/add")
    public RespResult<Void> addDoctorComment(@RequestBody CommentDTO dto) {
        commentService.addDoctorComment(dto);
        return RespUtils.success();
    }

    /**
     * 分页查询医生评论
     * @param doctorId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/doctor/page")
    public RespResult<PageInfo<CommentVO>> listDoctorCommentPage(@RequestParam("doctorId") Long doctorId,
                                                             @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                             @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        pageNum = Optional.ofNullable(pageNum).orElse(1);
        pageSize = Optional.ofNullable(pageSize).orElse(10);
        return RespUtils.success(commentService.listDoctorCommentPage(doctorId, pageNum, pageSize));
    }

    /**
     * 评论详情
     * @param id
     * @return
     */
    @GetMapping("/doctor/detail")
    public RespResult<CommentVO> detail(@RequestParam("id") Long id) {
        return RespUtils.success(commentService.doctorCommentDetail(id));
    }
}
