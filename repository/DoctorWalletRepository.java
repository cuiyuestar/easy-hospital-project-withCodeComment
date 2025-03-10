package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.DoctorWalletMapper;
import com.easy.hospital.dao.model.DoctorWallet;
import org.springframework.stereotype.Repository;

@Repository
public class DoctorWalletRepository extends ServiceImpl<DoctorWalletMapper, DoctorWallet> {

    /**
     * 根据DoctorId更新医生所得金额
     *
     * @param doctorId
     * @param amount
     */
    public synchronized void updateMoneyByDoctorId(Long doctorId, Integer amount) {
        DoctorWallet doctorWallet = lambdaQuery().eq(DoctorWallet::getDoctorId, doctorId).one();
        doctorWallet.setMoney(doctorWallet.getMoney() + amount);
        updateById(doctorWallet);
    }

    public DoctorWallet getByDoctorId(Long doctorId) {
        return lambdaQuery().eq(DoctorWallet::getDoctorId, doctorId).one();
    }
}
