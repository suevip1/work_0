package com.dtstack.engine.master.listener;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.api.dto.BaselineTaskBatchDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.api.enums.BaselineStatusEnum;
import com.dtstack.engine.api.enums.BaselineTypeEnum;
import com.dtstack.engine.api.enums.FinishStatus;
import com.dtstack.engine.api.enums.OpenStatusEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.dto.BaselineBuildDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.impl.BaselineJobService;
import com.dtstack.engine.master.impl.BaselineTaskBatchService;
import com.dtstack.engine.master.impl.BaselineTaskService;
import com.dtstack.engine.master.impl.ScheduleTaskPriorityService;
import com.dtstack.engine.master.sync.baseline.DAGMapBaselineJobBuildr;
import com.dtstack.engine.po.BaselineJob;
import com.dtstack.engine.po.BaselineJobJob;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 10:15 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobGraphFinishBaselineListener implements JobGraphListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphFinishBaselineListener.class);

    @Autowired
    private JobGraphEvent jobGraphEvent;

    @Autowired
    private BaselineTaskService baselineTaskService;

    @Autowired
    private BaselineJobService baselineJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BaselineTaskBatchService baselineTaskBatchService;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    @Override
    public void successEvent(String triggerDay) {
        try {
            // 生成基线之前 清空优先级
            scheduleTaskPriorityService.clearPriority(null);

            Long startId = 0L;
            List<BaselineTaskDTO> baselineTask = baselineTaskService.scanningBaselineTask(startId,
                    environmentContext.getAlertTriggerRecordReceiveLimit());

            for (BaselineTaskDTO dto : baselineTask) {
                // 生成基线实例
                try {
                    LOGGER.info("baseline: {} start create job",dto.getId());
                    createBaselineJob(triggerDay, dto);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }

        } catch (Throwable throwable) {
            LOGGER.error("", throwable);
        }
    }

    public void createBaselineJob(String triggerDay, BaselineTaskDTO dto) {
        Timestamp timestamp = Timestamp.valueOf(triggerDay);
        if (baselineJobService.hasJobByBusinessDate(timestamp, dto.getId())) {
            return;
        }

        // 判断是否是单批次还是多批次，如果是单批次，逻辑不变
        if (BaselineTypeEnum.SINGLE_BATCH.getCode().equals(dto.getBatchType())) {
            // 单批次只有一个批次
            List<BaselineTaskBatchDTO> baselineTaskBatchDTOS =  baselineTaskBatchService.getBaselineBatchByBaselineTaskId(dto.getId());

            if (CollectionUtils.isNotEmpty(baselineTaskBatchDTOS)) {
                Optional<BaselineTaskBatchDTO> baselineTaskBatchDTO = baselineTaskBatchDTOS.stream().findFirst();
                baselineTaskBatchDTO.ifPresent(taskBatchDTO -> dto.setReplyTime(taskBatchDTO.getReplyTime()));
                singleBatch(triggerDay, dto,null);
            }
        } else if (BaselineTypeEnum.MANY_BATCH.getCode().equals(dto.getBatchType())) {
            manyBatch(triggerDay, dto);
        } else {
            LOGGER.error("batch not support type :{}",dto.getBatchType());
        }

    }

    private void manyBatch(String triggerDay, BaselineTaskDTO dto) {
        // 计算批次，如果计算出来的批次和用户选择的批次不一致，则无法生成基线告警
        List<AlarmChooseTaskDTO> taskVOS = dto.getTaskVOS();

        Set<String> taskCycTime = null;
        try {
            taskCycTime = baselineTaskBatchService.calculationBatch(DateUtil.getDateStrTOFormat(triggerDay, DateUtil.STANDARD_DATETIME_FORMAT, DateUtil.DATE_FORMAT), taskVOS);
        } catch (ParseException e) {
            LOGGER.error("",e);
        }

        if (CollectionUtils.isEmpty(taskCycTime)) {
            return;
        }

        List<BaselineTaskBatchDTO> baselineTaskBatchDTOS =  baselineTaskBatchService.getBaselineBatchByBaselineTaskId(dto.getId());
        Map<String, List<BaselineTaskBatchDTO>> baseMaps = baselineTaskBatchDTOS.stream().collect(Collectors.groupingBy(BaselineTaskBatchDTO::getCycTime));

        for (String cycTime : taskCycTime) {
            List<BaselineTaskBatchDTO> baselineBatchs = null;
            try {
                baselineBatchs = baseMaps.get(DateUtil.getDateStrTOFormat(cycTime,DateUtil.STANDARD_DATETIME_FORMAT,DateUtil.STANDARD_FORMAT));
            } catch (ParseException e) {
                LOGGER.error("",e);
            }

            if (CollectionUtils.isNotEmpty(baselineBatchs)) {
                Optional<BaselineTaskBatchDTO> batch = baselineBatchs.stream().findFirst();
                if (batch.isPresent()) {
                    BaselineTaskBatchDTO baselineTaskBatchDTO = batch.get();
                    if (OpenStatusEnum.OPEN.getCode().equals(baselineTaskBatchDTO.getOpenStatus()) && StringUtils.isNotBlank(baselineTaskBatchDTO.getReplyTime())) {
                        dto.setReplyTime(baselineTaskBatchDTO.getReplyTime());
                        singleBatch(triggerDay, dto,cycTime);
                    }

                }
            }
        }
    }

    private void singleBatch(String triggerDay, BaselineTaskDTO dto,String cycTime) {
        try {
            DAGMapBaselineJobBuildr dagMapBaselineJobBuildr = new DAGMapBaselineJobBuildr(applicationContext);
            Set<String> noExecCache = Sets.newHashSet();
            BaselineBuildDTO baselineBuildDTO = dagMapBaselineJobBuildr.buildBaselineJob(triggerDay, dto, noExecCache, cycTime);

            Long maxTime = 0L;
            // 取出所有实例结束时间
            List<BaselineJobJob> baselineJobJobs = baselineBuildDTO.getBaselineJobJobs();

            if (CollectionUtils.isEmpty(noExecCache)) {
                maxTime = baselineJobJobs.stream()
                        .map(baselineJobJob -> baselineJobJob.getExpectEndTime().getTime())
                        .max(Long::compareTo)
                        .orElse(0L);
            }

            BaselineJob baselineJob = new BaselineJob();
            baselineJob.setTenantId(dto.getTenantId());
            baselineJob.setProjectId(dto.getProjectId());
            baselineJob.setName(dto.getName());
            baselineJob.setBaselineTaskId(dto.getId());
            baselineJob.setAppType(dto.getAppType());
            baselineJob.setOwnerUserId(dto.getOwnerUserId());
            baselineJob.setBaselineStatus(getBaselineStatus(noExecCache));
            baselineJob.setFinishStatus(getFinishStatus());
            baselineJob.setBusinessDate(new Timestamp(DateTime.parse(triggerDay,
                    DateTimeFormat.forPattern(DateUtil.STANDARD_DATETIME_FORMAT)).getMillis()));
            baselineJob.setExpectFinishTime(getExpectFinishTime(maxTime));
            baselineJob.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            baselineJob.setBatchType(dto.getBatchType());
            baselineJob.setCycTime(StringUtils.isBlank(cycTime) ?
                    StringUtils.isBlank(baselineBuildDTO.getCycTime()) ? "" : baselineBuildDTO.getCycTime() : cycTime);
            baselineJobService.savaBaselineTask(baselineJob,baselineJobJobs,dto);
        } catch (Exception e) {
            LOGGER.error("baseline {} create error: ", JSON.toJSONString(dto), e);
        }
    }

    private Integer getFinishStatus() {
        return FinishStatus.NO_FINISH.getCode();
    }

    private Integer getBaselineStatus(Set<String> noExecCache) {
        if (CollectionUtils.isNotEmpty(noExecCache)) {
            return BaselineStatusEnum.OTHERS.getCode();
        }

        return BaselineStatusEnum.SAFETY.getCode();
    }

    private Timestamp getExpectFinishTime(Long maxTime) {

        if (maxTime == null || maxTime == 0L) {
            return null;
        }
        return new Timestamp(maxTime);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jobGraphEvent.registerEventListener(this);
    }


}
