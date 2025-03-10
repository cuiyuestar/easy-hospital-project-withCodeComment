package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.WithdrawMapper;
import com.easy.hospital.dao.model.Withdraw;
import com.easy.hospital.dto.WithdrawDTO;
import com.easy.hospital.dto.WithdrawListReq;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WithdrawRepository extends ServiceImpl<WithdrawMapper, Withdraw> {
    public List<Withdraw> listOnCondition(WithdrawListReq req) {
        QueryWrapper<Withdraw> wrapper = new QueryWrapper<>();
        wrapper.eq(req.getDoctorId() != null, "doctor_id", req.getDoctorId())
                .eq(req.getDoctorWalletId() != null, "doctor_wallet_id", req.getDoctorWalletId())
                .orderByDesc("time");
        return list(wrapper);
    }

    public void updateStatusById(WithdrawDTO dto) {
        UpdateWrapper<Withdraw> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", dto.getId())
                .set("status", dto.getStatus());
        update(wrapper);
    }
}
