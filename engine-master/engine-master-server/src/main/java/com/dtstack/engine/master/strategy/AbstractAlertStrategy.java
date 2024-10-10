package com.dtstack.engine.master.strategy;

import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.AlertAlarmDao;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.dao.AlertRuleDao;
import com.dtstack.engine.master.listener.AlterChannelListener;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.AlertRule;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/24 11:31 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractAlertStrategy extends AbstractTemplateReplaceStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlterChannelListener.class);

    @Autowired
    protected AlertAlarmDao alertAlarmDao;

    @Autowired
    protected AlertAlarmRuleDao alertAlarmRuleDao;

    @Autowired
    protected AlertRuleDao alertRuleDao;

    @Override
    public void alert(AlterEventContext context) {
        // 获得该通知事件对应的所有告警
        List<AlertAlarm> alertAlarmList = getAlertAlarm(context);
        if (CollectionUtils.isEmpty(alertAlarmList)) {
            LOGGER.warn("send jobId {} alter not alert alarm", context.getJobId());
            return;
        }
        Map<Long, AlertAlarm> mapAlertAlarm = alertAlarmList.stream()
                .collect(Collectors.toMap(AlertAlarm::getId, g -> (g)));

        List<Long> alertId = alertAlarmList.stream().map(AlertAlarm::getId).collect(Collectors.toList());
        List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(alertId, null);

        Map<Long, List<AlertAlarmRule>> alertRuleMap = alertAlarmRules.stream()
                .collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId));

        for (Map.Entry<Long, List<AlertAlarmRule>> entry : alertRuleMap.entrySet()) {
            List<AlertAlarmRule> entryValue = entry.getValue();
            Long alertAlarmId = entry.getKey();
            AlertAlarm alertAlarm = mapAlertAlarm.get(alertAlarmId);

            List<Long> ruleIds = entryValue.stream()
                    .filter(alertAlarmRule -> AlarmRuleBusinessTypeEnum.RULE.getCode().equals(alertAlarmRule.getBusinessType()))
                    .map(AlertAlarmRule::getBusinessId).collect(Collectors.toList());

            List<AlertRule> alertRules = alertRuleDao.selectByIds(ruleIds);
            if (!preCheckAlertRule(entryValue, context,alertAlarm.getAlarmType())) {
                continue;
            }
            AlertRule alertRule = getRule(context, alertAlarm, alertRules);
            if (alertRule == null) {
                continue;
            }

            // 筛选出和当前告警规则相关联的 AlertAlarmRule
            List<AlertAlarmRule> relatedAlertAlarmRules = alertAlarmRules.stream()
                    .filter(e -> e.getAlertAlarmId().equals(alertAlarmId))
                    .collect(Collectors.toList());

            // 开始发送告警
            long start = System.currentTimeMillis();
            LOGGER.info("send jobId {} alter start send alarm. alarm: {} rule: {}", context.getJobId(),
                    alertAlarm.getName(),alertRule.getName());
            sendAlarm(alertAlarm, relatedAlertAlarmRules, alertRule, context);
            LOGGER.info("send jobId {} alter end send alarm. alarm: {} rule: {},time {}", context.getJobId(),alertAlarm.getName(),alertRule.getName(),System.currentTimeMillis()-start);

        }
    }

    /**
     * 获得所有规则
     */
    protected abstract List<AlertAlarm> getAlertAlarm(AlterEventContext context);

    /**
     * 获得支持的key
     *
     * @param context 上下文对象
     * @return 需要支持的rule的key
     */
    protected abstract AlertRule getRule(AlterEventContext context, AlertAlarm alertAlarm, List<AlertRule> alertRules);


    /**
     * 补数据配置了 才允许告警
     * 补数据配置了自动重试，补数据重试时也不会触发告警
     * 补数据重跑不触发告警
     * 手动任务允许告警
     *
     * @param alertAlarmRules
     * @param alterEventContext
     * @return
     */
    private boolean preCheckAlertRule(List<AlertAlarmRule> alertAlarmRules, AlterEventContext alterEventContext, Integer alertType) {
        if (AlarmTypeEnum.BASELINE.getCode().equals(alertType)) {
            return true;
        }

        if (AlarmTypeEnum.MANUAL_TASK.getCode().equals(alertType)) {
            return EScheduleType.MANUAL.getType().equals(alterEventContext.getScheduleType());
        }

        Predicate<AlterEventContext> fillDataPredicate =
                context -> EScheduleType.FILL_DATA.getType().equals(context.getScheduleType()) &&
                        alertAlarmRules.stream()
                                .noneMatch(alertAlarmRule -> AlarmRuleBusinessTypeEnum.FILL_DATA_ALERT.getCode().equals(alertAlarmRule.getBusinessType()));
        if (fillDataPredicate.test(alterEventContext)) {
            return false;
        }
        String jobId = alterEventContext.getJobId();
        ScheduleJob job = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (null == job) {
            return false;
        }
        if (Restarted.RESTARTED.getStatus() == job.getIsRestart()) {
            return false;
        }
        return true;
    }
}
