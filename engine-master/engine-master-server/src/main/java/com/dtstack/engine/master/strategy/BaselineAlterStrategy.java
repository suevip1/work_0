package com.dtstack.engine.master.strategy;

import com.dtstack.engine.api.enums.*;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.listener.AlterChannelListener;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertRule;
import com.dtstack.engine.po.BaselineJob;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 2:38 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class BaselineAlterStrategy extends AbstractAlertStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlterChannelListener.class);

    @Override
    protected List<AlertAlarm> getAlertAlarm(AlterEventContext context) {
        LOGGER.info("jobId: {} baseline get alertAlarm ",context.getJobId());
        BaselineJob baselineJob = context.getBaselineJob();
        Integer appType = baselineJob.getAppType();
        Long baselineTaskId = baselineJob.getBaselineTaskId();

        List<Long> alertId = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(
                AlarmRuleBusinessTypeEnum.BASELINE.getCode(), Lists.newArrayList(baselineTaskId));

        List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alertId, appType, null);

        alertAlarmList = alertAlarmList.stream().filter(alertAlarm ->
                AlarmTypeEnum.BASELINE.getCode().equals(alertAlarm.getAlarmType())
                        && OpenStatusEnum.OPEN.getCode().equals(alertAlarm.getOpenStatus()))
                .collect(Collectors.toList());
        LOGGER.info("jobId: {} baseline get alertAlarm {}",context.getJobId(),alertAlarmList);
        return alertAlarmList;
    }

    @Override
    protected AlertRule getRule(AlterEventContext context, AlertAlarm alertAlarm, List<AlertRule> alertRules) {
        Integer baselineStatus = context.getBaselineStatus();
        Integer finishStatus = context.getFinishStatus();
        String alertRuleKey = getSupportAlertRuleKey(baselineStatus,finishStatus);
        LOGGER.info("jobId: {} rule {}",context.getJobId(),alertRuleKey);
        if (StringUtils.isNotBlank(alertRuleKey)) {
            for (AlertRule alertRule : alertRules) {
                if (alertRule.getKey().equals(alertRuleKey)) {
                    return alertRule;
                }
            }
        }
        return null;
    }

    private String getSupportAlertRuleKey(Integer status, Integer finishStatus) {
        if (FinishStatus.FINISH.getCode().equals(finishStatus)) {
            return "baseline_finish";
        }

        if (BaselineStatusEnum.WARNING.getCode().equals(status)) {
            return "baseline_early_warning";
        }

        if (BaselineStatusEnum.BREAKING_THE_LINE.getCode().equals(status)) {
            return "baseline_broken_line";
        }

        if (BaselineStatusEnum.TIMING_NOT_COMPLETED.getCode().equals(status)) {
            return "baseline_time_out_no_finish";
        }

        return null;
    }
}
