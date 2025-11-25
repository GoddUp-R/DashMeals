package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;


    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        ArrayList<String> dateList = new ArrayList<>();
        dateList.add(begin.toString());
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin.toString());
        }
        ArrayList<BigDecimal> turnoverList = new ArrayList<>();
        for(String date : dateList){
            BigDecimal amount = orderMapper.querySumAmountByDate(date);
            if(amount == null){
                amount = BigDecimal.ZERO;
            }
            turnoverList.add(amount);
        }


        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(String.join(",", dateList))
                .turnoverList(String.join(",", turnoverList.stream().map(BigDecimal::toString).collect(Collectors.toList())))
                .build();
        return turnoverReportVO;
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 日期列表
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 用户总量列表
        ArrayList<Integer> totalUserList = new ArrayList<>();
        // 新增用户列表
        ArrayList<Integer> newUserList = new ArrayList<>();
        //遍历日期列表
        for (LocalDate localDate : dateList) {
            //获取当前日期的最早时间
            LocalDateTime start = LocalDateTime.of(localDate, LocalTime.MIN);
            //获取当前日期的最晚时间
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //获取当前日期之前的用户总量
            Integer totalUser = userMapper.queryUserCountByDate(null,endTime);
            if(totalUser == null){
                totalUser = 0;
            }
            totalUserList.add(totalUser);
            //获取当前日期新增用户总量
            Integer newUser = userMapper.queryUserCountByDate(start,endTime);
            if(newUser == null){
                newUser = 0;
            }
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 日期列表
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //订单数列表
        ArrayList<Integer> orderCountList = new ArrayList<>();
        //有效订单数列表
        ArrayList<Integer> validOrderCountList = new ArrayList<>();

        //遍历日期表
        for (LocalDate localDate : dateList) {
            //获取当前日期的最早时间
            LocalDateTime start = LocalDateTime.of(localDate, LocalTime.MIN);
            //获取当前日期的最晚时间
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //获取当前日期的订单数
            Integer orderCount = orderMapper.queryOrderCountByDate(start,endTime,null);
            if(orderCount == null){
                orderCount = 0;
            }
            orderCountList.add(orderCount);
            //获取当前日期的有效订单数
            Integer validOrderCount = orderMapper.queryOrderCountByDate(start,endTime, Orders.COMPLETED);
            if(validOrderCount == null){
                validOrderCount = 0;
            }
            validOrderCountList.add(validOrderCount);
        }
        //订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);
        //有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);
        //订单完成率
        Double orderCompletionRate = validOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount;
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}
