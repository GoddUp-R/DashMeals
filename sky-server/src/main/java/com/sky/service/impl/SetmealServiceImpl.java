package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        Setmeal setmeal = new Setmeal();
        if((setmealPageQueryDTO.getName() != null) && (!setmealPageQueryDTO.getName().isEmpty())){
            setmeal.setName(setmealPageQueryDTO.getName());
        }
        if(setmealPageQueryDTO.getCategoryId() != null){
            setmeal.setCategoryId(Long.valueOf(setmealPageQueryDTO.getCategoryId()));
        }
        if(setmealPageQueryDTO.getStatus() != null){
            setmeal.setStatus(setmealPageQueryDTO.getStatus());
        }
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> list = (Page<Setmeal>)setmealMapper.query(setmeal);
        return new PageResult(list.getTotal(), list.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        // 新增套餐商品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //设置套餐商品的套餐id
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Transactional
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.queryById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        //查询套餐商品
        List<SetmealDish> setmealDishes = setmealDishMapper.queryBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //更新setmeal表
        setmealMapper.update(setmeal);
        //删除setmeal_dish表中的数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //新增setmeal_dish表中的数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 启动/停用套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

     /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        setmealMapper.deleteBatch(ids);
        //批量删除setmeal_dish表中的数据
        setmealDishMapper.deleteBySetmealIds(ids);
    }
}
