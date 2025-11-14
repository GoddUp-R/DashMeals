package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        //设置电话
        orders.setPhone(addressBook.getPhone());
        //设置收货人
        orders.setConsignee(addressBook.getConsignee());


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
        //设置结账时间
        orders.setCheckoutTime(LocalDateTime.now());
        //更新订单信息
        ordersMapper.update(orders);
    }

    /**
     * 查询历史订单
     * @return
     */
    @Override
    @Transactional
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        //分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        //根据用户id查询历史订单
        Long currentId = BaseContext.getCurrentId();
        Page<Orders> ordersList = ordersMapper.queryByUserId(currentId);
        //创建Order的视图对象列表
        ArrayList<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            //查询订单详细信息
            List<OrderDetail> orderDishes = orderDetailMapper.queryOrderDetailByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDishes);
            orderVOList.add(orderVO);
        }

        //分页查询
        PageResult pageResult = new PageResult(ordersList.getTotal(),orderVOList);
        return pageResult;
    }


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        //根据订单id查询订单信息
        Orders orders = ordersMapper.queryById(id);
        if(orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        //创建OrderVO对象
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        //查询订单详细信息
        List<OrderDetail> orderDishes = orderDetailMapper.queryOrderDetailByOrderId(id);
        orderVO.setOrderDetailList(orderDishes);
        return orderVO;
    }

    @Override
    public void cancel(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.CANCELLED);
        ordersMapper.updateStatus(orders);
    }

    /**
     * 重复下单
     * @param id
     */
    @Override
    @Transactional
    public void repetition(Long id) {
        //获取之前的订单详细信息
        List<OrderDetail> orderDishes = orderDetailMapper.queryOrderDetailByOrderId(id);
        ArrayList<ShoppingCart> ShoppingCartList = new ArrayList<>();
        //遍历订单明细并创建购物车对象
        for(OrderDetail orderDetail : orderDishes) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            //设置购物车创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            ShoppingCartList.add(shoppingCart);
        }

        //删除之前的购物车信息
        shoppingCartMapper.deleteBatch(BaseContext.getCurrentId());
        //批量插入购物车
        shoppingCartMapper.insertBatch(ShoppingCartList);
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersList = ordersMapper.query(ordersPageQueryDTO);
        return new PageResult(ordersList.getTotal(),ordersList.getResult());
    }

    @Transactional
    @Override
    public OrderStatisticsVO statistics() {
        //根据用户id查询订单统计信息
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        Page<Orders> query = ordersMapper.query(ordersPageQueryDTO);
        int toBeConfirmed = 0;
        int confirmed = 0;
        int deliveryInProgress = 0;
        for (Orders orders : query) {
            if(orders.getStatus() == Orders.TO_BE_CONFIRMED) {
                toBeConfirmed++;
            } else if(orders.getStatus() == Orders.CONFIRMED) {
                confirmed++;
            } else if(orders.getStatus() == Orders.DELIVERY_IN_PROGRESS) {
                deliveryInProgress++;
            }
        }
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);


        return orderStatisticsVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        //根据订单id查询订单信息
        Orders orders = ordersMapper.queryById(ordersConfirmDTO.getId());
        if(orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        //更新订单状态
        orders.setStatus(Orders.CONFIRMED);
        ordersMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //根据订单id查询订单信息
        Orders orders = ordersMapper.queryById(ordersRejectionDTO.getId());
        if(orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        //更新订单状态
        orders.setStatus(Orders.CANCELLED);
        //设置拒绝原因
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        ordersMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //根据订单id查询订单信息
        Orders orders = ordersMapper.queryById(id);
        if(orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        //更新订单状态
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        ordersMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        //根据订单id查询订单信息
        Orders orders = ordersMapper.queryById(id);
        if(orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        //更新订单状态
        orders.setStatus(Orders.COMPLETED);
        //设置派送时间
        orders.setDeliveryTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }

        /**
     * 管理员取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOnAdmin(OrdersCancelDTO ordersCancelDTO) {
        //根据订单id查询订单信息
        Orders orders = ordersMapper.queryById(ordersCancelDTO.getId());
        if(orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        //更新订单状态
        orders.setStatus(Orders.CANCELLED);
        //设置取消原因
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        //设置取消时间
        orders.setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }
}
