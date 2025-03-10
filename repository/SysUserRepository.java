package com.easy.hospital.dao.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.hospital.dao.mapper.SysUserMapper;
import com.easy.hospital.dao.model.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public class SysUserRepository extends ServiceImpl<SysUserMapper, SysUser> {

    public SysUser getByUsernameAndPassword(String username, String password) {
        return lambdaQuery().eq(SysUser::getUsername, username)
                .eq(SysUser::getPassword, password).one();
    }
}
