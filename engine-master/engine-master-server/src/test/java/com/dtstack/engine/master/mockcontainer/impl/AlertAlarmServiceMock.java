package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.AlertAlarmModel;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.vo.alert.AlertAlarmMapBusinessVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmVO;
import com.dtstack.engine.dao.AlertAlarmDao;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.dao.BaselineTaskTaskDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.faced.sdk.PublicServiceAlertApiClientSdkFaced;
import com.dtstack.engine.master.impl.BaselineTaskService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mapstruct.AlertAlarmStruct;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/7/22
 */
public class AlertAlarmServiceMock {

    @MockInvoke(targetClass = PublicServiceAlertApiClientSdkFaced.class)
    public List<ClusterAlertResultDTO> listShow() {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = BaselineTaskTaskDao.class)
    List<Long> selectByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType) {
        return Lists.newArrayList(1L);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listByNameLike(@Param("projectId") Long projectId, @Param("name") String name, @Param("appType") Integer appType, @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(1L);
        scheduleTaskShade.setAppType(1);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    List<AlertAlarmRule> selectByAlarmIds(@Param("alertAlarmIds") List<Long> alertAlarmIds, @Param("businessType") Integer businessType) {
        AlertAlarmRule alertAlarmRule = new AlertAlarmRule();
        alertAlarmRule.setAlertAlarmId(1L);
        alertAlarmRule.setBusinessType(AlarmRuleBusinessTypeEnum.RULE.getCode());


        AlertAlarmRule alertAlarmRule2 = new AlertAlarmRule();
        alertAlarmRule2.setAlertAlarmId(2L);
        alertAlarmRule2.setBusinessType(AlarmRuleBusinessTypeEnum.USER.getCode());

        AlertAlarmRule alertAlarmRule3 = new AlertAlarmRule();
        alertAlarmRule3.setAlertAlarmId(3L);
        alertAlarmRule3.setBusinessType(AlarmRuleBusinessTypeEnum.BASELINE.getCode());

        AlertAlarmRule alertAlarmRule4 = new AlertAlarmRule();
        alertAlarmRule4.setAlertAlarmId(3L);
        alertAlarmRule4.setBusinessType(AlarmRuleBusinessTypeEnum.TASK.getCode());

        AlertAlarmRule alertAlarmRule5 = new AlertAlarmRule();
        alertAlarmRule5.setAlertAlarmId(3L);
        alertAlarmRule5.setBusinessType(AlarmRuleBusinessTypeEnum.CHANNEL.getCode());
        return Lists.newArrayList(alertAlarmRule, alertAlarmRule2, alertAlarmRule3, alertAlarmRule4, alertAlarmRule5);
    }

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    Integer batchInsert(@Param("alertAlarmRules") List<AlertAlarmRule> alertAlarmRules) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    int deleteByAlertId(@Param("alarmId") Long alarmId) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    int deleteByAlertIds(@Param("alarmIds") List<Long> alarmIds,
                         @Param("businessType") Integer businessType,
                         @Param("businessIds") List<Long> businessIds) {
        return 1;
    }


    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    int updateByPrimaryKeySelective(AlertAlarmRule record) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmRuleDao.class)
    List<Long> selectAlarmByBusinessTypeAndBusinessId(@Param("businessType") Integer businessType, @Param("businessIds") List<Long> businessIds) {
        return Lists.newArrayList(1L);
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    Integer deleteByIds(@Param("ids") List<Long> ids) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    AlertAlarm selectByPrimaryKey(@Param("id") Long id) {
        AlertAlarm alertAlarm = new AlertAlarm();
        alertAlarm.setAppType(1);
        alertAlarm.setName("test");
        return alertAlarm;
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    List<AlertAlarm> selectByIds(@Param("alarmIds") List<Long> alarmIds,
                                 @Param("appType") Integer appType,
                                 @Param("projectId") Long projectId) {
        AlertAlarm alertAlarm = new AlertAlarm();
        alertAlarm.setAppType(1);
        alertAlarm.setName("test");
        return Lists.newArrayList(alertAlarm);
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    int deleteByPrimaryKey(Long id) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    int insert(AlertAlarm record) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    String selectByProjectIdAndName(@Param("projectId") Long projectId, @Param("name") String name) {
        return "";
    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    List<AlertAlarm> selectPageByModel(@Param("model") AlertAlarmModel model) {
        AlertAlarm alertAlarm = new AlertAlarm();
        alertAlarm.setAppType(1);
        alertAlarm.setName("test");
        return Lists.newArrayList(alertAlarm);
    }

    @MockInvoke(targetClass = AlertAlarmStruct.class)
    AlertAlarmMapBusinessVO toAlertAlarmMapBusinessVO(AlertAlarm alertAlarm) {
        AlertAlarmMapBusinessVO alertAlarmVO = new AlertAlarmMapBusinessVO();
        BeanUtils.copyProperties(alertAlarm, alertAlarmVO);
        return alertAlarmVO;
    }

    @MockInvoke(targetClass = AlertAlarmStruct.class)
    AlertAlarmVO toAlertAlarmVO(AlertAlarm alertAlarm) {
        AlertAlarmVO alertAlarmVO = new AlertAlarmVO();
        BeanUtils.copyProperties(alertAlarm, alertAlarmVO);
        return alertAlarmVO;
    }

    @MockInvoke(targetClass = AlertAlarmStruct.class)
    AlertAlarm alertAlarmVOToAlertAlarm(AlertAlarmVO alertAlarmVO) {
        AlertAlarm alertAlarm = new AlertAlarm();
        BeanUtils.copyProperties(alertAlarmVO, alertAlarm);
        return alertAlarm;
    }

    @MockInvoke(targetClass = AlertAlarmStruct.class)
    List<AlertAlarmPageVO> alertAlarmListToAlertAlarmPageVOs(List<AlertAlarm> alertAlarmList) {
        return alertAlarmList.stream().map(a ->
        {
            AlertAlarmPageVO alertAlarmPageVO = new AlertAlarmPageVO();
            alertAlarmPageVO.setName(a.getName());
            return alertAlarmPageVO;
        }).collect(Collectors.toList());

    }

    @MockInvoke(targetClass = AlertAlarmDao.class)
    Long countByModel(@Param("model") AlertAlarmModel model) {
        return 1L;
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public List<BaselineTaskDTO> getBaselineTaskByIds(List<Long> baselineTaskIds) {
        return new ArrayList<>();
    }


    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        return new ArrayList<>();
    }


}