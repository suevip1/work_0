package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.api.dto.BaselineTaskBatchDTO;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.BaselineTaskBatchDao;
import com.dtstack.engine.master.mapstruct.BaselineTaskBatchStruct;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.utils.SetUtil;
import com.dtstack.engine.po.BaselineTaskBatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2023/1/31 10:16 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class BaselineTaskBatchService {

    @Autowired
    private BaselineTaskBatchDao baselineTaskBatchDao;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private BaselineTaskBatchStruct baselineTaskBatchStruct;

    public Set<String> calculationBatch(String triggerDay,List<AlarmChooseTaskDTO> taskVOS) {
        if (CollectionUtils.isEmpty(taskVOS)) {
            return Sets.newHashSet();
        }

        List<Long> taskIds = taskVOS.stream().map(AlarmChooseTaskDTO::getTaskId).collect(Collectors.toList());
        List<ScheduleTaskShade> tasks = scheduleTaskShadeService.getTaskByIds(taskIds, AppType.RDOS.getType());

        ScheduleCron scheduleCronAim = null;
        Map<Long,Set<String>> taskScheduleConf = Maps.newHashMap();
        for (ScheduleTaskShade task : tasks) {
            try {
                ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(task.getScheduleConf(), timeService, task.getTaskId(), task.getAppType());

                if (scheduleCronAim==null) {
                    scheduleCronAim = scheduleCron;
                } else {
                    if (!scheduleCronAim.equals(scheduleCron)) {
                        return null;
                    }
                }

                List<String> triggerTime = scheduleCron.getTriggerTime(triggerDay);
                triggerTime = triggerTime.stream().map(DateUtil::formatTimeToScheduleTrigger).collect(Collectors.toList());
                taskScheduleConf.put(task.getTaskId(), Sets.newHashSet(triggerTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Set<String> set = null;
        if (MapUtils.isNotEmpty(taskScheduleConf)) {
            set = taskScheduleConf.get(taskVOS.get(0).getTaskId());
        }

        if (checkSameTask(set,taskScheduleConf)){
            return set;
        } else {
            return Sets.newHashSet();
        }
    }

    private boolean checkSameTask(Set<String> set,Map<Long, Set<String>> taskScheduleConf) {
        for (Set<String> value : taskScheduleConf.values()) {
            if (!SetUtil.isSetEqual(set,value)) {
                return false;
            }
        }
        return true;
    }

    public List<BaselineTaskBatchDTO> getBaselineBatchByBaselineTaskId(Long id) {
        List<BaselineTaskBatch> baselineTaskBatches = baselineTaskBatchDao.selectByBaselineTaskId(id);
        return baselineTaskBatchStruct.toDTOs(baselineTaskBatches);
    }

    public List<BaselineTaskBatchDTO> getBaselineBatchByBaselineTaskIds(List<Long> ids) {
        List<BaselineTaskBatch> baselineTaskBatches = baselineTaskBatchDao.selectByBaselineTaskIds(ids);
        return baselineTaskBatchStruct.toDTOs(baselineTaskBatches);
    }

    public void deleteByBaselineTaskId(Long id) {
        if (id !=null) {
            baselineTaskBatchDao.deleteByBaselineTaskId(id);
        }
    }

    public Integer batchInsert(List<BaselineTaskBatch> baselineTaskBatches) {
        if (CollectionUtils.isNotEmpty(baselineTaskBatches)) {
            return baselineTaskBatchDao.batchInsert(baselineTaskBatches);
        }
        return 0;
    }
}
