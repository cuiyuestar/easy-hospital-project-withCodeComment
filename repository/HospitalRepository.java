package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.HospitalMapper;
import com.easy.hospital.dao.model.Hospital;
import com.easy.hospital.dto.HospitalListReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HospitalRepository extends ServiceImpl<HospitalMapper, Hospital> {
    public List<Hospital> listOnCondition(HospitalListReq req) {
        QueryWrapper<Hospital> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNoneBlank(req.getName()), "hospital_name", req.getName());
        wrapper.eq(req.getStatus() != null, "status", req.getStatus());
        return list(wrapper);
    }

}
