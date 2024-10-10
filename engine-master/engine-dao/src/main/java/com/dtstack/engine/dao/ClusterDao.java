package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ClusterPageDTO;
import com.dtstack.engine.api.dto.ClusterPageQueryDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClusterDao {

    Integer getPageQueryCount(@Param("model") ClusterPageQueryDTO clusterPageQueryDTO);

    List<ClusterPageDTO> pageQuery(PageQuery<ClusterPageQueryDTO> pageQueryDTO);

    List<Cluster> generalQuery(PageQuery<ClusterDTO> pageQuery);

    Integer insert(Cluster cluster);

    Integer insertWithId(Cluster cluster);

    Cluster getByClusterName(@Param("clusterName") String clusterName);

    Cluster getOne(@Param("id") Long clusterId);

    List<Cluster> listAll();

    Integer countAll();

    Integer updateHadoopVersion(@Param("id") Long clusterId, @Param("hadoopVersion") String hadoopVersion);

    void deleteCluster(Long clusterId);

    void logicRemoveCluster(Long clusterId);

    void updateGmtModified(Long clusterId);

}
