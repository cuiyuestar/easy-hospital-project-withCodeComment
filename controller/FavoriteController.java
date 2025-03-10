package com.easy.hospital.controller;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespSystemCode;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.FavoriteDTO;
import com.easy.hospital.dto.FavoriteListReq;
import com.easy.hospital.dto.RecommendDoctorVO;
import com.easy.hospital.service.FavoriteService;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wx/favorite")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class FavoriteController {
    private final FavoriteService favoriteService;

    /**
     * 分页查询收藏医生
     * @param req
     * @return
     */
    @PostMapping("/doctor/list")
    public RespResult<PageInfo<RecommendDoctorVO>> listFavoriteDoctor(@RequestBody FavoriteListReq req) {
        return RespUtils.success(favoriteService.listFavoriteDoctor(req));
    }

    /**
     * 添加收藏医生
     * @param dto
     * @return
     */
    @PostMapping("/doctor/add")
    public RespResult<Void> addFavoriteDoctor(@RequestBody FavoriteDTO dto) {
        return favoriteService.addFavoriteDoctor(dto) ?
                RespUtils.success() :
                RespUtils.fail(RespSystemCode.SUCCESS.getCode(), "添加失败");
    }

    /**
     * 删除收藏医生
     * @param dto
     * @return
     */
    @DeleteMapping("/doctor/delete")
    public RespResult<Void> deleteFavoriteDoctor(@RequestBody FavoriteDTO dto) {
        return favoriteService.deleteFavoriteDoctor(dto) ?
                RespUtils.success() :
                RespUtils.fail(RespSystemCode.SUCCESS.getCode(), "删除失败");
    }

    /**
     * 查看是否收藏成功
     * @param dto
     * @return
     */
    @PostMapping("/doctor/query")
    public RespResult<Boolean> queryIsFavoriteDoctor(@RequestBody FavoriteDTO dto) {
        return RespUtils.success(favoriteService.queryIsFavoriteDoctor(dto));
    }
}
