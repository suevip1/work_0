package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.master.enums.CloseRetryEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/11/9 7:38 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class WhiteAbstractFillDataTask extends AbstractFillDataTask {

    private final List<TaskKeyVO> whitelist;

    public WhiteAbstractFillDataTask(ApplicationContext applicationContext, List<TaskKeyVO> whitelist) {
        super(applicationContext);
        this.whitelist = whitelist;
    }

    protected List<String> addWhite(Integer filterFrozen) {
        List<String> taskKeyVOS = Lists.newArrayList();

        if (CollectionUtils.isEmpty(whitelist)) {
            return taskKeyVOS;
        }

        if (CloseRetryEnum.OPEN.getType().equals(filterFrozen)) {
            Map<Integer, List<Long>> taskMaps = whitelist.stream().collect(Collectors.groupingBy(TaskKeyVO::getAppType, Collectors.mapping(TaskKeyVO::getTaskId, Collectors.toList())));
            for (Integer appType : taskMaps.keySet()) {
                List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeService.getTaskByIds(taskMaps.get(appType), appType);

                scheduleTaskShades.forEach(taskShade-> {
                    if (!EScheduleStatus.PAUSE.getVal().equals(taskShade.getScheduleStatus())) {
                        taskKeyVOS.add(taskShade.getTaskId()+"-"+taskShade.getAppType());
                    }
                });
            }
        } else {
            taskKeyVOS.addAll(whitelist.stream().map(taskKeyVO -> taskKeyVO.getTaskId() + "-" + taskKeyVO.getAppType()).collect(Collectors.toList()));
        }
        return taskKeyVOS;
    }
}
