package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.dto.JobPageDTO;
import com.dtstack.engine.api.vo.job.JobPageVO;
import org.mapstruct.Mapper;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName OperationJobStruct
 * @date 2022/7/21 4:07 PM
 */
@Mapper(componentModel = "spring")
public interface OperationJobStruct {

    JobPageDTO jobPageVOToJobPageDTO(JobPageVO vo);
}
