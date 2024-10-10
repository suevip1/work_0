package com.dtstack.engine.master.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.api.enums.OpenStatusEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertRule;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 2:40 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class ScanningAlterStrategy extends AbstractAlertStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanningAlterStrategy.class);

    public static final String TIMING_NO_FINISH_KEY = "timing_no_finish";

    public static final String TIME_OUT_NO_FINISH_KEY = "time_out_no_finish";

    private final String FILED_UN_COMPLETE_TIME = "uncompleteTime";

    private final String FILED_TIMER_UNCOMPLETE_TIME = "timerUncompleteTime";

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    protected List<AlertAlarm> getAlertAlarm(AlterEventContext context) {
        Long taskId = context.getTaskId();
        Integer appType = context.getAppType();

        List<Long> alertId = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(
                AlarmRuleBusinessTypeEnum.TASK.getCode(), Lists.newArrayList(taskId));
        List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alertId, appType, null);
        alertAlarmList = alertAlarmList.stream().filter(alertAlarm ->
                AlarmTypeEnum.isTask(alertAlarm.getAlarmType())
                        && OpenStatusEnum.OPEN.getCode().equals(alertAlarm.getOpenStatus()))
                .collect(Collectors.toList());
        return alertAlarmList;
    }

    @Override
    protected AlertRule getRule(AlterEventContext context, AlertAlarm alertAlarm, List<AlertRule> alertRules) {
        List<String> keys = getSupportAlertRuleKey();

        List<AlertRule> filterRule = alertRules.stream().filter(alertRule ->
                keys.contains(alertRule.getKey())).collect(Collectors.toList());

        // 获取触发告警的告警规则
        if (CollectionUtils.isNotEmpty(filterRule)) {
            for (AlertRule alertRule : filterRule) {
                if (judgeAlertRule(context,alertAlarm,alertRule)) {
                    return alertRule;
                }
            }
        }

        return null;
    }

    /**
     * 判断是否触发告警
     *
     * @param context    事件信息
     * @param alertAlarm 配置的告警信息
     * @param alertRule  告警触发的规则
     * @return 是否触发告警
     */
    private boolean judgeAlertRule(AlterEventContext context,AlertAlarm alertAlarm, AlertRule alertRule) {
        String extraParams = alertAlarm.getExtraParams();
        JSONObject extraMap = JSON.parseObject(extraParams);
        ScheduleJob job = scheduleJobService.getByJobId(context.getJobId(), IsDeletedEnum.NOT_DELETE.getType());

        if (job == null){
            LOGGER.error("schedule job not found when judge alter rule {}",context.getJobId());
            return Boolean.FALSE;
        }

        if (!RdosTaskStatus.FINISH_STATUS.contains(job.getStatus()) && TIMING_NO_FINISH_KEY.equals(alertRule.getKey())) {
            // 定时未完成
            String uncompleteTime = extraMap.getString(FILED_TIMER_UNCOMPLETE_TIME);

            if (StringUtils.isNotBlank(uncompleteTime)) {

                DateTime parse = DateTime.parse(job.getCycTime(),
                        DateTimeFormat.forPattern(DateUtil.UN_STANDARD_DATETIME_FORMAT));
                DateTime uncompleteDateTime = DateTime.parse(parse.toString(DateUtil.DATE_FORMAT) + " " + uncompleteTime + ":00",
                        DateTimeFormat.forPattern(DateUtil.STANDARD_DATETIME_FORMAT));

                if (System.currentTimeMillis() > uncompleteDateTime.getMillis()) {
                    // 触发告警
                    return Boolean.TRUE;
                }
            }

        } else if (RdosTaskStatus.RUNNING_STATUS.contains(job.getStatus()) && TIME_OUT_NO_FINISH_KEY.equals(alertRule.getKey())) {
            // 超时未完成
            String min = extraMap.getString(FILED_UN_COMPLETE_TIME);
            int minInt = Integer.parseInt(min);
            long milliSecond = minInt  * 1000L;
            Timestamp execStartTime = job.getExecStartTime();
            if (execStartTime != null) {
                milliSecond += execStartTime.getTime();
                if (System.currentTimeMillis() >= milliSecond) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    private List<String> getSupportAlertRuleKey() {
        return Lists.newArrayList(TIMING_NO_FINISH_KEY, TIME_OUT_NO_FINISH_KEY);
    }
}
