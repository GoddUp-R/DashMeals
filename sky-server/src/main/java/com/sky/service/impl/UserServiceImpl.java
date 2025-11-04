package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    WeChatProperties weChatProperties;
    @Resource
    UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //调用微信接口，获取session_key和openid
        String json = getRequest(userLoginDTO.getCode());


        //解析json字符串，获取openid
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        //判断openid是否存在
        if(openid == null){
            throw new RuntimeException("openid为空");
        }

        //根据openid查询用户是否存在
        User user = userMapper.selectByOpenid(openid);
        //如果用户不存在，则创建新用户
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户信息
        return user;
    }


    public String getRequest(String code){
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session",paramMap);
        return json;
    }
}
