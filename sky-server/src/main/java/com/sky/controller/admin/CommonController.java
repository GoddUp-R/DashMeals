package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliYunOssUtil;
import com.sun.net.httpserver.Authenticator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Resource
    private AliYunOssUtil aliYunOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String path = null;
        try {
            path = aliYunOssUtil.uploadFile(file.getBytes(), file.getOriginalFilename());
            return Result.success(path);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
