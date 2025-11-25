package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    private OrderMapper orderMapper;


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
}
