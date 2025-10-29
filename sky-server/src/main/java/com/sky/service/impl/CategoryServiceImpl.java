package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.stereotype.Service;
import com.sky.mapper.CategoryMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> records = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = records.getTotal();
        return new PageResult(total, records);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void insert(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser(BaseContext.getCurrentId())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.insert(category);
    }

    @Override
    public List<Category> list(Integer type) {
        CategoryPageQueryDTO categoryPageQueryDTO = new CategoryPageQueryDTO();
        categoryPageQueryDTO.setType(type);
        return categoryMapper.pageQuery(categoryPageQueryDTO);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 检查分类是否关联了菜品
        List<Dish> dishList = dishMapper.query(Dish.builder().categoryId(id).build());
        if (!dishList.isEmpty()) {
            throw new DeletionNotAllowedException("分类关联了菜品，不能删除");
        }

        // 检查分类是否关联了套餐
        List<Setmeal> setmealList = setmealMapper.query(Setmeal.builder().categoryId(id).build());
        if (!setmealList.isEmpty()) {
            throw new DeletionNotAllowedException("分类关联了套餐，不能删除");
        }

        // 删除分类
        categoryMapper.delete(id);
    }
}
