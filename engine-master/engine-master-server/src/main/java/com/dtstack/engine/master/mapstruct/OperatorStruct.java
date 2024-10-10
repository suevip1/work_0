package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.common.pojo.JobResult;
import org.mapstruct.Mapper;

/**
 * @Auther: dazhi
 * @Date: 2022/3/10 1:50 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface OperatorStruct {


    JobResult toJobResult(com.dtstack.dtcenter.loader.dto.JobResult jobResult);

}
