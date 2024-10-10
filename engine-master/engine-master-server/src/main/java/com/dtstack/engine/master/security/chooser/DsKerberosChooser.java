package com.dtstack.engine.master.security.chooser;

import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.dtstack.engine.master.security.kerberos.DsKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.ProjectKerberosAuthentication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-07 20:42
 * 保留旧逻辑：项目级 --> 数据源级(meta数据源时，等同集群级)
 */
@Component
public class DsKerberosChooser extends AbstractKerberosChooser implements InitializingBean {

    @Autowired
    private ConsoleKerberosProjectService kerberosProjectService;

    @Override
    public void afterPropertiesSet() throws Exception {
        ProjectKerberosAuthentication projectKerberosAuthentication = new ProjectKerberosAuthentication(kerberosProjectService);
        DsKerberosAuthentication dsKerberosAuthentication = new DsKerberosAuthentication();

        add(projectKerberosAuthentication);
        add(dsKerberosAuthentication);
    }
}
