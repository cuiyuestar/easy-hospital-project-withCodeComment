package com.easy.hospital.service.impl;

import com.easy.hospital.common.enums.WithdrawStatusEnum;
import com.easy.hospital.dao.mapper.WithdrawMapper;
import com.easy.hospital.dao.model.Withdraw;
import com.easy.hospital.dao.repository.DoctorWalletRepository;
import com.easy.hospital.dao.repository.WithdrawRepository;
import com.easy.hospital.dto.*;
import com.easy.hospital.service.WithdrawService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    private final WithdrawRepository withdrawRepository;
    private final DoctorWalletRepository doctorWalletRepository;
    private final WithdrawMapper withdrawMapper;

    public WithdrawServiceImpl(WithdrawRepository withdrawRepository, DoctorWalletRepository doctorWalletRepository, WithdrawMapper withdrawMapper) {
        this.withdrawRepository = withdrawRepository;
        this.doctorWalletRepository = doctorWalletRepository;
        this.withdrawMapper = withdrawMapper;
    }

    /**
     * 医生取款列表
     * @param req
     * @return
     */
    @Override
    public PageInfo<WithdrawVO> list(WithdrawListReq req) {
        PageHelper.startPage(req.getPage(),req.getPageSize());
        List<Withdraw> withdraws = withdrawRepository.listOnCondition(req);
        PageInfo<Withdraw> pageInfo = new PageInfo<>(withdraws);
        List<WithdrawVO> list = withdraws.stream().map(WithdrawVO::of).toList();
        PageInfo<WithdrawVO> resInfo = new PageInfo<>();
        resInfo.setTotal(pageInfo.getTotal());
        resInfo.setList(list);
        return resInfo;
    }

    /**
     * 修改取款状态（检测到“取消提款”状态就将钱返还到医生的虚拟钱包“DoctorWallet”中）
     * @param dto
     */
    @Override
    public void changeStatus(WithdrawDTO dto) {
        if (Objects.equals(dto.getStatus(), WithdrawStatusEnum.CANCELLED.getCode())){
            doctorWalletRepository.updateMoneyByDoctorId(dto.getDoctorId(), Math.toIntExact(dto.getAmount()));
        }
        withdrawRepository.updateStatusById(dto);
    }

    /**
     * 分页查询取款记录？？
     * @param req
     * @return
     */
    @Override
    public PageInfo<WithdrawAdminVO> adminList(WithdrawAdminListReq req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<WithdrawAdminVO> list = withdrawMapper.adminList();
        return new PageInfo<>(list);
    }
}
