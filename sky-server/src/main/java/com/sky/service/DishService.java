package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void saveFlavorAndDish(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

     /**
      * 菜品的起售和停售
      * @param status
      * @param id
      */
    void startOrStop(Integer status, Long id);
     /**
      * 根据分类id查询菜品
      * @param Id
      * @return
      */
    DishVO listById(Long Id);

     /**
      * 修改菜品
      * @param dishDTO
      */
    void updateFlavorAndDish(DishDTO dishDTO);

     /**
      * 批量删除菜品
      * @param ids
      */
    void delete(List<Long> ids);

     /**
      * 根据分类id查询菜品
      * @param categoryId
      * @return
      */
    List<Dish> listByCategoryId(Long categoryId);
}
