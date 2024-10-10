package com.dtstack.engine.dao;


import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleFillDataJobDao {

    ScheduleFillDataJob getByJobName(@Param("jobName") String jobName, @Param("projectId") Long projectId);

    Integer insert(ScheduleFillDataJob fillDataJob);

    List<ScheduleFillDataJob> listFillJob(@Param("nameList") List<String> nameList, @Param("projectId") long projectId);

    List<ScheduleFillDataJob> listFillJobByPageQuery( PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleFillDataJob> getFillJobList(@Param("fillIdList") List<Long> fillIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId,@Param("dtuicTenantId") Long dtuicTenantId);

    Integer updateGeneratStatus(@Param("id") Long fillId, @Param("oldStatus")Integer oldStatus, @Param("status") Integer status,@Param("fillDataFailure") String fillDataFailure);

    List<ScheduleFillDataJob> listFillJobByIds(@Param("fillIds") List<Long> fillIds);

    ScheduleFillDataJob getById(@Param("id") Long id);

    Integer incrementParallelNum(@Param("id") Long id);

    Integer decrementParallelNum(@Param("id") Long id);

    Integer countListFillJobByPageQuery(PageQuery<ScheduleJobDTO> pageQuery);
}
