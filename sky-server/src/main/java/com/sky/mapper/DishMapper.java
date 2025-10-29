package com.sky.mapper;

import com.sky.entity.Category;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {
    public List<Dish> query(Dish dish);
}
