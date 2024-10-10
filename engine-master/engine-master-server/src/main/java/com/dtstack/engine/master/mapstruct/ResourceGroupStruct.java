package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.dto.TimeUsedNode;
import com.dtstack.engine.api.vo.ResourceGroupDetailVO;
import com.dtstack.engine.api.vo.ResourceGroupDropDownVO;
import com.dtstack.engine.api.vo.ResourceGroupListVO;
import com.dtstack.engine.api.vo.TimeUsedNodeVO;
import com.dtstack.engine.api.vo.resource.ResourceGroupVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface ResourceGroupStruct {
    ResourceGroupVO toVO(ResourceGroup resourceGroup);

    ResourceGroupListVO toListVO(ResourceGroup resourceGroup);

    List<ResourceGroupListVO> toListVOs(List<ResourceGroup> resourceGroup);

    ResourceGroupDropDownVO toDropDownVO(ResourceGroupDetail detail);

    List<ResourceGroupDropDownVO> toDropDownVOs(List<ResourceGroupDetail> details);

    ResourceGroupDetailVO toGroupDetailVO(ResourceGroupDetail resourceGroupDetail);

    List<ResourceGroupDetailVO> toGroupDetailVOs(List<ResourceGroupDetail> resourceGroupDetails);

    TimeUsedNodeVO toUserNodeVO(TimeUsedNode timeUsedNode);

    List<TimeUsedNodeVO> toUserNodeVOs(List<TimeUsedNode> timeUsedNode);
}
