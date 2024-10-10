package com.dtstack.engine.master.security.kerberos;

import cn.hutool.core.lang.Assert;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.master.mockcontainer.impl.ProjectKerberosAuthenticationMock;
import org.junit.Test;

@MockWith(ProjectKerberosAuthenticationMock.class)
public class ProjectKerberosAuthenticationTest {
    ProjectKerberosAuthentication projectKerberosAuthentication = new ProjectKerberosAuthentication(null);

    @Test
    public void testGetKerberosAuthenticationConfig() throws Exception {
        KerberosConfig result = projectKerberosAuthentication.getKerberosAuthenticationConfig(1L, 0, "componentVersion", 0,
                1L, 0, 1L, 1L, null);
        Assert.notNull(result);
    }
}