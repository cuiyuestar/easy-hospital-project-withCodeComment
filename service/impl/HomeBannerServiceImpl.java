package com.easy.hospital.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.easy.hospital.common.response.RespSystemCode;
import com.easy.hospital.common.response.ServiceException;
import com.easy.hospital.dao.model.HomeBanner;
import com.easy.hospital.dao.repository.HomeBannerRepository;
import com.easy.hospital.dto.HomeBannerSaveDTO;
import com.easy.hospital.service.HomeBannerService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HomeBannerServiceImpl implements HomeBannerService {
    @Resource
    private HomeBannerRepository homeBannerRepository;

    /**
     * 获取首页轮播图
     * @return
     */
    @Override
    public List<HomeBanner> getHomeBanner() {
        List<HomeBanner> homeBanners = homeBannerRepository.list();
        if (CollectionUtil.isEmpty(homeBanners)){ //如果为空则返回空列表
            return Collections.emptyList();
        }
        //最多返回前3个轮播图
        return homeBanners.subList(0, Math.min(homeBanners.size(), 3));
    }

    /**
     * 添加或修改轮播图
     * @param dto
     */
    @Override
    @Transactional
    public void saveOrUpdate(HomeBannerSaveDTO dto) {
        homeBannerRepository.remove(null); //应该是清理掉数据库中的空值？
        List<String> imageUrls = dto.getImageUrls(); //获取若干张轮播图的地址，存入列表
        if (CollectionUtil.isEmpty(imageUrls)){ //若列表为空则抛出异常
            throw new ServiceException(RespSystemCode.PARAM_ERROR, "轮播图不能为空");
        }
        //遍历轮播图地址列表，为每个地址创建一个轮播图对象（也可理解为将每个地址分别封装到一个轮播图对象里）
        //并将各个轮播图对象依次存入数据库
        for (String imageUrl : imageUrls) {
            HomeBanner homeBanner = new HomeBanner().setImageUrl(imageUrl);
            homeBannerRepository.saveOrUpdate(homeBanner);
        }
        log.info("轮播图保存成功:{}", JSON.toJSON(dto));
    }
}
