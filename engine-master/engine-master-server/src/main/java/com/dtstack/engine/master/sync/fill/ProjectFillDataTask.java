package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.common.enums.ETaskGroupEnum;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.google.common.collect.Sets;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 1:51 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ProjectFillDataTask extends AbstractFillDataTask {

    private final FillDataInfoBO fillDataInfoBO;

    public ProjectFillDataTask(ApplicationContext applicationContext, FillDataInfoBO fillDataInfoBO) {
        super(applicationContext);
        this.fillDataInfoBO = fillDataInfoBO;
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.CONDITION_PROJECT_TASK;
    }

    @Override
    public Set<String> getRunList() {
        Set<String> runList = Sets.newHashSet();
        // 添加白名单
        List<FillDataChooseTaskVO> whitelist = fillDataInfoBO.getWhiteList();
        whitelist.forEach(white -> runList.add(white.getTaskId() + FillDataConst.KEY_DELIMITER + white.getAppType()));

        // 添加工程下的节点
        List<FillDataChooseProjectVO> projectList = fillDataInfoBO.getProjectList();
        List<FillDataChooseTaskVO> blacklist = fillDataInfoBO.getBlackList();
        List<String> blackTaskKeyList = blacklist.stream().map(vo -> vo.getTaskId() + FillDataConst.KEY_DELIMITER + vo.getAppType()).collect(Collectors.toList());
        for (FillDataChooseProjectVO project : projectList) {
            List<ScheduleTaskShadeDTO> shadeDTOS = scheduleTaskShadeService.findTaskKeyByProjectId(project.getProjectId(), project.getAppType(),null,null, ETaskGroupEnum.NORMAL_SCHEDULE.getType());

            shadeDTOS.forEach(scheduleTaskShadeDTO -> {
                String taskKey = scheduleTaskShadeDTO.getTaskId() + FillDataConst.KEY_DELIMITER + scheduleTaskShadeDTO.getAppType();
                if (!blackTaskKeyList.contains(taskKey)) {
                    // 补数据黑名称
                    runList.add(taskKey);
                }
            });

        }

        return runList;
    }
}
