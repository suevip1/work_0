package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 1:50 下午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BatchFillDataTask extends AbstractFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(BatchFillDataTask.class);

    private final FillDataInfoBO fillDataInfoBO;

    public BatchFillDataTask(ApplicationContext applicationContext, FillDataInfoBO fillDataInfoBO) {
        super(applicationContext);
        this.fillDataInfoBO = fillDataInfoBO;
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.BATCH;
    }

    @Override
    public Set<String> getRunList() {
        Set<String> runList = Sets.newHashSet();
        FillDataChooseTaskVO rootTaskId = this.fillDataInfoBO.getRootTaskId();

        if (rootTaskId == null) {
            List<FillDataChooseTaskVO> taskIds = this.fillDataInfoBO.getTaskChooseList();
            taskIds.forEach(task -> runList.add(task.getTaskId() + FillDataConst.KEY_DELIMITER + task.getAppType()));
        } else {
            List<String> rootKey = Lists.newArrayList(rootTaskId.getTaskId() + FillDataConst.KEY_DELIMITER + rootTaskId.getAppType());
            runList.addAll(rootKey);
            int level = 0;
            List<ScheduleTaskTaskShadeDTO> taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(rootKey);
            while (CollectionUtils.isNotEmpty(taskTaskShadeDTOS)) {
                List<String> taskKeys = taskTaskShadeDTOS.stream().map(ScheduleTaskTaskShadeDTO::getTaskKey).collect(Collectors.toList());
                runList.addAll(taskKeys);

                level++;

                if (level > environmentContext.getFillDataRootTaskMaxLevel()) {
                    LOGGER.warn("rootTaskId:{} max:{} break cycle",rootTaskId.getTaskId(),level);
                    break;
                }

                taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(taskKeys);
            }
        }

        return runList;
    }


}
