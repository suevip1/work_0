package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.job.ScheduleJobAuthVO;
import com.dtstack.engine.po.ScheduleJobAuth;
import org.mapstruct.Mapper;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-07-31 15:24
 */
@Mapper(componentModel = "spring")
public interface ScheduleJobAuthStruct {
    ScheduleJobAuthVO toVO(ScheduleJobAuth scheduleJobAuth);
}
