package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.po.ScheduleTaskChainParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-22 00:32
 */
public interface ScheduleTaskChainParamDao {

    int deleteInOutParamsByTaskShade(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    int batchSave(@Param("list") List<ScheduleTaskChainParam> list);

    List<ScheduleTaskChainParam> listParamsByTask(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("type") Integer type);

    List<ScheduleTaskChainParam> listOutputParamsByTask(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("outputParamType") Integer outputParamType);

    ScheduleTaskChainParam getParamByTaskAndName(@Param("taskShade") ScheduleTaskShade taskShade, @Param("type") Integer type, @Param("paramName") String paramName);


    int deleteByTaskAndType(@Param("taskId")Long taskId, @Param("appType")Integer appType, @Param("type") Integer type);

    int deleteByTasksAndType(@Param("taskIds")List<Long> taskIds, @Param("appType")Integer appType, @Param("type") Integer type);

    int deleteByTasks(@Param("taskIds")List<Long> taskIds, @Param("appType")Integer appType);

    int deleteFlowParamsByFlowTaskAndType(@Param("flowId")Long flowId, @Param("appType")Integer appType, @Param("type")Integer type);

    int removeIdenticalWorkflowParam(@Param("taskId")Long taskId, @Param("appType")Integer appType, @Param("paramNames")List<String> paramNames);

    List<ScheduleTaskChainParam> findTaskBindWorkflowParams(@Param("taskId")Long taskId, @Param("appType")Integer appType);

}