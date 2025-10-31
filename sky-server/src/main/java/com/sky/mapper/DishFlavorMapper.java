package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

     /**
      * 根据菜品id查询口味
      * @param dishId
      * @return
      */
     List<DishFlavor> queryByDishId(Long dishId);

     /**
      * 根据菜品id删除口味
      * @param id
      */
    void deleteByDishId(Long id);

    /**
     * 批量删除菜品口味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);
}
