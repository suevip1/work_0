package com.dtstack.engine.master.security.chooser;

import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.dtstack.engine.master.security.kerberos.ProjectKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.SyncPartitionKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.UserKerberosAuthentication;
import com.dtstack.pubsvc.sdk.usercenter.client.UicKerberosApiClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 16:47
 * 保留旧逻辑：用户-->项目-->集群(meta数据源)
 */
@Component
public class SyncKerberosChooser extends AbstractKerberosChooser implements InitializingBean {

    @Autowired
    private ConsoleKerberosProjectService kerberosProjectService;

    @Autowired
    private UicKerberosApiClient uicKerberosApiClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        ProjectKerberosAuthentication projectKerberosAuthentication = new ProjectKerberosAuthentication(kerberosProjectService);
        UserKerberosAuthentication userKerberosAuthentication = new UserKerberosAuthentication(uicKerberosApiClient);
        SyncPartitionKerberosAuthentication syncPartitionKerberosAuthentication = new SyncPartitionKerberosAuthentication();

        add(userKerberosAuthentication);
        add(projectKerberosAuthentication);
        add(syncPartitionKerberosAuthentication);
    }
}