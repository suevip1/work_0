package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import org.mapstruct.Mapper;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 4:12 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ScheduleTaskShadeStruct {

    ScheduleTaskShadeDTO toScheduleTaskShadeDTO(ScheduleTaskShade shade);
}
