package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sky.mapper.DishMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    public void saveFlavorAndDish(DishDTO dishDTO) {
        //对象拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //插入菜品
        dishMapper.insert(dish);
        //获取dish生成的id
        Long dishId = dish.getId();
        //获取口味列表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            //批量插入口味
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //对象拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishPageQueryDTO, dish);
        //查询菜品列表
        Page<DishVO> dishList = (Page<DishVO>)dishMapper.query(dish);

        PageResult pageResult = new PageResult(dishList.getTotal(), dishList);
        return pageResult;
    }





    //TODO 等做完update再做
     /**
      * 菜品的起售和停售
      * @param status
      * @param id
      */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }


    /**
     * 根据分类id查询菜品
     * @param Id
     * @return
     */
    @Override
    public DishVO listById(Long Id) {
        DishVO dishVO = dishMapper.queryById(Id);
        //查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.queryByDishId(dishVO.getId());
        dishVO.setFlavors(flavors);
        return dishVO;
    }


    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateFlavorAndDish(DishDTO dishDTO) {
        //对象拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //更新菜品
        dishMapper.update(dish);
        //删除原有的口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //获取口味列表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            //批量插入口味
            dishFlavorMapper.insertBatch(flavors);
        }
    }


     /**
      * 批量删除菜品
      * @param ids
      */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //删除菜品
        dishMapper.deleteBatch(ids);
        //删除菜品口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

     /**
      * 根据分类id查询菜品
      * @param categoryId
      * @return
      */
    @Override
    public List<Dish> listByCategoryId(Long categoryId) {
        return dishMapper.queryByCategoryId(categoryId);
    }


}
