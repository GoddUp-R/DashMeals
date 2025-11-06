package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @PostMapping
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result save(@RequestBody DishDTO dishDTO) {
        dishService.saveFlavorAndDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageResult dishVO = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(dishVO);
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> listById(@PathVariable("id") Long id) {
        DishVO dishVO = dishService.listById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @Operation(summary = "修改菜品")
    @PutMapping
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateFlavorAndDish(dishDTO);
        return Result.success();
    }


    /**
     * 菜品的起售和停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);
        return Result.success();
    }


     /**
      * 批量删除菜品
      * @param ids
      * @return
      */
    @Operation(summary = "批量删除菜品")
    @DeleteMapping
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        dishService.delete(ids);
        return Result.success();
    }
     /**
      * 根据分类id查询菜品
      * @param categoryId
      * @return
      */
    @Operation(summary = "根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> dishList = dishService.listByCategoryId(categoryId);
        return Result.success(dishList);
    }

}
