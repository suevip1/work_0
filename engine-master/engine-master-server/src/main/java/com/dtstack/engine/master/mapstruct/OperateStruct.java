package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.ScheduleJobOperate;
import com.dtstack.engine.api.vo.ScheduleJobOperateVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 3:09 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface OperateStruct {


    List<ScheduleJobOperateVO> toScheduleJobOperateVOs(List<ScheduleJobOperate> scheduleJobOperate);
}
