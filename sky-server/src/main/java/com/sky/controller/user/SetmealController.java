package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
public class SetmealController {
    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;

    @Operation(summary = "根据分类id查询套餐")
    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<Setmeal>> list(@RequestParam(value = "categoryId") Long categoryId) {
        List<Setmeal> list = setmealService.listSetmeal(categoryId);
        return Result.success(list);
    }

    @Operation(summary = "根据id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> listDishItemById(@PathVariable(value = "id") Long id) {
        List<DishItemVO> list = dishService.listDishItemBySetmealId(id);
        return Result.success(list);
    }
}
