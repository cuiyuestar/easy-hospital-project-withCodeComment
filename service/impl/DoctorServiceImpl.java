package com.easy.hospital.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.easy.hospital.common.holder.LoginUserHolder;
import com.easy.hospital.common.response.ServiceException;
import com.easy.hospital.common.utils.JWTUtils;
import com.easy.hospital.dao.mapper.DoctorMapper;
import com.easy.hospital.dao.model.Doctor;
import com.easy.hospital.dao.model.DoctorWallet;
import com.easy.hospital.dao.repository.DepartmentRepository;
import com.easy.hospital.dao.repository.DoctorRepository;
import com.easy.hospital.dao.repository.DoctorWalletRepository;
import com.easy.hospital.dao.repository.HospitalRepository;
import com.easy.hospital.dto.*;
import com.easy.hospital.model.bo.AccountAddDoctorInputBO;
import com.easy.hospital.model.bo.AccountGetTInputBO;
import com.easy.hospital.service.AccountService;
import com.easy.hospital.service.DoctorService;
import com.easy.hospital.service.DoctorWalletService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {
    @Resource
    private DoctorRepository doctorRepository;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepartmentRepository departmentRepository;
    @Resource
    private HospitalRepository hospitalRepository;
    @Autowired
    private AccountService accountService;

    private static final String SALT = "easy-online-hospital";
    @Autowired
    private DoctorWalletRepository doctorWalletRepository;

    /**
     * 分页查询医生（管理端）
     * @param req
     * @return
     */
    @Override
    public PageInfo<DoctorOMSVO> list(DoctorListReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<Doctor> doctors = doctorRepository.listOnCondition(req);
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctors);
        List<DoctorOMSVO> resList = BeanUtil.copyToList(pageInfo.getList(), DoctorOMSVO.class);
        PageInfo<DoctorOMSVO> info = new PageInfo<>();
        info.setList(resList);
        info.setTotal(pageInfo.getTotal());
        return info;
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        doctorRepository.updateStatusById(id, status);
    }

    @Override
    public Doctor detail(Long id) {
        return doctorRepository.getById(id);
    }

    @Override
    public void deleted(Long id) {
        doctorRepository.deleteLogic(id);
    }

    /**
     * 创建或更新医生账户数据
     * @param dto
     */
    @Override
    public void saveOrUpdate(DoctorSaveDTO dto) {
        Doctor doctor = new Doctor();
        BeanUtil.copyProperties(dto, doctor);
        //创建新的医生账户
        if (Objects.isNull(dto.getId())) { //如果无法从数据库查到医生账户，说明要创建新的医生账户
            doctor.setPassword(DigestUtils.md5Hex("123456" + SALT));
            doctorRepository.save(doctor);
            DoctorWallet doctorWallet = new DoctorWallet();
            doctorWallet.setDoctorId(doctor.getId());
            doctorWallet.setMoney(0);
            doctorWalletRepository.save(doctorWallet);
            try {
                TransactionResponse transactionResponse = accountService.addDoctor(new AccountAddDoctorInputBO(
                        doctor.getDoctorName(),
                        BigInteger.valueOf(doctor.getId()),
                        BigInteger.valueOf((long) (doctor.getStar() * 20)))
                );
                log.info("链上addDoctor返回：{}", transactionResponse);
            } catch (Exception e) {
                log.error("链上创建医生账号失败");
                throw new RuntimeException("链上创建医生账号失败", e);
            }
        } else {
            //更新医生账户信息
            doctorRepository.updateById(doctor);
        }
    }

    /**
     * 医生账号登陆
     * @param req
     * @return
     */
    @Override
    public String login(DoctorLoginReq req) {
        String phone = req.getPhone();
        String password = req.getPassword();
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
            throw new ServiceException("400", "用户名或密码不能为空");
        }
        Doctor doctor = doctorRepository.getByPhoneAndPassword(phone, DigestUtils.md5Hex(password + SALT));
        if (Objects.isNull(doctor)) {
            throw new ServiceException("400", "用户名或密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("doctorId", doctor.getId());
        map.put("phone", phone);
        map.put("password", DigestUtils.md5Hex(password + SALT));
        return JWTUtils.getToken(map, 7);
    }

    /**
     * 推荐10名医生
     * @return
     */
    @Override
    public List<RecommendDoctorVO> recommendDoctor() {
        List<Doctor> doctors = doctorRepository.listAllDoctor();
        if (CollectionUtil.isEmpty(doctors)) {
            return Collections.emptyList();
        }
        return doctors.subList(0, Math.min(doctors.size(), 10))
                .stream()
                .map(RecommendDoctorVO::ofDoctor)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询医生（用户端）
     * @param req
     * @return
     */
    @Override
    public PageInfo<RecommendDoctorVO> listDoctor(DoctorWXListReq req) {
        DoctorListReq listReq = new DoctorListReq()
                .setDoctorName(req.getSearch()) //传入搜索关键字
                .setPageNum(req.getPage())
                .setPageSize(req.getPageSize());
        PageHelper.startPage(listReq.getPageNum(), listReq.getPageSize());
        List<Doctor> doctors = doctorRepository.listOnCondition(listReq); //根据搜索条件查询医生
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctors); //将符合搜索条件的医生传入页表，便于分页展示
        List<RecommendDoctorVO> collect = doctors.stream().map(RecommendDoctorVO::ofDoctor).collect(Collectors.toList());
        PageInfo<RecommendDoctorVO> resultInfo = new PageInfo<>();
        resultInfo.setList(collect);
        resultInfo.setTotal(pageInfo.getTotal());
        return resultInfo;
    }

    /**
     * 获取医生详细信息
     * @param id
     * @return
     */
    @Override
    public RecommendDoctorVO doctorDeatil(Long id) {
        return RecommendDoctorVO.ofDoctor(doctorRepository.getById(id));
    }

    @Override
    public void register(DoctorRegisterDTO registerDTO) {
        Doctor doctor = new Doctor();
        BeanUtil.copyProperties(registerDTO, doctor);
        doctor.setPassword(DigestUtils.md5Hex(registerDTO.getPassword() + SALT));
        doctor.setStatus(0);
        doctorRepository.save(doctor);
    }

    @Override
    public void resetPassword(Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setPassword(DigestUtils.md5Hex("123456" + SALT));
        doctorRepository.updateById(doctor);
    }

    /**
     * 更新医生信息（管理端）
     * @param dto
     * @return
     */
    @Override
    public boolean updateForDoctor(DoctorUpdateDTO dto) {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        Long doctorIdLong = Long.parseLong(doctorId);
        dto.setId(doctorIdLong);
        return doctorRepository.updateById(dto);
    }

    @Override
    public void setPassword(SetPasswordReq req) {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        Long doctorIdLong = Long.parseLong(doctorId);
        Doctor doctor = new Doctor();
        doctor.setId(doctorIdLong);
        doctor.setPassword(DigestUtils.md5Hex(req.getPassword() + SALT));
        doctorRepository.updateById(doctor);
    }

    @Override
    public Integer getT() {
        String token = LoginUserHolder.getToken();
        DecodedJWT jwt = JWTUtils.verify(token);
        String doctorId = jwt.getClaim("doctorId").asString();
        long doctorIdLong = Long.parseLong(doctorId);
        try {
            CallResponse callResponse = accountService.getT(new AccountGetTInputBO(
                    BigInteger.valueOf(doctorIdLong)
            ));
            log.info("链上getT返回：{}", callResponse);
            return ((BigInteger) callResponse.getReturnObject().getFirst()).intValue();
        } catch (Exception e) {
            log.error("链上获取T币数失败");
            throw new RuntimeException("链上获取T币数失败", e);
        }
    }

    public static void main(String[] args) {
        String password = DigestUtils.md5Hex("123456" + SALT);
        System.out.println(password);
    }
}
