package com.dtstack.engine.dao;

import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ResourceGroupDao {
    Integer generalCount(@Param("model") ResourceGroup group);

    List<ResourceGroup> generalQuery(PageQuery<ResourceGroup> pageQuery);

    ResourceGroup getOne(@Param("id") Long id,@Param("deleted") Integer isDeleted);

    long countByClusterIdAndName(@Param("clusterId") Long clusterId, @Param("name") String name, @Param("componentTypeCode") Integer componentTypeCode);

    long countByLabelAndUser(@Param("clusterId") Long clusterId, @Param("label") String label, @Param("user") String user, @Param("componentTypeCode") Integer componentTypeCode);

    void insert(ResourceGroup group);

    void batchInsert(@Param("groupList") List<ResourceGroup> groupList);

    /**
     * 批量改成非叶子节点
     * @param groupList
     */
    void batchUpdateToUnLeaf(@Param("groupList") List<ResourceGroup> groupList);

    /**
     * 批量改成叶子节点
     * @param groupNameList
     */
    void batchUpdateToLeaf(@Param("groupList") List<String> groupNameList);

    void update(ResourceGroup group);

    List<String> listNamesByQueueName(@Param("clusterId") Long clusterId, @Param("queuePath") String queuePath, @Param("componentTypeCode") Integer componentTypeCode);

    List<ResourceGroupDetail> listDropDownByClusterId( @Param("clusterId")Long clusterId, @Param("componentTypeCode") Integer componentTypeCode);

    List<ResourceGroupDetail> listDetailByIds(@Param("resourceIds") Collection<Long> resourceIds);

    ResourceGroupDetail getDetailById(@Param("resourceId") Long resourceId);

    void delete(@Param("id") Long id);

    ResourceGroup findLabelByClusterAndName(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode,
                                            @Param("name") String name, @Param("queuePath") String queuePath);

    List<ResourceGroup> listByIds(@Param("resourceIds") List<Long> resourceIds);

    int batchRemove(@Param("resourceIds") List<Long> resourceIds);

    List<ResourceGroup> listByClusterIdAndComponentTypeCode(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode);

    /**
     * 获取trino资源组叶子节点集合
     * @param resourceType
     * @param clusterId
     * @param onlyChild 1只查询叶子节点，不传或其他值表示查询所有
     * @return
     */
    List<ResourceGroup> listAllTrinoResource(@Param("resourceType") Integer resourceType,@Param("clusterId") Long clusterId,@Param("onlyChild") Integer onlyChild);

    /**
     * 查询单个trino资源组信息
     * @param resourceName
     * @param clusterId
     * @return
     */
    ResourceGroup getTrinoResourceByName(@Param("resourceName") String resourceName,@Param("clusterId") Long clusterId);


    /**
     * 删除trino资源组
     * @param groupNames
     */
    void deleteTrinoResource(@Param("groupNames") List<String> groupNames);
}
