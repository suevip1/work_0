package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.schedule.job.ScheduleJobHistoryVO;
import com.dtstack.engine.po.ScheduleJobHistory;
import org.mapstruct.Mapper;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface ScheduleJobHistoryStruct {

    ScheduleJobHistoryVO toJobHistoryVO(ScheduleJobHistory jobHistory);
}
