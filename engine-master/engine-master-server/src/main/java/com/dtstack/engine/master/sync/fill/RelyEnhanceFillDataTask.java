package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.api.vo.schedule.job.FillDataRelyInfo;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.master.enums.CloseRetryEnum;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/10/28 2:03 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class RelyEnhanceFillDataTask extends WhiteAbstractFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(RelyEnhanceFillDataTask.class);

    private final FillDataRelyInfo fillDataRelyInfo;

    public RelyEnhanceFillDataTask(ApplicationContext applicationContext, FillDataRelyInfo fillDataRelyInfo,List<TaskKeyVO> whitelist) {
        super(applicationContext,whitelist);
        this.fillDataRelyInfo = fillDataRelyInfo;
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.RELY;
    }

    @Override
    public Set<String> getRunList() {
        List<TaskKeyVO> rootTasks = fillDataRelyInfo.getRootTasks();
        Set<String> runList = Sets.newHashSet();

        if (CollectionUtils.isNotEmpty(rootTasks)) {
            int level = 0;
            List<String> rootKey = filter(rootTasks, fillDataRelyInfo.getFilterFrozen());
            runList.addAll(rootKey);
            List<ScheduleTaskTaskShadeDTO> taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(rootKey);
            while (CollectionUtils.isNotEmpty(taskTaskShadeDTOS)) {
                List<String> taskKeys = taskTaskShadeDTOS.stream()
                        .filter(dto -> {
                            if (CloseRetryEnum.OPEN.getType().equals(fillDataRelyInfo.getFilterFrozen())) {
                                return !EScheduleStatus.PAUSE.getVal().equals(dto.getScheduleStatus());
                            } else {
                                return true;
                            }
                        })
                        .map(ScheduleTaskTaskShadeDTO::getTaskKey).collect(Collectors.toList());
                runList.addAll(taskKeys);

                level++;

                if (level > environmentContext.getFillDataRootTaskMaxLevel()) {
                    LOGGER.warn("rootTaskId max:{} break cycle", level);
                    break;
                }

                taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(taskKeys);
            }
        }

        // 添加白名单
        runList.addAll(addWhite(fillDataRelyInfo.getFilterFrozen()));
        return runList;
    }

    private List<String> filter(List<TaskKeyVO> rootTasks, Integer filterFrozen) {
        if (CloseRetryEnum.OPEN.getType().equals(filterFrozen)) {
            Map<Integer, List<Long>> taskMaps = rootTasks.stream().collect(Collectors.groupingBy(TaskKeyVO::getAppType, Collectors.mapping(TaskKeyVO::getTaskId, Collectors.toList())));
            List<String> rootKey = Lists.newArrayList();
            for (Integer appType : taskMaps.keySet()) {
                List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeService.getTaskByIds(taskMaps.get(appType), appType);

                scheduleTaskShades.forEach(taskShade-> {
                    if (!EScheduleStatus.PAUSE.getVal().equals(taskShade.getScheduleStatus())) {
                        rootKey.add(taskShade.getTaskId()+"-"+taskShade.getAppType());
                    }
                });
            }
            return rootKey;
        } else {
           return rootTasks.stream()
                    .map(rootTaskId -> rootTaskId.getTaskId() + FillDataConst.KEY_DELIMITER + rootTaskId.getAppType())
                    .collect(Collectors.toList());
        }
    }
}
