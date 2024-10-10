package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleTenantComponent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author leon
 * @date 2022-08-22 11:05
 **/
@Repository
public interface ConsoleTenantComponentDao {

    Integer insert(ConsoleTenantComponent consoleTenantComponent);

    ConsoleTenantComponent getOneById(Long id);

    List<ConsoleTenantComponent> getByTenantIdAndClusterIdAndComponentTypeCode(@Param("tenantId") Long tenantId,
                                                                               @Param("clusterId") Long clusterId,
                                                                               @Param("componentTypeCode") Integer componentTypeCode);

    Integer update(ConsoleTenantComponent consoleTenantComponent);

}
