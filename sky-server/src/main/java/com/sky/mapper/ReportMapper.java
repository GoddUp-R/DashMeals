package com.sky.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ReportMapper {
    /**
     * 查询指定日期范围内的营业额统计
     * @param dateList 日期列表
     * @return 营业额列表
     */
    ArrayList<BigDecimal> queryTurnover(ArrayList<LocalDate> dateList);
}
