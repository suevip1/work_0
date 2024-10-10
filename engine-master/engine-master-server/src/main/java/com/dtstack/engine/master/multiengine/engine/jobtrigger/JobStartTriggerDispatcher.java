package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-04 11:01
 * jobStartTrigger 转发器
 */
@Component
public class JobStartTriggerDispatcher {

    @Resource
    private SyncJobStartTrigger syncJobStartTrigger;

    @Resource
    private ScriptJobStartTrigger scriptJobStartTrigger;

    @Resource
    private MrJobStartTrigger mrJobStartTrigger;

    @Resource
    private JobStartTriggerBase hadoopJobStartTrigger;

    @Resource
    private FileJobStartTrigger fileJobStartTrigger;

    @Resource
    private AgentJobStartTrigger agentJobStartTrigger;


    public JobStartTriggerBase dispatch(Integer taskType, Integer taskEngineType) {
        if (EScheduleJobType.FILE_COPY.getType().equals(taskType)) {
            return fileJobStartTrigger;
        }else if (SQL_JOB_TYPE.contains(taskType)) {
            return hadoopJobStartTrigger;
        } else if (EScheduleJobType.SYNC.getVal().equals(taskType)) {
            return syncJobStartTrigger;
        }  else if (belongScriptJobTrigger(taskType, taskEngineType)) {
            return scriptJobStartTrigger;
        } else if (MR_ENGINE_TYPE.contains(taskEngineType)) {
            return mrJobStartTrigger;
        }

        // agent server 任务
        if (AGENT_JOB_TYPE.contains(taskType)) {
            return agentJobStartTrigger;
        }

        return hadoopJobStartTrigger;
    }

    private static final List<Integer> SQL_JOB_TYPE = ImmutableList.of(
            EScheduleJobType.SPARK_SQL.getVal(),
            EScheduleJobType.HIVE_SQL.getVal(),
            EScheduleJobType.CARBON_SQL.getVal());

    public static final Set<Integer> AGENT_JOB_TYPE = ImmutableSet.of(
            EScheduleJobType.SHELL_ON_AGENT.getVal(),
            EScheduleJobType.PYTHON_ON_AGENT.getVal()
    );

    private static final List<Integer> SCRIPT_ENGINE_TYPE = ImmutableList.of(
            ScheduleEngineType.Learning.getVal(),
            ScheduleEngineType.Shell.getVal(), ScheduleEngineType.DtScript.getVal(),
            ScheduleEngineType.Python2.getVal(), ScheduleEngineType.Python3.getVal()
    );

    private static final List<Integer> MR_ENGINE_TYPE = ImmutableList.of(
            ScheduleEngineType.Hadoop.getVal(),
            ScheduleEngineType.Spark.getVal());

    private static boolean belongScriptJobTrigger(Integer taskType, Integer taskEngineType) {
        return SCRIPT_ENGINE_TYPE.contains(taskEngineType)
                || (taskEngineType.equals(ScheduleEngineType.Spark.getVal()) && !taskType.equals(EScheduleJobType.SPARK.getVal()));
    }
}