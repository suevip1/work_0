package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleJobAuth;
import org.apache.ibatis.annotations.Param;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-07-25 20:40
 */
public interface ScheduleJobAuthDao {
    int deleteById(Long id);

    int insertIgnore(ScheduleJobAuth record);

    ScheduleJobAuth selectById(@Param("id")Long id);

    ScheduleJobAuth findByUnikey(@Param("uicUserId") Long uicUserId,
                                 @Param("dtuicTenantId") Long dtuicTenantId,
                                 @Param("authType") String authType,
                                 @Param("authBizName") String authBizName);

    Integer updateByUniKey(@Param("uicUserId")Long uicUserId,
                           @Param("dtuicTenantId")Long dtuicTenantId,
                           @Param("authType")String authType,
                           @Param("authBizName")String authBizName,
                           @Param("submitUserName")String submitUserName,
                           @Param("authInfo")String authInfo);
}