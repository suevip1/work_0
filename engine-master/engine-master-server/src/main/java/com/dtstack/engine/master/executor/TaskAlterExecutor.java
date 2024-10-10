package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.dao.AlertRuleDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.master.impl.AlertAlarmService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.listener.AlterEvent;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.master.strategy.ScanningAlterStrategy;
import com.dtstack.engine.master.utils.CacheKeyUtil;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.AlertRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/25 2:13 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class TaskAlterExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(TaskAlterExecutor.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Autowired
    private AlertAlarmService alertAlarmService;

    @Autowired
    private AlertAlarmRuleDao alertAlarmRuleDao;

    @Autowired
    private AlterEvent alterEvent;

    @Autowired
    private AlertRuleDao alertRuleDao;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private ScheduledExecutorService scheduledService;

    public void scanningAlarm(List<ScheduleBatchJob> scheduleJobs) {
        LOGGER.info("scanning alarm task start");
        try {
            Set<Long> alarmTaskIds = getAlarmTaskIds();
            List<ScheduleBatchJob> batchJobs = scheduleJobs.stream().filter(s -> alarmTaskIds.contains(s.getTaskId())).collect(Collectors.toList());
            for (ScheduleBatchJob batchJob : batchJobs) {
                LOGGER.info("jobId {} taskId: {} start event",batchJob.getJobId(),batchJob.getTaskId());
                AlterEventContext context = new AlterEventContext();
                context.setKey(AlterKey.scanning);
                context.setStatus(batchJob.getStatus());
                context.setJobId(batchJob.getJobId());
                context.setTaskId(batchJob.getTaskId());
                context.setAppType(batchJob.getAppType());
                context.setScheduleType(batchJob.getScheduleType());
                alterEvent.event(context);
            }
        } catch (Throwable e) {
            LOGGER.error("error happen during handle scanningAlarm:{}", e.getMessage(), e);
        }
    }


    Set<Long> getAlarmTaskIds() {

        Set<Long> taskIds = Sets.newHashSet();
       List<AlertRule> alertRules = alertRuleDao.selectAll();
       List<Long> supperRuleIds = alertRules.stream().filter(alertRule ->
                       alertRule.getKey().equals(ScanningAlterStrategy.TIME_OUT_NO_FINISH_KEY)
                               || alertRule.getKey().equals(ScanningAlterStrategy.TIMING_NO_FINISH_KEY))
               .map(AlertRule::getId).collect(Collectors.toList());

       Long id = null;
       List<AlertAlarm> alertAlarmList = alertAlarmService.scanningAlertAlarmByIdLimit(id,
               environmentContext.getAlertTriggerRecordReceiveLimit(), null);
       while (CollectionUtils.isNotEmpty(alertAlarmList)) {
           List<Long> ids = alertAlarmList.stream().map(AlertAlarm::getId).collect(Collectors.toList());
           List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(ids, null);

           Map<Long, List<Long>> alertToTaskMaps = alertAlarmRules.stream().filter(alertAlarmRule ->
                           AlarmRuleBusinessTypeEnum.TASK.getCode().equals(alertAlarmRule.getBusinessType()))
                   .collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId, Collectors.mapping(
                           AlertAlarmRule::getBusinessId, Collectors.toList())));

           Map<Long, List<Long>> alertToRuleMaps = alertAlarmRules.stream().filter(alertAlarmRule ->
                           AlarmRuleBusinessTypeEnum.RULE.getCode().equals(alertAlarmRule.getBusinessType()))
                   .collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId, Collectors.mapping(
                           AlertAlarmRule::getBusinessId, Collectors.toList())));

           for (AlertAlarm alertAlarm : alertAlarmList) {
               if (id == null || id < alertAlarm.getId()) {
                   id = alertAlarm.getId();
               }

               if (AlarmTypeEnum.BASELINE.getCode().equals(alertAlarm.getAlarmType())) {
                   continue;
               }

               List<Long> ruleIds = alertToRuleMaps.get(alertAlarm.getId());
               List<Long> intersection = supperRuleIds.stream().filter(ruleIds::contains).collect(Collectors.toList());

               if (CollectionUtils.isEmpty(intersection)) {
                   continue;
               }
               taskIds.addAll(alertToTaskMaps.get(alertAlarm.getId()));
           }
           alertAlarmList =  alertAlarmService.scanningAlertAlarmByIdLimit(id,
                   environmentContext.getAlertTriggerRecordReceiveLimit(), null);
       }
       return taskIds;
   }
}
