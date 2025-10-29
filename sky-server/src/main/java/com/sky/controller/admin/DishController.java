package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.saveFlavorAndDish(dishDTO);
        return Result.success();
    }
}
