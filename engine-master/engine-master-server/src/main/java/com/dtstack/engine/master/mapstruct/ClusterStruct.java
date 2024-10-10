package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.dto.ClusterPageDTO;
import com.dtstack.engine.api.vo.ClusterPageVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author leon
 * @date 2022-09-19 11:29
 **/
@Mapper(componentModel = "spring")
public interface ClusterStruct {

    ClusterPageVO toPageVO(ClusterPageDTO pageDTO);

    List<ClusterPageVO> toPageVOS (List<ClusterPageDTO> pageDTOS);
}
