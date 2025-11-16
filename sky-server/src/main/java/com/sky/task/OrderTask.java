package com.sky.task;

import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.sky.entity.Orders;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Resource
    private OrderMapper orderMapper;
    //处理超时订单
//    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrders() {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        //获取超时订单
        List<Orders> orders = orderMapper.queryTimeoutOrders(Orders.PENDING_PAYMENT, localDateTime.minusMinutes(15));
        if (orders != null && orders.size() > 0) {
            for (Orders order : orders) {
                log.info("处理超时订单，订单id：{}", order.getId());
                //更新订单状态为已取消
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时未支付");
                orderMapper.update(order);
            }
        }
    }


    //处理派送超时订单
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processTimeoutDeliverOrders() {
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        //获取超时订单
        List<Orders> orders = orderMapper.queryTimeoutOrders(Orders.DELIVERY_IN_PROGRESS, localDateTime.minusMinutes(60));
        if (orders != null && orders.size() > 0) {
            for (Orders order : orders) {
                log.info("处理超时订单，订单id：{}", order.getId());
                //更新订单状态为已取消
                order.setStatus(Orders.COMPLETED);
                order.setCancelReason("订单已完成");
                orderMapper.update(order);
            }
        }
    }
}
