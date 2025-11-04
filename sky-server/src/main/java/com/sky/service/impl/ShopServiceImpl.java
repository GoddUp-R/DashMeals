package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ShopServiceImpl implements ShopService {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void setStatus(Integer status) {
        // 1. 校验参数是否合法
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("店铺状态参数错误");
        }
        // 2. 更新店铺状态
        redisTemplate.opsForValue().set("shopStatus", status);
    }

    @Override
    public Integer getStatus() {
        // 1. 从Redis中获取店铺状态
        Integer status = (Integer) redisTemplate.opsForValue().get("shopStatus");
        // 2. 如果Redis中没有店铺状态，则默认返回0（表示店铺关闭）
        return status != null ? status : 0;
    }

}
