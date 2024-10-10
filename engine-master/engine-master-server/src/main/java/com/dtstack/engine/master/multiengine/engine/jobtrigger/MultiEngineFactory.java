package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.MultiEngineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yuebai
 * @date 2020-04-26
 */
@Component
public class MultiEngineFactory {

    @Resource
    private JobStartTriggerBase kylinJobStartTrigger;

    @Resource
    private JobStartTriggerBase conditionBranchJobStartTrigger;

    @Resource
    private JobStartTriggerBase jobStartTriggerBase;

    @Autowired
    JobStartTriggerDispatcher jobStartTriggerDispatcher;

    public JobStartTriggerBase getJobTriggerService(Integer multiEngineType, Integer taskType, Integer taskEngineType) {
        if (EScheduleJobType.CONDITION_BRANCH.getType().equals(taskType)) {
            return conditionBranchJobStartTrigger;
        }
        if(null == multiEngineType){
            return jobStartTriggerBase;
        }
        MultiEngineType multiEngineTypeEnum = MultiEngineType.getByType(multiEngineType);
        switch (multiEngineTypeEnum) {
            case HADOOP:
                return jobStartTriggerDispatcher.dispatch(taskType, taskEngineType);
            case KYLIN:
                return kylinJobStartTrigger;
            default:
                return jobStartTriggerBase;
        }
    }
}
