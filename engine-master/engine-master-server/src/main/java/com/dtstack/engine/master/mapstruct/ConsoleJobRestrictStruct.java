package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.master.vo.ConsoleJobRestrictVO;
import com.dtstack.engine.po.ConsoleJobRestrict;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 21:48
 */
@Mapper(componentModel = "spring")
public interface ConsoleJobRestrictStruct {
    @Mapping(expression = "java(com.dtstack.engine.common.util.DateUtil.getDate(consoleJobRestrict.getRestrictStartTime(), com.dtstack.engine.common.util.DateUtil.STANDARD_DATETIME_FORMAT))",
            target = "restrictStartTime")
    @Mapping(expression = "java(com.dtstack.engine.common.util.DateUtil.getDate(consoleJobRestrict.getRestrictEndTime(), com.dtstack.engine.common.util.DateUtil.STANDARD_DATETIME_FORMAT))",
            target = "restrictEndTime")
    @Mapping(expression = "java(com.dtstack.engine.common.util.DateUtil.getDate(consoleJobRestrict.getEffectiveTime(), com.dtstack.engine.common.util.DateUtil.STANDARD_DATETIME_FORMAT))",
            target = "effectiveTime")
    @Mapping(expression = "java(com.dtstack.engine.common.util.DateUtil.getDate(consoleJobRestrict.getGmtCreate(), com.dtstack.engine.common.util.DateUtil.STANDARD_DATETIME_FORMAT))",
            target = "gmtCreate")
    @Mapping(expression = "java(com.dtstack.engine.common.util.DateUtil.getDate(consoleJobRestrict.getGmtModified(), com.dtstack.engine.common.util.DateUtil.STANDARD_DATETIME_FORMAT))",
            target = "gmtModified")
    @Mapping(expression = "java(java.util.Objects.nonNull(consoleJobRestrict.getEffectiveTime()) ? 1 : 0)",
            target = "isEffective")
    ConsoleJobRestrictVO toVO(ConsoleJobRestrict consoleJobRestrict);

    List<ConsoleJobRestrictVO> toVOList(List<ConsoleJobRestrict> consoleJobRestrictList);
}
