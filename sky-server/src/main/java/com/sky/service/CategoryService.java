package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
     PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);


     /**
      * 更新分类
      * @param categoryDTO
      */
     void update(CategoryDTO categoryDTO);

     /**
      * 启用禁用分类
      * @param status
      * @param id
      */
    void startOrStop(Integer status, Long id);


    void insert(CategoryDTO categoryDTO);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    void delete(Long id);
}
