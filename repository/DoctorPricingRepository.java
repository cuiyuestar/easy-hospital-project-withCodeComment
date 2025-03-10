package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.DoctorPricingMapper;
import com.easy.hospital.dao.model.DoctorPricing;
import org.springframework.stereotype.Repository;

@Repository
public class DoctorPricingRepository extends ServiceImpl<DoctorPricingMapper, DoctorPricing> {
    public DoctorPricing getPriceByDoctorIdAndDayOfWeekAndTime(Long id, int weekdayNumber, String time) {
        return lambdaQuery().eq(DoctorPricing::getDoctorId, id)
                .like(DoctorPricing::getDayOfWeek, weekdayNumber)
                .eq(DoctorPricing::getServiceType, 1)
                .le(DoctorPricing::getEndTime, time)
                .ge(DoctorPricing::getStartTime, time)
                .one();
    }
}
