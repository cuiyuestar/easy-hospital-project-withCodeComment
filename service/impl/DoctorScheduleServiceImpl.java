package com.easy.hospital.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.model.Doctor;
import com.easy.hospital.dao.model.DoctorSchedule;
import com.easy.hospital.dao.repository.DoctorScheduleRepository;
import com.easy.hospital.dto.DoctorScheduleDTO;
import com.easy.hospital.dto.DoctorScheduleListReq;
import com.easy.hospital.dto.DoctorScheduleVO;
import com.easy.hospital.service.DoctorScheduleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {
    @Resource
    private DoctorScheduleRepository doctorScheduleRepository;

    /**
     * 获取排班表（用户端）
     * @param dto
     * @return
     */
    @Override
    public List<DoctorScheduleVO> getSchedule(DoctorScheduleDTO dto) {
        List<DoctorSchedule> doctorSchedules = doctorScheduleRepository.listByDoctorIdAndDate(dto.getDoctorId(), dto.getDate());
        return doctorSchedules.stream().map(DoctorScheduleVO::of).toList();
    }

    /**
     * 分页展示排班表（医生端）
     * @param req
     * @return
     */
    @Override
    public PageInfo<DoctorScheduleVO> list(DoctorScheduleListReq req) {
        String token = LoginUserHolder.getToken(); //ThreadLocl拿到token
        DecodedJWT jwt = JWTUtils.verify(token); //解析token
        String doctorId = jwt.getClaim("doctorId").asString();//拿到令牌中的doctorId
        Long doctorIdLong = Long.parseLong(doctorId);
        req.setDoctorId(doctorIdLong); //将从令牌拿到的doctorId传给请求对象
        PageHelper.startPage(req.getPage(), req.getPageSize());
        //这里查询医生排班表时，需要用上doctorId，因此才在上面将令牌中的doctorId传入req
        List<DoctorSchedule> doctorSchedules = doctorScheduleRepository.listOnCondition(req);
        PageInfo<DoctorSchedule> pageInfo = new PageInfo<>(doctorSchedules);
        List<DoctorScheduleVO> collect = doctorSchedules.stream().map(DoctorScheduleVO::of).toList();
        PageInfo<DoctorScheduleVO> resultInfo = new PageInfo<>();
        resultInfo.setList(collect);
        resultInfo.setTotal(pageInfo.getTotal());
        return resultInfo;
    }

    @Override
    public void saveOrUpdate(DoctorScheduleDTO dto) {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        Long doctorIdLong = Long.parseLong(doctorId);
        dto.setDoctorId(doctorIdLong);
        DoctorSchedule doctorSchedule = new DoctorSchedule();
        BeanUtil.copyProperties(dto, doctorSchedule);
        if (dto.getId() == null) {
            doctorSchedule.setAvailableSlots(doctorSchedule.getMaxPatients());
            doctorScheduleRepository.save(doctorSchedule);
        } else {
            doctorScheduleRepository.updateById(doctorSchedule);
        }
    }

    @Override
    public void delete(DoctorScheduleDTO dto) {
        doctorScheduleRepository.deleteLogic(dto.getId());
    }

    @Override
    public DoctorScheduleVO getDetail(Long id) {
        return DoctorScheduleVO.of(doctorScheduleRepository.getById(id));
    }
}
