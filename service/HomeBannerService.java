package com.easy.hospital.service;

import com.easy.hospital.dao.model.HomeBanner;
import com.easy.hospital.dto.HomeBannerSaveDTO;

import java.util.List;

public interface HomeBannerService {
    List<HomeBanner> getHomeBanner();

    void saveOrUpdate(HomeBannerSaveDTO dto);
}
