package com.dtstack.engine.api.vo.task;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2023-07-05 11:12
 * @Email: dazhi@dtstack.com
 * @Description: StatisticsPriorityReturnVO
 */
public class StatisticsPriorityReturnVO {


    private String currentRefreshTime;


    private Map<String,List<StatisticsPriorityCountVO>> statisticsMap;

    public String getCurrentRefreshTime() {
        return currentRefreshTime;
    }

    public void setCurrentRefreshTime(String currentRefreshTime) {
        this.currentRefreshTime = currentRefreshTime;
    }

    public Map<String, List<StatisticsPriorityCountVO>> getStatisticsMap() {
        return statisticsMap;
    }

    public void setStatisticsMap(Map<String, List<StatisticsPriorityCountVO>> statisticsMap) {
        this.statisticsMap = statisticsMap;
    }
}
