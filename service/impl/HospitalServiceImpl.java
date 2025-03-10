package com.easy.hospital.service.impl;

import com.easy.hospital.dao.model.Hospital;
import com.easy.hospital.dao.repository.HospitalRepository;
import com.easy.hospital.dto.HospitalListReq;
import com.easy.hospital.dto.HospitalWXListReq;
import com.easy.hospital.dto.RecommendHospitalVO;
import com.easy.hospital.service.HospitalService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HospitalServiceImpl implements HospitalService {
    @Resource
    private HospitalRepository hospitalRepository;

    /**
     * 分页展示医院
     * @param req
     * @return
     */
    @Override
    public PageInfo<Hospital> list(HospitalListReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<Hospital> hospitals = hospitalRepository.listOnCondition(req);
        return new PageInfo<>(hospitals);
    }

    @Override
    public void saveOrUpdate(Hospital hospital) {
        if (Objects.isNull(hospital.getId())){
            hospitalRepository.save(hospital);
        } else {
            hospitalRepository.updateById(hospital);
        }
    }

    @Override
    public void deleteLogic(Long id) {
        hospitalRepository.removeById(id);
    }

    /**
     * 推荐十家医院
     * @return
     */
    @Override
    public List<RecommendHospitalVO> recommendHospital() {
        //req对象封装了查询医院的条件，这里将req的状态字段设置为1，可能是要查询状态为1的医院
        HospitalListReq req = new HospitalListReq().setStatus(1);
        //根据req封装的条件，查询出所有状态为1的医院，装入hospitals列表
        List<Hospital> hospitals = hospitalRepository.listOnCondition(req);
        //截取hospitals列表的前10个元素（如果医院总数不足10，则返回医院总数）
        List<Hospital> subHospitals = hospitals.subList(0, Math.min(hospitals.size(), 10));
        return subHospitals.stream()
                //将hospitals列表中的每个医院对象转换为RecommendHospitalVO对象
                .map(RecommendHospitalVO::ofHospital)
                //将转换后的每个RecommendHospitalVO对象以此放入新的列表中
                .collect(Collectors.toList());
    }

    /**
     * 分页查询医院（用户端）
     * @param req
     * @return
     */
    @Override
    public PageInfo<RecommendHospitalVO> listHospitalWX(HospitalWXListReq req) {
        HospitalListReq listReq = new HospitalListReq()
                .setPageNum(req.getPage())
                .setPageSize(req.getPageSize())
                .setStatus(1)
                .setName(req.getSearch()); //传入搜索关键字
        PageHelper.startPage(listReq.getPageNum(), listReq.getPageSize());
        List<Hospital> hospitals = hospitalRepository.listOnCondition(listReq);
        PageInfo<Hospital> pageInfo = new PageInfo<>(hospitals);
        List<RecommendHospitalVO> result = hospitals.stream().map(RecommendHospitalVO::ofHospital).collect(Collectors.toList());
        PageInfo<RecommendHospitalVO> resultInfo = new PageInfo<>();
        resultInfo.setList(result);
        resultInfo.setTotal(pageInfo.getTotal());
        return resultInfo;
    }

    @Override
    public RecommendHospitalVO doctorDeatil(Long id) {
        return RecommendHospitalVO.ofHospital(hospitalRepository.getById(id));
    }
}
