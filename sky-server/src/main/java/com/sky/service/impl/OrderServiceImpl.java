package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Resource
    private AddressBookMapper addressBookMapper;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private OrderMapper ordersMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //获取地址簿信息
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if(addressBook == null) {
            throw new AddressBookBusinessException("地址簿不存在");
        }
        //获取购物车信息,先获取用户id
        Long currentId = BaseContext.getCurrentId();

        //根据用户id查询购物车
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByUserId(currentId);
        if(shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException("购物车为空");
        }

        //根据地址簿信息和购物车信息创建订单
        Orders orders = new Orders();
        //设置订单属性
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //设置订单号
        orders.setNumber(UUID.randomUUID().toString().replace("-", ""));
        //设置订单状态（待付款）
        orders.setStatus(Orders.PENDING_PAYMENT);
        //设置支付状态（未支付）
        orders.setPayStatus(Orders.UN_PAID);
        //设置下单用户id
        orders.setUserId(currentId);
        //设置下单时间
        orders.setOrderTime(LocalDateTime.now());
        //设置地址
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        //插入订单
        ordersMapper.insert(orders);
        //获取订单id
        Long orderId = orders.getId();


        //创建订单明细列表,并插入
        List<OrderDetail> orderDetailList = new java.util.ArrayList<>();
        //遍历购物车并创建订单明细
        for(ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        }
        //批量插入订单明细
        orderDetailMapper.insertBatch(orderDetailList);

        //清除购物车信息
        shoppingCartMapper.deleteBatch(currentId);



        //返回订单提交成功VO
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orderId);
        orderSubmitVO.setOrderTime(orders.getOrderTime());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        return orderSubmitVO;
    }


    /**
     * 订单支付
     * @param ordersPaymentDTO
     */
    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        //修改订单的支付状态和订单状态
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        Orders orders = new Orders();
        orders.setNumber(orderNumber);
        orders.setPayStatus(Orders.PAID);
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        ordersMapper.update(orders);
    }
}
