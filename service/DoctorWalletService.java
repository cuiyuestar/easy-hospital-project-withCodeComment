package com.easy.hospital.service;

import com.easy.hospital.dto.DoctorWalletVO;
import com.easy.hospital.dto.WithdrawDTO;

public interface DoctorWalletService {
    DoctorWalletVO getWalletInfo(Long doctorId);

    void withdraw(WithdrawDTO dto);
}
