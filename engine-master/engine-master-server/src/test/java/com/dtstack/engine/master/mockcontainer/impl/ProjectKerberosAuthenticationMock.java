package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.dtstack.engine.po.ConsoleKerberosProject;

public class ProjectKerberosAuthenticationMock {

    @MockInvoke(targetClass = ConsoleKerberosProjectService.class)
    public ConsoleKerberosProject findKerberosProject(Long projectId, Integer appType) {
        ConsoleKerberosProject consoleKerberosProject = new ConsoleKerberosProject();
        consoleKerberosProject.setProjectId(1L);
        consoleKerberosProject.setOpenKerberos(1);
        consoleKerberosProject.setPrincipal("HTTP/master@DTSTACK.COM");
        consoleKerberosProject.setKeytabName("hive.keytab");
        consoleKerberosProject.setTaskTypeList("0,1");
        return consoleKerberosProject;
    }

    }
