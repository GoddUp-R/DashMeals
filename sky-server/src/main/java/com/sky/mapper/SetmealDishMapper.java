package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
        /**
         * 新增套餐商品
         * @param setmealDishes
         */
        void insertBatch(List<SetmealDish> setmealDishes);
        /**
         * 根据套餐id查询套餐商品
         * @param id
         * @return
         */
        List<SetmealDish> queryBySetmealId(Long id);

        /**
         * 根据套餐id删除套餐商品
         * @param id
         */
        void deleteBySetmealId(Long id);

         /**
          * 批量删除套餐商品
          * @param ids
          */
        void deleteBySetmealIds(List<Long> ids);
}
