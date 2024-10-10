package com.dtstack.engine.master.strategy;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.api.enums.OpenStatusEnum;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.BaselineTaskTaskDao;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.master.impl.BaselineTaskService;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertRule;
import com.dtstack.engine.po.BaselineJobJob;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 2:41 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class StatusChangeStrategy extends AbstractAlertStrategy {

    @Autowired
    private BaselineTaskTaskDao baselineTaskTaskDao;

    @Override
    protected List<AlertAlarm> getAlertAlarm(AlterEventContext context) {
        Long taskId = context.getTaskId();
        Integer appType = context.getAppType();

        List<Long> alertIds = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(
                AlarmRuleBusinessTypeEnum.TASK.getCode(), Lists.newArrayList(taskId));

        if (CollectionUtils.isEmpty(alertIds)) {
            return Lists.newArrayList();
        }

        Integer status = context.getStatus();
        if (TaskStatusConstrant.FAILED_STATUS.contains(status) ){
            List<Long> baselineTaskIds = baselineTaskTaskDao.selectByTaskIds(Lists.newArrayList(taskId), appType);
            if (CollectionUtils.isNotEmpty(baselineTaskIds)) {
                // 说明这个任务有配置基线
                List<Long> baselineAlertIds = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(
                        AlarmRuleBusinessTypeEnum.BASELINE.getCode(), Lists.newArrayList(baselineTaskIds));

                if (CollectionUtils.isNotEmpty(baselineAlertIds)) {
                    alertIds.addAll(baselineAlertIds);
                }
            }
        }
        List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alertIds, appType, null);

        if (CollectionUtils.isNotEmpty(alertAlarmList)) {
            alertAlarmList = alertAlarmList.stream().filter(alertAlarm -> OpenStatusEnum.OPEN.getCode().equals(alertAlarm.getOpenStatus()))
                    .collect(Collectors.toList());
        }

        return alertAlarmList;
    }

    @Override
    public AlertRule getRule(AlterEventContext context, AlertAlarm alertAlarm, List<AlertRule> alertRules) {
        if (!AlterKey.status_change.equals(context.getKey())) {
            return null;
        }

        Integer alarmType = alertAlarm.getAlarmType();
        Integer status = context.getStatus();
        String alertRuleKey = getSupportAlertRuleKey(status,alarmType);

        if (StringUtils.isNotBlank(alertRuleKey)) {
            for (AlertRule alertRule : alertRules) {
                if (alertRule.getKey().equals(alertRuleKey)) {
                    return alertRule;
                }
            }
        }
        return null;
    }

    private String getSupportAlertRuleKey(Integer status,Integer alarmType) {
        if (AlarmTypeEnum.BASELINE.getCode().equals(alarmType)) {
            if (TaskStatusConstrant.FAILED_STATUS.contains(status) ){
                return "baseline_error";
            }
        } else {
            if (TaskStatusConstrant.FAILED_STATUS.contains(status) ){
                return "error_status";
            }

            if (TaskStatusConstrant.FINISH_STATUS.contains(status)) {
                return "finish_status";
            }

            if (TaskStatusConstrant.STOP_STATUS.contains(status)) {
                return "stop_status";
            }
        }
        return null;
    }
}
