package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    public List<ShoppingCart> query(ShoppingCart shoppingCart);

    void update(ShoppingCart cartItem);

    /**
     * 新增购物车
     * @param shoppingCart
     */
     void insert(ShoppingCart shoppingCart);
}
