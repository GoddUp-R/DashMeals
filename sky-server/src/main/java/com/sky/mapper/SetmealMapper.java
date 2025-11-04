package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.constant.AutoFillConstant;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealMapper {
    List<SetmealVO> query(Setmeal setmeal);

    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    Setmeal queryById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

     /**
      * 批量删除套餐
      * @param ids
      */
    void deleteBatch(List<Long> ids);
}
