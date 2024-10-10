package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleKerberosProject;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: dazhi
 * @Date: 2022/3/14 1:46 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface ConsoleKerberosProjectDao {

    int insert(@Param("record") ConsoleKerberosProject record);

    int updateByPrimaryKeySelective(@Param("record") ConsoleKerberosProject record);

    ConsoleKerberosProject selectByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType);
}
