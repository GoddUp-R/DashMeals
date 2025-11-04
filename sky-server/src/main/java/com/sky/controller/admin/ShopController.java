package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    /**
     * 设置店铺状态
     */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        shopService.setStatus(status);
        return Result.success();
    }

     /**
      * 获取店铺状态
      */
     @GetMapping("/status")
     public Result<Integer> getStatus() {
         Integer status = shopService.getStatus();
         return Result.success(status);
     }

}
