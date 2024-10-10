package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobBuildRecord;
import com.dtstack.engine.dto.ScheduleJobBuildRecordQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务实例构建记录表
 *
 * @author ：wangchuan
 * date：Created in 15:09 2022/8/31
 * company: www.dtstack.com
 */
public interface ScheduleJobBuildRecordDao {

    /**
     * 插入一条实例构建记录
     *
     * @param scheduleJobBuildRecord 单条实例构建记录
     * @return 执行结果
     */
    Integer insert(ScheduleJobBuildRecord scheduleJobBuildRecord);

    /**
     * 更新任务实例构建状态
     *
     * @param recordIdList   记录 id 集合
     * @param jobBuildStatus 实例构建状态
     * @return 执行结果
     */
    Integer updateJobBuildStatus(@Param("recordIdList") List<Long> recordIdList, @Param("jobBuildStatus") Integer jobBuildStatus);

    /**
     * 更新任务实例构建状态和日志
     *
     * @param recordIdList   记录 id 集合
     * @param jobBuildStatus 实例构建状态
     * @param jobBuildLog    实例构建日志
     * @return 执行结果
     */
    Integer updateJobBuildStatusAndLog(@Param("recordIdList") List<Long> recordIdList, @Param("jobBuildStatus") Integer jobBuildStatus, @Param("jobBuildLog") String jobBuildLog);

    /**
     * 根据条件查询任务实例构建记录
     *
     * @param scheduleJobBuildRecordQuery 查询条件
     * @return 实例构建记录
     */
    List<ScheduleJobBuildRecord> getByCondition(@Param("model") ScheduleJobBuildRecordQuery scheduleJobBuildRecordQuery);
}
