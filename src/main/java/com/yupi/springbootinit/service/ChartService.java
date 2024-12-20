package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.yupi.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author leeyou
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-12-18 21:08:01
*/
public interface ChartService extends IService<Chart> {

    Wrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
}
