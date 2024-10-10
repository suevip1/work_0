package com.dtstack.engine.master.mockcontainer.strategy;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.AlertAlarmDao;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.po.AlertAlarm;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/12/14 10:54 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineAlterStrategyMock extends AbstractAlertStrategyMock {

    private Map<Long,AlertAlarm> map= Maps.newConcurrentMap();

    public BaselineAlterStrategyMock() {
        AlertAlarm alertAlarm = new AlertAlarm();
        alertAlarm.setId(1L);
        alertAlarm.setProjectId(1L);
        alertAlarm.setTenantId(1L);
        alertAlarm.setAppType(1);
        alertAlarm.setOpenStatus(0);
        alertAlarm.setAlarmType(1);
        alertAlarm.setIsDeleted(0);
        alertAlarm.setName("1");
        map.put(1L,alertAlarm);

        AlertAlarm alertAlarm2 = new AlertAlarm();
        alertAlarm2.setId(2L);
        alertAlarm2.setProjectId(1L);
        alertAlarm2.setTenantId(1L);
        alertAlarm2.setAppType(1);
        alertAlarm2.setOpenStatus(0);
        alertAlarm2.setAlarmType(1);
        alertAlarm2.setIsDeleted(0);
        alertAlarm2.setName("2");
        map.put(2L,alertAlarm2);

        AlertAlarm alertAlarm3 = new AlertAlarm();
        alertAlarm3.setId(3L);
        alertAlarm3.setProjectId(1L);
        alertAlarm3.setTenantId(1L);
        alertAlarm3.setAppType(1);
        alertAlarm3.setOpenStatus(0);
        alertAlarm3.setAlarmType(1);
        alertAlarm3.setIsDeleted(0);
        alertAlarm3.setName("3");
        map.put(3L,alertAlarm3);

        AlertAlarm alertAlarm4 = new AlertAlarm();
        alertAlarm4.setId(4L);
        alertAlarm4.setProjectId(1L);
        alertAlarm4.setTenantId(1L);
        alertAlarm4.setAppType(1);
        alertAlarm4.setOpenStatus(0);
        alertAlarm4.setAlarmType(1);
        alertAlarm4.setIsDeleted(0);
        alertAlarm4.setName("4");
        map.put(4L,alertAlarm4);

        AlertAlarm alertAlarm5 = new AlertAlarm();
        alertAlarm5.setId(5L);
        alertAlarm5.setProjectId(1L);
        alertAlarm5.setTenantId(1L);
        alertAlarm5.setAppType(1);
        alertAlarm5.setOpenStatus(0);
        alertAlarm5.setAlarmType(1);
        alertAlarm5.setIsDeleted(0);
        alertAlarm5.setName("5");
        map.put(5L,alertAlarm5);


    }

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    public List<Long> selectAlarmByBusinessTypeAndBusinessId(Integer businessType,
                                                             List<Long> businessIds) {
        return Lists.newArrayList(1L, 2L, 3L, 4L, 5L);
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    public List<AlertAlarm> selectByIds(List<Long> alarmIds,
                                        Integer appType, Long projectId) {
        List<AlertAlarm> alertAlarmList = Lists.newArrayList();

        for (Long alarmId : alarmIds) {
            AlertAlarm alertAlarm = map.get(alarmId);

            if (alertAlarm != null) {
                alertAlarmList.add(alertAlarm);
            }
        }
        return alertAlarmList;
    }

}
