package com.easy.hospital.service;

import com.easy.hospital.dto.*;
import com.github.pagehelper.PageInfo;

public interface WithdrawService {
    PageInfo<WithdrawVO> list(WithdrawListReq req);

    void changeStatus(WithdrawDTO dto);

    PageInfo<WithdrawAdminVO> adminList(WithdrawAdminListReq req);
}
