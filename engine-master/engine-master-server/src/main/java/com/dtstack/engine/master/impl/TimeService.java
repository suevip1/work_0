package com.dtstack.engine.master.impl;


import com.dtstack.engine.po.ScheduleTaskCalender;

import java.util.List;

public interface TimeService {

    /**
     * 获取任务指定日期内日历
     *
     * @param calenderId 日历 id
     * @param expendTime 扩展时间
     * @param startTime  开始日期 20220302085000
     * @param endTime    结束日期 20220303085000
     * @return yyyyMMddHHmmss
     */
    List<String> listBetweenTime(Long calenderId, String expendTime, String startTime, String endTime,Integer candlerBatchType);



    /**
     * 获取过去时间距离currentDateMillis 最近的一条日历时间
     *
     * @param calenderId        日历 id
     * @param expendTime        扩展时间
     * @param currentDateMillis currentDateMillis
     * @param eq                是否允许时间相同
     * @return yyyyMMddHHmmss
     */
    String getNearestTime(Long calenderId, String expendTime, String currentDateMillis,Integer candlerBatchType, boolean eq);

    String getNearestOffset(Long calenderId, String expendTime, Integer customOffset, String currentDateFormat,Integer candlerBatchType, boolean eq);

    /**
     * 获取任务日历绑定关系
     *
     * @param taskId  任务 id
     * @param appType 应用类型
     * @return 绑定关系
     */
    ScheduleTaskCalender getScheduleTaskCalender(Long taskId, Integer appType);


}
