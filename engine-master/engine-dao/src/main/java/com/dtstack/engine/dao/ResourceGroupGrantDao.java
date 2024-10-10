package com.dtstack.engine.dao;

import com.dtstack.engine.api.vo.resource.ResourceGroupQueryParam;
import com.dtstack.engine.dto.LabelGrantedProjectDTO;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ResourceGroupGrant;
import com.dtstack.engine.dto.GrantedProjectDTO;
import com.dtstack.engine.dto.ResourceGroupGrantSearchDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceGroupGrantDao {
    Integer generalCount(@Param("model") ResourceGroupGrantSearchDTO group);

    List<GrantedProjectDTO> generalQuery(PageQuery<ResourceGroupGrantSearchDTO> pageQuery);

    void insert(ResourceGroupGrant grant);

    long countByResourceIdAndEngineProjectId(@Param("resourceId") Long resourceId,
                                             @Param("projectId") Long projectId,@Param("appType")Integer appType);

    List<ResourceGroupDetail> listAccessedResourceDetailByProjectId(@Param("projectId") Long projectId,@Param("appType")Integer appType, @Param("componentTypeCode")Integer componentTypeCode);

    List<ResourceGroupDetail> listAccessedResourceByProjectId(@Param("projectId") Long projectId,@Param("appType")Integer appType, @Param("componentTypeCode")Integer componentTypeCode);

    List<ResourceGroup> listAccessedResourceGroups(@Param("projectId") Long projectId, @Param("appType")Integer appType, @Param("componentTypeCode")Integer componentTypeCode);

    long countAccessedResourceByProjectId(@Param("projectId") Long projectId,@Param("appType")Integer appType, @Param("componentTypeCode")Integer componentTypeCode);

    List<Long> listGrantedProjects(@Param("resourceId") Long resourceId,@Param("appType")Integer appType);

    void insertAll(@Param("grants") List<ResourceGroupGrant> grants);

    void delete(@Param("resourceId") Long resourceId, @Param("projectId") Long projectId,@Param("appType")Integer appType);

    /**
     * 用户组换trino绑资源组
     * @param oldResourceId
     * @param resourceId
     * @param groupId
     */
    void updateByResourceIdAndGroupId(@Param("oldResourceId")Long oldResourceId, @Param("resourceId") Long resourceId, @Param("groupId") Long groupId);


    void removeTenantId(@Param("dtuicTenantId") Long dtUicTenantId);

    int removeByTenantIdAndResourceIds(@Param("dtuicTenantId") Long dtUicTenantId, @Param("resourceIds") List<Long> resourceIds);

    int removeByProject(@Param("projectId") Long projectId, @Param("appType") Integer appType);

    ResourceGroupGrant getById(@Param("id")Long id);

    boolean changeProjectDefault(@Param("id")Long resourceGrantId, @Param("targetDefault") Integer targetDefault);

    int revertProjectDefault(@Param("ids")List<Long> ids);

    List<ResourceGroupGrant> listByResourceIdAndAppType(@Param("resourceIds") List<Long> resourceIds, @Param("appType")Integer appType);

    List<ResourceGroupGrant> listDistinctProjectsByResourceIdAndAppType(@Param("resourceIds") List<Long> resourceIds, @Param("appType")Integer appType);

    List<LabelGrantedProjectDTO> generalLabelQuery(PageQuery<ResourceGroupGrantSearchDTO> query);

    int generalLabelCount(@Param("model")ResourceGroupGrantSearchDTO searchDto);

    ResourceGroupDetail findAccessedResourceDetail(@Param("model")ResourceGroupQueryParam resourceGroupQueryParam);

    List<GrantedProjectDTO> listAllTrinoResourceBindInfos(@Param("dtUicTenantId") Long dtuicTenantId);

    List<ResourceGroupGrant> listTrinoResourceBindByGroupId(@Param("dtuicTenantId") Long dtuicTenantId,@Param("groupId") Long groupId);

    List<GrantedProjectDTO> fuzzyQueryByResourceName(@Param("resourceName") String resourceName,@Param("dtuicTenantId") Long dtuicTenantId);
}
