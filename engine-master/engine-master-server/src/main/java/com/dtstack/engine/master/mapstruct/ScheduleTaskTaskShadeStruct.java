package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.api.vo.task.TaskViewElementVO;
import com.dtstack.engine.api.vo.task.TaskViewSideVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 4:12 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ScheduleTaskTaskShadeStruct {


    List<ScheduleTaskTaskShadeDTO> toScheduleTaskTaskShadeDTO(List<ScheduleTaskTaskShade> taskTaskShades);

    @Mapping(source = "name", target = "taskName")
    TaskViewElementVO scheduleTaskShadeToTaskViewElementVO(ScheduleTaskShade scheduleTaskShade);

    List<TaskViewElementVO> toTaskViewElementVO(List<ScheduleTaskShade> taskLists);

    List<TaskViewSideVO> toTaskViewSideVO(List<ScheduleTaskTaskShade> taskTaskShades);
}
