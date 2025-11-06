package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

     /**
      * 根据用户id查询购物车
      * @param userId
      * @return
      */
     @Select("select * from shopping_cart where user_id = #{userId} order by create_time desc")
    List<ShoppingCart> queryByUserId(Long userId);

     /**
      * 批量删除购物车
      * @param userId
      */
     @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteBatch(Long userId);
     /**
      * 根据id删除购物车
      * @param id
      */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
