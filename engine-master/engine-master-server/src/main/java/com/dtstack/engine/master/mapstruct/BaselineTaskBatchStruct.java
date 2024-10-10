package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.api.dto.BaselineTaskBatchDTO;
import com.dtstack.engine.api.vo.BaselineBatchVO;
import com.dtstack.engine.api.vo.alert.AlarmChooseTaskVO;
import com.dtstack.engine.po.BaselineTaskBatch;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/3 1:53 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface BaselineTaskBatchStruct {

    BaselineTaskBatchDTO toDTO(BaselineTaskBatch baselineTaskBatch);

    List<BaselineTaskBatchDTO> toDTOs(List<BaselineTaskBatch> baselineTaskBatches);

    AlarmChooseTaskDTO toAlarmChooseTaskDTO(AlarmChooseTaskVO vo);

    List<AlarmChooseTaskDTO> toAlarmChooseTaskDTOs(List<AlarmChooseTaskVO> taskVOS);

    List<BaselineBatchVO> toBatchTaskVOs(List<BaselineTaskBatchDTO> baselineBatchByBaselineTaskIds);

}
