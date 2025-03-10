package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.FavoriteMapper;
import com.easy.hospital.dao.model.Favorite;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepository extends ServiceImpl<FavoriteMapper, Favorite> {
    public List<Favorite> listOnOpenid(String openid) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    public boolean removeOnCondition(String openid, String doctorId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        queryWrapper.eq("doctor_id",doctorId);
        return remove(queryWrapper);
    }

    public Favorite getOnCondition(String openid, String doctorId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        queryWrapper.eq("doctor_id",doctorId);
        return getOne(queryWrapper);
    }
}
