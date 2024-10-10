package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskBlack;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/11/1 4:38 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskBlackDao {


    List<ScheduleTaskBlack> findByTenantAndProjectAndAppType(@Param("dtuicTenantId") Long dtuicTenantId, @Param("projectId") Long projectId, @Param("appType") Integer appType);
}
