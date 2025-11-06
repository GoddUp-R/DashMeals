package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import java.util.List;

@Api(tags = "菜品浏览接口")
@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Operation(summary = "根据分类id查询菜品")
    @GetMapping("/list")
    @Cacheable(cacheNames = "dishCache", key = "#categoryId")
    public Result<List<DishVO>> list(@RequestParam(value = "categoryId",required = false) Long categoryId) {
        List<DishVO> list = dishService.listDishAndFlavor(categoryId);
        return Result.success(list);
    }
}
