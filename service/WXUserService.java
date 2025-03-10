package com.easy.hospital.service;

import com.easy.hospital.dao.model.WXUser;
import com.easy.hospital.dto.WXUserLoginDTO;
import com.easy.hospital.dto.WXUserUpdateDTO;

public interface WXUserService {
    WXUser wxLogin(WXUserLoginDTO dto);

    WXUser getInfo();

    String saveInfo(WXUserUpdateDTO dto);

    Integer getT();
}
