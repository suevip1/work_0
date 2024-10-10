package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.dao.ScheduleTaskChainParamDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.po.ScheduleTaskChainParam;
import com.dtstack.schedule.common.enums.EParamType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流参数
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-07 11:54
 */
@Service
public class ScheduleTaskWorkflowParamService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskWorkflowParamService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskChainParamDao scheduleTaskChainParamDao;

    /**
     * 保存工作流参数
     *
     * @param taskId
     * @param appType
     * @param workflowParams
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTaskWorkflowParams(Long taskId, Integer appType, List<ScheduleTaskParamShade> workflowParams) {
        this.removeTaskWorkflowParams(taskId, appType);
        if (CollectionUtils.isEmpty(workflowParams)) {
            return;
        }
        ScheduleTaskShade oneTask = scheduleTaskShadeDao.getOne(taskId, appType);
        List<ScheduleTaskChainParam> result = JobChainParamHandler.trans2TaskChainParams(workflowParams).stream()
                .peek(p -> {
                    p.setAppType(oneTask.getAppType());
                    p.setTaskType(oneTask.getTaskType());
                    p.setFlowId(oneTask.getFlowId());
                })
                .collect(Collectors.toList());
        scheduleTaskChainParamDao.batchSave(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeTaskWorkflowParams(Long taskId, Integer appType) {
        scheduleTaskChainParamDao.deleteByTaskAndType(taskId, appType, EParamType.WORK_FLOW.getType());
    }

    /**
     * 移除跟全局参数相同名称的工作流参数
     * @param taskId
     * @param appType
     * @param globalTaskParams
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeIdenticalWorkflowParam(Long taskId, Integer appType, List<ScheduleTaskParamShade> globalTaskParams) {
        // 找到没有偏移量的全局参数
        List<String> globalTaskParamsNotOffset = globalTaskParams.stream()
                .filter(p -> StringUtils.isEmpty(p.getOffset()))
                .map(ScheduleTaskParamShade::getParamName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(globalTaskParamsNotOffset)) {
            return;
        }
        scheduleTaskChainParamDao.removeIdenticalWorkflowParam(taskId, appType, globalTaskParamsNotOffset);
    }

    /**
     * 获取任务绑定的工作流参数
     * @param taskId
     * @param appType
     * @return
     */
    public List<ScheduleTaskParamShade> findTaskBindWorkflowParams(Long taskId, Integer appType) {
        List<ScheduleTaskChainParam> chainParams = scheduleTaskChainParamDao.findTaskBindWorkflowParams(taskId, appType);
        return JobChainParamHandler.trans2TaskParamShades(chainParams);
    }
}