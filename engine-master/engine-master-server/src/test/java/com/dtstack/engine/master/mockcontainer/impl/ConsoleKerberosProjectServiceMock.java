package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import com.dtstack.engine.dao.ConsoleKerberosProjectDao;
import com.dtstack.engine.master.mapstruct.KerberosProjectStruct;
import com.dtstack.engine.po.ConsoleKerberosProject;
import org.apache.ibatis.annotations.Param;

public class ConsoleKerberosProjectServiceMock {

    @MockInvoke(targetClass = ConsoleKerberosProjectDao.class)
    int updateByPrimaryKeySelective(@Param("record") ConsoleKerberosProject record) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleKerberosProjectDao.class)
    int insert(@Param("record") ConsoleKerberosProject record) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleKerberosProjectDao.class)
    ConsoleKerberosProject selectByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType) {
        if (1l == projectId) {
            return null;
        }
        ConsoleKerberosProject consoleKerberosProject = new ConsoleKerberosProject();
        consoleKerberosProject.setProjectId(projectId);
        consoleKerberosProject.setTaskTypeList("1,213");
        consoleKerberosProject.setOpenKerberos(1);
        return consoleKerberosProject;
    }

    @MockInvoke(targetClass = KerberosProjectStruct.class)
    ConsoleKerberosProject toConsoleKerberosProject(KerberosProjectVO kerberosProjectVO) {
        ConsoleKerberosProject consoleKerberosProject = new ConsoleKerberosProject();
        consoleKerberosProject.setOpenKerberos(kerberosProjectVO.getOpenKerberos());
        consoleKerberosProject.setTaskTypeList(kerberosProjectVO.getTaskTypeList());
        return consoleKerberosProject;
    }

}
