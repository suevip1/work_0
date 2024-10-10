package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.BaselineTaskConditionModel;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.api.vo.alert.BaselineTaskConditionVO;
import com.dtstack.engine.api.vo.alert.BaselineTaskPageVO;
import com.dtstack.engine.api.vo.alert.BaselineTaskVO;
import com.dtstack.engine.po.BaselineTask;
import com.dtstack.engine.po.BaselineTaskTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 11:13 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface BaselineTaskStruct {


    BaselineTaskConditionModel voToBaselineTaskConditionModel(BaselineTaskConditionVO vo);

    List<BaselineTaskPageVO> baselineTaskToBaselineTaskPageVO(List<BaselineTask> baselineTasks);

    BaselineTask baselineTaskVOTOBaselineTask(BaselineTaskVO baselineTaskVO);

    BaselineTaskVO baselineTaskToBaselineTaskVO(BaselineTask baselineTask);

    BaselineTaskDTO baselineTaskToBaselineTaskDTO(BaselineTask baselineTask);

    @Mapping(source = "taskAppType", target = "appType")
    AlarmChooseTaskDTO baselineTaskTasksToAlarmChooseTaskDTO(BaselineTaskTask taskTask);

    List<AlarmChooseTaskDTO> baselineTaskTasksToAlarmChooseTaskDTOs(List<BaselineTaskTask> taskTasks);

    List<BaselineTaskDTO> baselineTaskToBaselineTaskDTOs(List<BaselineTask> baselineTasks);

    List<BaselineTaskVO> toBaselineTaskVOs(List<BaselineTask> baselineTasks);
}
