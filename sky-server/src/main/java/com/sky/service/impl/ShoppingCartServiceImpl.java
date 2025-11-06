package com.sky.service.impl;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    //自动注入购物车Mapper
    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;


    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        //将购物车DTO转换为购物车实体类
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
       //获取用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //判断购物车是否存在
        List<ShoppingCart> cart = shoppingCartMapper.query(shoppingCart);
        if(cart != null && cart.size() > 0) {
            //购物车存在则更新数量
            ShoppingCart cartItem = cart.get(0);
            cartItem.setNumber(cartItem.getNumber() + 1);
            shoppingCartMapper.update(cartItem);
        }else{
            //若购物车不存在则新增
            //获取dishId和SetmealId
            Long dishId = shoppingCart.getDishId();
            //判断是菜品还是套餐
            if(dishId != null){
                //获取菜品信息,并设置
                Dish dish = dishMapper.queryEntity(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            }else{
                //获取套餐信息,并设置
                Long setmealId = shoppingCart.getSetmealId();
                //获取套餐信息,并设置
                Setmeal setmeal = setmealMapper.queryById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }
            //将数量设置为1
            shoppingCart.setNumber(1);
            //设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            //新增购物车
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.queryByUserId(userId);
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteBatch(userId);
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //将购物车DTO转换为购物车实体类
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);
        //判断购物车是否存在
        List<ShoppingCart> cart = shoppingCartMapper.query(shoppingCart);
        if(cart != null && cart.size() > 0) {
            //购物车存在则更新数量
            ShoppingCart cartItem = cart.get(0);
            cartItem.setNumber(cartItem.getNumber() - 1);
            shoppingCartMapper.update(cartItem);
            //若数量为0,则删除购物车
            if(cartItem.getNumber() <= 0){
                shoppingCartMapper.deleteById(cartItem.getId());
            }
        }
    }
}
