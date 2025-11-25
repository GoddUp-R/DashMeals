package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 更新订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 更新订单状态
     * @param orders
     */
    void updateStatus(Orders orders);

     /**
     * 根据用户id查询订单
     * @param currentId
     * @return
     */
     Page<Orders> queryByUserId(Long currentId);

      /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
     Orders queryById(Long id);

      /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> query(OrdersPageQueryDTO ordersPageQueryDTO);


    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> queryTimeoutOrders(Integer status, LocalDateTime orderTime);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     * @return
     */
     @Select("select * from orders where number = #{orderNumber}")
    Orders queryByNumber(String orderNumber);

    /**
     * 根据日期查询营业额统计

     * @return
     */
     @Select("select sum(amount) from orders where order_time like concat(#{date}, '%')")
    BigDecimal querySumAmountByDate(String date);


     /**
     * 根据日期查询订单数
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer queryOrderCountByDate(LocalDateTime beginTime, LocalDateTime endTime,Integer status);
}
