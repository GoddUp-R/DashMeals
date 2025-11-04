package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
public class CategoryController {
    @Resource
    CategoryService categoryService;

    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult page = categoryService.page(categoryPageQueryDTO);
        return Result.success(page);
    }

    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        System.out.println(categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status,@RequestParam("id") Long id) {
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    @PostMapping
    public Result insert(@RequestBody CategoryDTO categoryDTO) {
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(value = "type",required = false) Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

    @DeleteMapping
    public Result delete(@RequestParam("id") Long id) {
        categoryService.delete(id);
        return Result.success();
    }


}
