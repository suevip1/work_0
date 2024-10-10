package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.engine.po.ScheduleProjectParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleProjectParamStruct {

    ScheduleProjectParam toEntity(ScheduleProjectParamDTO projectParamDTO);

    ScheduleProjectParamDTO toDTO(ScheduleProjectParam projectParam);

    List<ScheduleProjectParamDTO> toDTOs(List<ScheduleProjectParam> projectParam);

}