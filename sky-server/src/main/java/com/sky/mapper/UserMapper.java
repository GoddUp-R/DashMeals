package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    //根据openid查询用户是否存在
    User selectByOpenid(String openid);
    //插入用户
    void insert(User user);

    //根据时间查询用户数量
    Integer queryUserCountByDate(LocalDateTime begin, LocalDateTime endTime);
}
