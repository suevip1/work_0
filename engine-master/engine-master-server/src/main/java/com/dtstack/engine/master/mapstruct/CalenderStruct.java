package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.api.vo.calender.CalenderTaskVO;
import com.dtstack.engine.api.vo.calender.ConsoleCalenderVO;
import com.dtstack.engine.api.vo.calender.TaskCalenderVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalenderStruct {

    ConsoleCalenderVO toVo(ConsoleCalender consoleCalender);

    List<ConsoleCalenderVO> toVOs(List<ConsoleCalender> consoleCalenders);

    CalenderTaskVO toTaskVO(CalenderTaskDTO calenderTaskDTO);

    List<TaskCalenderVO> toTaskCalenderVO(List<CalenderTaskDTO> calenderByTasks);
}
