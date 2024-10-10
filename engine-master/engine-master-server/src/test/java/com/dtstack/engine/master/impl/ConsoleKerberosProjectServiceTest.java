package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import com.dtstack.engine.master.mockcontainer.impl.ConsoleKerberosProjectServiceMock;
import com.dtstack.engine.po.ConsoleKerberosProject;
import org.junit.Assert;
import org.junit.Test;

@MockWith(ConsoleKerberosProjectServiceMock.class)
public class ConsoleKerberosProjectServiceTest {

    ConsoleKerberosProjectService consoleKerberosProjectService = new ConsoleKerberosProjectService();

    @Test
    public void testAddOrUpdateKerberosProject() throws Exception {
        KerberosProjectVO kerberosProjectVO = new KerberosProjectVO();
        kerberosProjectVO.setProjectId(100L);
        Boolean result = consoleKerberosProjectService.addOrUpdateKerberosProject(kerberosProjectVO);
        Assert.assertEquals(Boolean.TRUE, result);

        KerberosProjectVO updateProject = new KerberosProjectVO();
        updateProject.setProjectId(1L);
        result = consoleKerberosProjectService.addOrUpdateKerberosProject(updateProject);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testFindKerberosProject() throws Exception {
        ConsoleKerberosProject result = consoleKerberosProjectService.findKerberosProject(1L, 0);
        Assert.assertNull(result);
    }
}