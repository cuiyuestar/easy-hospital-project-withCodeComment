package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.dto.*;
import com.easy.hospital.service.WithdrawService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/easy-online-hospital/withdraw")
public class WithdrawOMSController {
    @Resource
    private WithdrawService withdrawService;

    /**
     * 分页查询提现记录（展示数据较少）
     * @param req
     * @return
     */
    @PostMapping("/doctor/list")
    public RespResult<PageInfo<WithdrawVO>> list(@RequestBody WithdrawListReq req){
        return RespUtils.success(withdrawService.list(req));
    }

    /**
     * 分页查询提现记录（管理端，展示的数据更全面——包括医生的银行信息）
     * @param req
     * @return
     */
    @PostMapping("/admin/list")
    public RespResult<PageInfo<WithdrawAdminVO>> adminList(@RequestBody WithdrawAdminListReq req){
        return RespUtils.success(withdrawService.adminList(req));
    }

    /**
     * 修改提现记录的状态
     * @param dto
     * @return
     */
    @PostMapping("/changeStatus")
    public RespResult<Void> changeStatus(@RequestBody WithdrawDTO dto){
        withdrawService.changeStatus(dto);
        return RespUtils.success();
    }
}
