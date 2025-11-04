package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {
    public List<DishVO> query(Dish dish);

    public DishVO queryById(Long id);

     /**
      * 根据分类id查询菜品
      * @param categoryId
      * @return
      */
    public List<Dish> queryByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    public void insert(Dish dish);
    /**
     * 更新菜品
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    List<DishItemVO> queryDishItemBySetmealId(Long id);
}
