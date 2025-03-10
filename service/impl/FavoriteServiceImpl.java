package com.easy.hospital.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.model.Favorite;
import com.easy.hospital.dao.repository.DoctorRepository;
import com.easy.hospital.dao.repository.FavoriteRepository;
import com.easy.hospital.dto.FavoriteDTO;
import com.easy.hospital.dto.FavoriteListReq;
import com.easy.hospital.dto.RecommendDoctorVO;
import com.easy.hospital.service.FavoriteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteRepository favoriteRepository;
    @Resource
    private DoctorRepository doctorRepository;

    /**
     * 分页查询收藏的医生
     * @param req
     * @return
     */
    @Override
    public PageInfo<RecommendDoctorVO> listFavoriteDoctor(FavoriteListReq req) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<Favorite> favorites = favoriteRepository.listOnOpenid(openid);
        PageInfo<Favorite> pageInfo = new PageInfo<>(favorites);
        List<RecommendDoctorVO> doctors = favorites.stream()
                .map(favorite -> doctorRepository.getById(favorite.getDoctorId()))
                .map(RecommendDoctorVO::ofDoctor)
                .toList();
        PageInfo<RecommendDoctorVO> resultInfo = new PageInfo<>();
        resultInfo.setTotal(pageInfo.getTotal());
        resultInfo.setList(doctors);
        return resultInfo;
    }

    @Override
    public boolean addFavoriteDoctor(FavoriteDTO dto) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        dto.setOpenid(openid); //将解码后的token重新放入dto
        log.info("添加收藏:{}", JSON.toJSON(dto));
        Favorite favorite = BeanUtil.copyProperties(dto, Favorite.class);
        favorite.setCreateTime(LocalDateTime.now());
        boolean result = favoriteRepository.save(favorite);
        if (result) {
            log.info("添加成功:{}", JSON.toJSON(dto));
        } else {
            log.info("添加失败:{}", JSON.toJSON(dto));
        }
        return result;
    }

    @Override
    public boolean deleteFavoriteDoctor(FavoriteDTO dto) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        dto.setOpenid(openid);
        log.info("删除收藏:{}", JSON.toJSON(dto));
        boolean result = favoriteRepository.removeOnCondition(openid, dto.getDoctorId());
        if (result) {
            log.info("删除成功:{}", JSON.toJSON(dto));
        } else {
            log.info("删除失败:{}", JSON.toJSON(dto));
        }
        return result;
    }

    /**
     * 查询医生是否被收藏
     * @param dto
     * @return
     */
    @Override
    public boolean queryIsFavoriteDoctor(FavoriteDTO dto) {
        String token = LoginUserHolder.getToken();
        String openid = JWTUtils.parseToken(token);
        //根据医生编号查询收藏夹，若不为空则收藏成功
        return favoriteRepository.getOnCondition(openid, dto.getDoctorId()) != null;
    }
}
