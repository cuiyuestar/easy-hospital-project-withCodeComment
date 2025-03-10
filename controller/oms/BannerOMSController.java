package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dao.model.HomeBanner;
import com.easy.hospital.dto.HomeBannerSaveDTO;
import com.easy.hospital.service.HomeBannerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序首页轮播图控制器
 */
@RestController
@RequestMapping("/api/easy-online-hospital/banner")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class BannerOMSController {
    private final HomeBannerService homeBannerService;

    /**
     * 查询小程序首页轮播图
     * @return
     */
    @GetMapping("/list")
    public RespResult<List<HomeBanner>> listBanner(){
        return RespUtils.success(homeBannerService.getHomeBanner());
    }

    /**
     * 保存小程序首页轮播图
     * @param dto
     * @return
     */
    @PostMapping("/save")
    public RespResult<Void> saveBanner(@RequestBody HomeBannerSaveDTO dto){
        homeBannerService.saveOrUpdate(dto);
        return RespUtils.success();
    }
}
