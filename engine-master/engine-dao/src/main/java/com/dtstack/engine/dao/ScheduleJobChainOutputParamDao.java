package com.dtstack.engine.dao;

import com.dtstack.engine.dto.IdRangeDTO;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-22 20:12
 */
public interface ScheduleJobChainOutputParamDao {

    int batchSave(@Param("list") List<ScheduleJobChainOutputParam> list);

    int deleteOutputParamsByTask(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("jobType") Integer jobType);

    List<ScheduleJobChainOutputParam> listOutputParamsByTask(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("jobType") Integer jobType);

    ScheduleJobChainOutputParam getTempJobOutputParam(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("paramName") String paramName);

    ScheduleJobChainOutputParam getJobOutputParam(@Param("jobId") String jobId, @Param("paramName") String paramName);

    int deleteByJobId(@Param("jobId") String jobId);

    List<ScheduleJobChainOutputParam> listByOutputParamType(@Param("jobId") String jobId, @Param("outputParamType") Integer outputParamType);

    List<ScheduleJobChainOutputParam> listByJobId(@Param("jobId") String jobId);

    List<ScheduleJobChainOutputParam> listTempJobOutputParamsByThreshold(@Param("retainInDay") Integer retainInDay);

    List<Integer> listTempJobOutputParamIdsByThreshold(@Param("retainInDay") Integer retainInDay);

    List<ScheduleJobChainOutputParam> listCleanJobOutputParamsById(@Param("ids") List<Integer> ids, @Param("rdbTaskTypes") Set<Integer> rdbTaskTypes);

    int deleleBatchByIds(@Param("ids") List<Integer> ids);

    int modifyParamValue(@Param("paramValue") String paramValue, @Param("jobId") String jobId, @Param("paramName") String paramName);

    /************** start 以下方法拼接sql用于清除历史数据，慎用！！！***************/
    IdRangeDTO queryIdRange(@Param("whereSql") String whereSql);

    long queryCountByCondition(@Param("whereSql") String whereSql);

    List<ScheduleJobChainOutputParam> queryByCondition(@Param("whereSql") String whereSql);

    long deleteByCondition(@Param("whereSql") String whereSql);
    /************** end ***************/

}