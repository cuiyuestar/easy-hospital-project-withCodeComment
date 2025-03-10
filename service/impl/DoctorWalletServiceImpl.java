package com.easy.hospital.service.impl;

import com.easy.hospital.common.enums.WithdrawStatusEnum;
import com.easy.hospital.dao.model.DoctorWallet;
import com.easy.hospital.dao.model.Withdraw;
import com.easy.hospital.dao.repository.DoctorRepository;
import com.easy.hospital.dao.repository.DoctorWalletRepository;
import com.easy.hospital.dao.repository.WithdrawRepository;
import com.easy.hospital.dto.DoctorWalletVO;
import com.easy.hospital.dto.WithdrawDTO;
import com.easy.hospital.service.DoctorWalletService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class DoctorWalletServiceImpl implements DoctorWalletService {
    @Resource
    private DoctorWalletRepository doctorWalletRepository;
    @Autowired
    private WithdrawRepository withdrawRepository;

    /**
     * 获取医生钱包信息
     * @param doctorId
     * @return
     */
    @Override
    public DoctorWalletVO getWalletInfo(Long doctorId) {
        DoctorWallet doctorWallet = doctorWalletRepository.getByDoctorId(doctorId);
        return new DoctorWalletVO()
                .setMoney(doctorWallet.getMoney())
                .setDoctorId(doctorWallet.getDoctorId())
                .setId(doctorWallet.getId());
    }

    /**
     * 医生提款
     * @param dto
     */
    @Override
    public void withdraw(WithdrawDTO dto) {
        //从dto获取医生id来从数据库获取医生钱包数据，并.getMoney()得知余额
        if (doctorWalletRepository.getByDoctorId(dto.getDoctorId()).getMoney() < dto.getAmount()){
            log.info("医生:{},余额不足",dto.getDoctorId());
            return;
        }
        Withdraw withdraw = new Withdraw();
        withdraw.setDoctorId(dto.getDoctorId())
                .setDoctorWalletId(dto.getDoctorWalletId())
                .setAmount(dto.getAmount())
                .setStatus(WithdrawStatusEnum.PENDING.getCode())
                .setTime(new Date());
        withdrawRepository.save(withdraw);
        doctorWalletRepository.updateMoneyByDoctorId(withdraw.getDoctorId(), -Math.toIntExact(withdraw.getAmount()));
    }
}
