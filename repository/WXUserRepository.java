package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.WXUserMapper;
import com.easy.hospital.dao.model.WXUser;
import com.easy.hospital.dto.WXUserUpdateDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WXUserRepository extends ServiceImpl<WXUserMapper, WXUser> {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    public WXUser getByOpenid(String openid) {
        //lambdaQuery来自MybatisPlus，返回一个LambdaQueryWrapper对象，用于构建查询条件
        //eq方法是添加查询条件，将外部传入的openid作为查询条件添加给lambdaQuery方法
        //WXUser::getOpenid表示获取WXUser对象的openid属性
        //整个函数链的大致意思是：根据外部传入的openid从数据库查询WXUser对象，类似于select * from wx_user where open_id = openid
        //.one()指返回查到的第一条记录
        return lambdaQuery().eq(WXUser::getOpenid, openid).one();
    }

    /**
     * 更新用户信息
     * @param dto
     * @return
     */
    public boolean updateByOpenId(WXUserUpdateDTO dto) {
        LambdaUpdateWrapper<WXUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WXUser::getOpenid,dto.getOpenid())
                .set(WXUser::getName,dto.getName())
                .set(WXUser::getPhone,dto.getPhone())
                .set(WXUser::getGender,dto.getGender())
                .set(WXUser::getIdentityId,dto.getIdentityId());
        return update(wrapper);
    }

    /**
     * 根据用户名查询用户
     * @param patientName
     * @return
     */
    public List<WXUser> listOnCondition(String patientName) {
        //QueryWrapper类用于构建查询条件，这里创建wrapper对象封装查询条件
        QueryWrapper<WXUser> wrapper = new QueryWrapper<>();
        //.like表示添加一个模糊查询条件，如果patientName不为空，则将其模糊查询的条件传入，查询的是WXUser的name字段
        wrapper.like(StringUtils.isNoneBlank(patientName), "name", patientName);
        //将查到的WXUser对象以列表的形式返回（因为根据名字查到的用户大概率不止一个）
        return list(wrapper);
    }
}
