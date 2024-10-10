package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordConditionVO;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordPageVO;
import com.dtstack.engine.api.vo.alert.CountAlarmVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.AlertAlarmDao;
import com.dtstack.engine.dao.AlertTriggerRecordDao;
import com.dtstack.engine.dao.AlertTriggerRecordReceiveDao;
import com.dtstack.engine.dto.AlertTriggerRecordModel;
import com.dtstack.engine.master.faced.sdk.PublicServiceAlertApiClientSdkFaced;
import com.dtstack.engine.master.mapstruct.AlertTriggerRecordStruct;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertTriggerRecord;
import com.dtstack.engine.po.AlertTriggerRecordReceive;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 2:53 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertTriggerRecordService {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private AlertTriggerRecordDao alertTriggerRecordDao;

    @Autowired
    private AlertTriggerRecordStruct alertTriggerRecordStruct;

    @Autowired
    private AlertTriggerRecordReceiveDao alertTriggerRecordReceiveDao;

    @Autowired
    private AlertAlarmDao alertAlarmDao;

    @Autowired
    private PublicServiceAlertApiClientSdkFaced alertApiClientSdkFaced;

    public PageResult<List<AlertTriggerRecordPageVO>> page(AlertTriggerRecordConditionVO vo) {
        AlertTriggerRecordModel alertTriggerRecordModel = alertTriggerRecordStruct.alertTriggerRecordModelToAlertTriggerRecordConditionVO(vo);

        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        if (startTime != null) {
            alertTriggerRecordModel.setStartTimestamp(new Timestamp(startTime * 1000));
        }

        if (endTime != null) {
            alertTriggerRecordModel.setEndTimestamp(new Timestamp(endTime * 1000));
        }

        //历史原因 receiverId 实际接受为任务责任人
        Long ownerUserId = vo.getReceiverId();

        if (ownerUserId != null && ownerUserId > 0) {
            alertTriggerRecordModel.setOwnerUserId(ownerUserId);
        }

        Long count = alertTriggerRecordDao.countByModel(alertTriggerRecordModel);
        List<AlertTriggerRecordPageVO> pageVOS = Lists.newArrayList();
        if (count > 0) {
            List<AlertTriggerRecord> records = alertTriggerRecordDao.selectByModel(alertTriggerRecordModel);
            List<Long> alertAlarmIds = records.stream().map(AlertTriggerRecord::getAlertAlarmId).collect(Collectors.toList());

            List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alertAlarmIds, vo.getAppType(), vo.getProjectId());
            Map<Long, AlertAlarm> alarmMap = alertAlarmList.stream().collect(Collectors.toMap(AlertAlarm::getId, g -> (g)));

            pageVOS = alertTriggerRecordStruct.recordsToPageVOs(records);

            // 补充接收人信息
            List<Long> ids = pageVOS.stream().map(AlertTriggerRecordPageVO::getId).collect(Collectors.toList());
            List<AlertTriggerRecordReceive> receives = alertTriggerRecordReceiveDao.selectRecordNameByReceiverIds(ids);

            List<ClusterAlertResultDTO> clusterAlertResultDTOS = alertApiClientSdkFaced.listShow();
            List<Integer> channelIds = clusterAlertResultDTOS.stream().map(ClusterAlertResultDTO::getAlertId).collect(Collectors.toList());

            Map<Long, List<AlertTriggerRecordReceive>> receiveMaps = receives.stream().collect(Collectors.groupingBy(AlertTriggerRecordReceive::getAlertTriggerRecordId));
            for (AlertTriggerRecordPageVO pageVO : pageVOS) {
                Long id = pageVO.getId();
                List<AlertTriggerRecordReceive> recordReceives = receiveMaps.get(id);
                if (CollectionUtils.isNotEmpty(recordReceives)) {
                    List<String> receiveNames = recordReceives.stream().map(AlertTriggerRecordReceive::getReceiveUserName).collect(Collectors.toList());
                    pageVO.setReceiveList(receiveNames);
                }
                AlertAlarm alertAlarm = alarmMap.get(pageVO.getAlertAlarmId());

                if (alertAlarm != null) {
                    pageVO.setAlertAlarmName(alertAlarm.getName());
                }
            }
        }
        return new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), count.intValue(), pageVOS);
    }
    
    public CountAlarmVO countAlarm(Long projectId, Integer appType) {
        CountAlarmVO countAlarmVO = new CountAlarmVO();
        Timestamp time = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
        Integer count = getCountWithCreateTime(projectId, time, appType);

        Timestamp weekTime = new Timestamp(DateUtil.getLastDay(6));
        Integer weekCount = getCountWithCreateTime(projectId, weekTime, appType);

        Timestamp monthTime = new Timestamp(DateUtil.getLastDay(29));
        Integer monthCount = getCountWithCreateTime(projectId, monthTime, appType);

        countAlarmVO.setToday(count);
        countAlarmVO.setWeek(weekCount);
        countAlarmVO.setMonth(monthCount);
        return countAlarmVO;
    }

    public Integer getCountWithCreateTime(Long projectId, Timestamp time, Integer appType) {
        return alertTriggerRecordDao.countAlarmByGmtCreate(time, projectId, appType);
    }

}
