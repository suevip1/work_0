package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.dtstack.engine.master.mapstruct.KerberosProjectStruct;
import com.dtstack.engine.po.ConsoleKerberosProject;
import org.junit.Test;

@MockWith(ConsoleKerberosProjectSdkControllerMock.class)
public class ConsoleKerberosProjectSdkControllerTest {
    ConsoleKerberosProjectSdkController consoleKerberosProjectSdkController = new ConsoleKerberosProjectSdkController();

    @Test
    public void testAddOrUpdateKerberosProject() throws Exception {
        consoleKerberosProjectSdkController.addOrUpdateKerberosProject(new KerberosProjectVO());
    }

    @Test
    public void testFindKerberosProject() throws Exception {
        consoleKerberosProjectSdkController.findKerberosProject(1L, 0);
    }
}

class ConsoleKerberosProjectSdkControllerMock {
    @MockInvoke(targetClass = ConsoleKerberosProjectService.class)
    public ConsoleKerberosProject findKerberosProject(Long projectId, Integer appType) {
        return null;
    }

    @MockInvoke(targetClass = KerberosProjectStruct.class)
    KerberosProjectVO toKerberosProjectVO(ConsoleKerberosProject consoleKerberosProject) {
        return null;
    }

    @MockInvoke(targetClass = ConsoleKerberosProjectService.class)
    public Boolean addOrUpdateKerberosProject(KerberosProjectVO kerberosProjectVO) {
        return true;
    }
}