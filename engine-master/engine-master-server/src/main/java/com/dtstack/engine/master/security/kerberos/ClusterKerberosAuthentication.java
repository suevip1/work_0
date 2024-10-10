package com.dtstack.engine.master.security.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.dao.KerberosDao;

public class ClusterKerberosAuthentication implements IKerberosAuthentication {

    public static final String CLUSTER = "Cluster";

    private KerberosDao kerberosDao;

    public ClusterKerberosAuthentication(KerberosDao kerberosDao) {
        this.kerberosDao = kerberosDao;
    }

    public KerberosConfig getKerberosAuthenticationConfig(Long clusterId, Integer componentCode, String componentVersion, Integer taskType, Long projectId, Integer appType, Long userId, Long tenantId, JSONObject sftpConfig) {
        return kerberosDao.getByComponentType(clusterId, componentCode, componentVersion);
    }

    @Override
    public KerberosConfig getKerberosAuthenticationConfig(KerberosReq kerberosReq) {
        return kerberosDao.getByComponentType(kerberosReq.getClusterId(), kerberosReq.getComponentCode(), kerberosReq.getComponentVersion());
    }

    @Override
    public String name() {
        return CLUSTER;
    }
}