package com.easy.hospital.service;

import com.easy.hospital.dto.FavoriteDTO;
import com.easy.hospital.dto.FavoriteListReq;
import com.easy.hospital.dto.RecommendDoctorVO;
import com.github.pagehelper.PageInfo;

public interface FavoriteService {
    PageInfo<RecommendDoctorVO> listFavoriteDoctor(FavoriteListReq req);

    boolean addFavoriteDoctor(FavoriteDTO dto);

    boolean deleteFavoriteDoctor(FavoriteDTO dto);

    boolean queryIsFavoriteDoctor(FavoriteDTO dto);
}
