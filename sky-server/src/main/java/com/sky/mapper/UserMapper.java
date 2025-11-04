package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    //根据openid查询用户是否存在
    User selectByOpenid(String openid);
    //插入用户
    void insert(User user);
}
