package com.dtstack.engine.master.security;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.dtstack.engine.master.security.chooser.DefaultKerberosChooser;
import com.dtstack.engine.master.security.kerberos.ClusterKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.KerberosReq;
import com.dtstack.engine.master.security.kerberos.ProjectKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.UserKerberosAuthentication;
import com.dtstack.pubsvc.sdk.usercenter.client.UicKerberosApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
public class KerberosHandler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KerberosHandler.class);

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private ConsoleKerberosProjectService kerberosProjectService;

    @Autowired
    private UicKerberosApiClient uicKerberosApiClient;

    private List<IKerberosAuthentication> kerberosAuthentications = new LinkedList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        ClusterKerberosAuthentication clusterKerberosAuthentication = new ClusterKerberosAuthentication(kerberosDao);
        ProjectKerberosAuthentication projectKerberosAuthentication = new ProjectKerberosAuthentication(kerberosProjectService);
        UserKerberosAuthentication userKerberosAuthentication = new UserKerberosAuthentication(uicKerberosApiClient);
        kerberosAuthentications.add(userKerberosAuthentication);
        kerberosAuthentications.add(projectKerberosAuthentication);
        kerberosAuthentications.add(clusterKerberosAuthentication);
    }

    public List<IKerberosAuthentication> allKerberosAuthentications() {
        return Collections.unmodifiableList(kerberosAuthentications);
    }

    /**
     * 迁移到 {@link DefaultKerberosChooser#authentication(com.dtstack.engine.master.security.kerberos.KerberosReq)}
     * @param clusterId
     * @param componentCode
     * @param componentVersion
     * @param taskType
     * @param projectId
     * @param appType
     * @param userId
     * @param tenantId
     * @param sftpConfig
     * @return
     */
    @Deprecated
    public KerberosConfig authentication(Long clusterId, Integer componentCode, String componentVersion, Integer taskType, Long projectId, Integer appType
            , Long userId, Long tenantId, JSONObject sftpConfig) {
        Iterator<IKerberosAuthentication> iterator = kerberosAuthentications.iterator();
        while (iterator.hasNext()) {
            IKerberosAuthentication kerberosAuthentication = iterator.next();
            KerberosReq kerberosReq = KerberosReq.builder()
                    .clusterId(clusterId)
                    .componentCode(componentCode)
                    .componentVersion(componentVersion)
                    .taskType(taskType)
                    .projectId(projectId)
                    .appType(appType)
                    .userId(userId)
                    .tenantId(tenantId)
                    .sftpConfig(sftpConfig).build();
            KerberosConfig kerberosAuthenticationConfig = kerberosAuthentication.getKerberosAuthenticationConfig(kerberosReq);
            if (kerberosAuthenticationConfig != null) {
                LOGGER.info("kerberos handler get userId {} tenantId {} projectId {} appType {} componentVersion {} componentCode {} handler class  is {}",
                        userId, tenantId, projectId, appType, componentVersion, componentCode, kerberosAuthentication.getClass());
                return kerberosAuthenticationConfig;
            }
        }
        return null;
    }

    /**
     * 迁移到 {@link DtKerberosUtil#parse2Json(com.dtstack.engine.api.domain.KerberosConfig)}
     * @param pluginJson
     * @param authentication
     */
    @Deprecated
    public void setKerberos(JSONObject pluginJson, KerberosConfig authentication) {
        pluginJson.fluentPut("openKerberos", null != authentication.getOpenKerberos() && authentication.getOpenKerberos() > 0)
                .fluentPut("remoteDir", authentication.getRemotePath())
                .fluentPut("principalFile", authentication.getName())
                .fluentPut("principal", authentication.getPrincipal())
                .fluentPut("krbName", authentication.getKrbName())
                .fluentPut("kerberosFileTimestamp", authentication.getGmtModified())
                .fluentPut("mergeKrbContent", authentication.getMergeKrbContent());
    }
}