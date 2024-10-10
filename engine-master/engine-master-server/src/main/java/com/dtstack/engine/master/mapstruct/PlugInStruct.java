package com.dtstack.engine.master.mapstruct;

import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDescriptionDTO;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.FlinkTableResult;
import com.dtstack.engine.api.vo.stream.FlinkQueryResultVO;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.dto.ApplicationInfo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/11/5 4:07 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface PlugInStruct {

    /**
     * CheckResultDTO -> CheckResult
     *
     * @param checkResultDTO
     * @return
     */
    CheckResult toCheckResult(CheckResultDTO checkResultDTO);

    FlinkTableResult toFlinkTableResult(FlinkTableResultDTO flinkTableResultDTO);

    List<FlinkTableResult> toFlinkTableResultList(List<FlinkTableResultDTO> flinkTableResultDTOList);



    FlinkQueryResultVO toFlinkQueryResultVO(FlinkQueryResultDTO flinkQueryResultDTO);

    /**
     * ComponentTestResultDTO -> ComponentTestResult
     *
     * @param testConnect
     * @return
     */
    ComponentTestResult toComponentTestResult(ComponentTestResultDTO testConnect);


    ApplicationInfo toApplicationInfoDTO(ApplicationInfoDTO dto);

    ComponentTestResult.ClusterResourceDescription yarnResourceDescriptionDTOtoComponentTestResult(YarnResourceDescriptionDTO yarnResourceDescriptionDTO);

    ClusterResource yarnResourceDTOtoClusterResource(YarnResourceDTO yarnResource);

}
