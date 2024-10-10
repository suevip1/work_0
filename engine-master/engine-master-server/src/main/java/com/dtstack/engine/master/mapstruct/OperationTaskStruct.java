package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.dto.TaskPageDTO;
import com.dtstack.engine.api.vo.task.TaskPageVO;
import org.mapstruct.Mapper;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName OperationTaskStruct
 * @date 2022/7/20 10:41 AM
 */
@Mapper(componentModel = "spring")
public interface OperationTaskStruct {

    TaskPageDTO taskPageVOToTaskPageDTO(TaskPageVO vo);
}
