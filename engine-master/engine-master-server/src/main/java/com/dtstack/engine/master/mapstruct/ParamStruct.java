package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.po.ScheduleJobParam;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParamStruct {

    ConsoleParamVO toVo(ConsoleParam consoleParam);

    ConsoleParamBO toBO(ConsoleParam consoleParam);

    List<ConsoleParamBO> toBOs(List<ConsoleParam> consoleParams);

    List<ConsoleParamVO> toVos(List<ConsoleParam> consoleParams);

    ConsoleParam toEntity(ConsoleParamVO consoleParamVO);

    @Mapping(source = "paramName", target = "paramName")
    @Mapping(source = "paramValue", target = "paramCommand")
    @Mapping(source = "paramType", target = "type")
    ScheduleTaskParamShade toTaskParam(ConsoleParam consoleParam);


    @Mapping(source = "paramName", target = "paramName")
    @Mapping(source = "paramValue", target = "paramCommand")
    @Mapping(source = "paramType", target = "type")
    ScheduleTaskParamShade toTaskParam(ConsoleParamBO consoleParam);

    List<ScheduleTaskParamShade> toTaskParams(List<ConsoleParam> consoleParam);

    List<ScheduleTaskParamShade> BOtoTaskParams(List<ConsoleParamBO> consoleParamBOS);

    @Mapping(source = "paramName", target = "paramName")
    @Mapping(source = "paramValue", target = "paramCommand")
    @Mapping(source = "paramType", target = "type")
    ScheduleTaskParamShade jobParamToTaskParam(ScheduleJobParam scheduleJobParam);

    List<ScheduleTaskParamShade> jobParamToTaskParams(List<ScheduleJobParam> scheduleJobParams);

}
