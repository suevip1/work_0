package com.dtstack.engine.master.mockcontainer.strategy;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.dao.AlertRuleDao;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.AlertRule;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/14 10:56 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AbstractAlertStrategyMock extends AbstractTemplateReplaceStrategyMock {

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    public List<AlertAlarmRule> selectByAlarmIds(List<Long> alertAlarmIds, Integer businessType){
        AlertAlarmRule alertAlarmRule = new AlertAlarmRule();
        alertAlarmRule.setId(1L);
        alertAlarmRule.setAlertAlarmId(1L);
        alertAlarmRule.setBusinessId(1L);
        alertAlarmRule.setBusinessType(0);

        AlertAlarmRule alertAlarmRule1 = new AlertAlarmRule();
        alertAlarmRule1.setId(2L);
        alertAlarmRule1.setAlertAlarmId(2L);
        alertAlarmRule1.setBusinessId(1L);
        alertAlarmRule1.setBusinessType(1);

        AlertAlarmRule alertAlarmRule2 = new AlertAlarmRule();
        alertAlarmRule2.setId(2L);
        alertAlarmRule2.setAlertAlarmId(1L);
        alertAlarmRule2.setBusinessId(1L);
        alertAlarmRule2.setBusinessType(2);

        AlertAlarmRule alertAlarmRule3 = new AlertAlarmRule();
        alertAlarmRule3.setId(3L);
        alertAlarmRule3.setAlertAlarmId(1L);
        alertAlarmRule3.setBusinessId(1L);
        alertAlarmRule3.setBusinessType(3);

        AlertAlarmRule alertAlarmRule4 = new AlertAlarmRule();
        alertAlarmRule4.setId(4L);
        alertAlarmRule4.setAlertAlarmId(1L);
        alertAlarmRule4.setBusinessId(1L);
        alertAlarmRule4.setBusinessType(4);
        return Lists.newArrayList(alertAlarmRule,alertAlarmRule1,alertAlarmRule2,alertAlarmRule3,alertAlarmRule4);
    }

    @MockInvoke(targetClass = AlertRuleDao.class)
    public List<AlertRule> selectByIds(@Param("ids") List<Long> ids) {
        AlertRule alertRule = new AlertRule();
        alertRule.setAppType(1);
        alertRule.setId(1L);
        alertRule.setKey("baseline_finish");
        alertRule.setParams("{}");
        alertRule.setTemplate("11222");
        alertRule.setTitle("1233");

        AlertRule alertRule1 = new AlertRule();
        alertRule1.setAppType(1);
        alertRule1.setId(2L);
        alertRule1.setKey("baseline_early_warning");
        alertRule1.setParams("{}");
        alertRule1.setTemplate("11222");
        alertRule1.setTitle("1233");

        AlertRule alertRule2 = new AlertRule();
        alertRule2.setAppType(1);
        alertRule2.setId(3L);
        alertRule2.setKey("baseline_broken_line");
        alertRule2.setParams("{}");
        alertRule2.setTemplate("11222");
        alertRule2.setTitle("1233");

        AlertRule alertRule3 = new AlertRule();
        alertRule3.setAppType(1);
        alertRule3.setId(4L);
        alertRule3.setKey("baseline_time_out_no_finish");
        alertRule3.setParams("{}");
        alertRule3.setTemplate("11222");
        alertRule3.setTitle("1233");

        AlertRule alertRule4 = new AlertRule();
        alertRule4.setAppType(1);
        alertRule4.setId(5L);
        alertRule4.setKey("timing_no_finish");
        alertRule4.setParams("{}");
        alertRule4.setTemplate("11222");
        alertRule4.setTitle("1233");

        AlertRule alertRule5 = new AlertRule();
        alertRule5.setAppType(1);
        alertRule5.setId(6L);
        alertRule5.setKey("timing_no_finish");
        alertRule5.setParams("{}");
        alertRule5.setTemplate("11222");
        alertRule5.setTitle("1233");

        AlertRule alertRule6 = new AlertRule();
        alertRule6.setAppType(1);
        alertRule6.setId(7L);
        alertRule6.setKey("baseline_error");
        alertRule6.setParams("{}");
        alertRule6.setTemplate("11222");
        alertRule6.setTitle("1233");

        AlertRule alertRule7 = new AlertRule();
        alertRule7.setAppType(1);
        alertRule7.setId(8L);
        alertRule7.setKey("error_status");
        alertRule7.setParams("{}");
        alertRule7.setTemplate("11222");
        alertRule7.setTitle("1233");

        AlertRule alertRule8 = new AlertRule();
        alertRule8.setAppType(1);
        alertRule8.setId(9L);
        alertRule8.setKey("finish_status");
        alertRule8.setParams("{}");
        alertRule8.setTemplate("11222");
        alertRule8.setTitle("1233");

        AlertRule alertRule9 = new AlertRule();
        alertRule9.setAppType(1);
        alertRule9.setId(10L);
        alertRule9.setKey("stop_status");
        alertRule9.setParams("{}");
        alertRule9.setTemplate("11222");
        alertRule9.setTitle("1233");

        return Lists.newArrayList(alertRule,alertRule1,alertRule2,alertRule3,alertRule4,alertRule5,alertRule6,alertRule7,alertRule8,alertRule9);
    }
}

