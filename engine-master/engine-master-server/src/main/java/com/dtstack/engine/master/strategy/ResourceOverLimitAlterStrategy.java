package com.dtstack.engine.master.strategy;

import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.AlertRule;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 资源超限告警
 *
 * @author ：wangchuan
 * date：Created in 13:48 2023/1/30
 * company: www.dtstack.com
 */
@Component
public class ResourceOverLimitAlterStrategy extends AbstractAlertStrategy {

    @Override
    public void alert(AlterEventContext context) {
        Long alertAlarmId = context.getAlertAlarmId();
        if (Objects.isNull(alertAlarmId)) {
            return;
        }
        AlertAlarm alertAlarm = alertAlarmDao.selectByPrimaryKey(alertAlarmId);
        List<AlertAlarmRule> alarmRuleList = alertAlarmRuleDao.selectByAlarmIds(Lists.newArrayList(alertAlarmId), null);
        AlertRule alertRule = alertRuleDao.selectByKey(AbstractAlertStrategy.RESOURCE_OVER_LIMIT);
        if (Objects.isNull(alertRule)) {
            return;
        }
        // 发送告警
        sendAlarm(alertAlarm, alarmRuleList, alertRule, context);
    }

    @Override
    protected List<AlertAlarm> getAlertAlarm(AlterEventContext context) {
        return null;
    }

    @Override
    protected AlertRule getRule(AlterEventContext context, AlertAlarm alertAlarm, List<AlertRule> alertRules) {
        return null;
    }
}
