package com.dtstack.engine.master.security.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.enums.OpenKerberosEnum;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.pubsvc.sdk.usercenter.client.UicKerberosApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.KerberosConfigVo;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.KerberosDataVo;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.KerberosParam;
import com.dtstack.sdk.core.common.ApiResponse;

public class UserKerberosAuthentication implements IKerberosAuthentication {

    public static final String USER = "User";

    private UicKerberosApiClient uicKerberosApiClient;

    public UserKerberosAuthentication(UicKerberosApiClient uicKerberosApiClient) {
        this.uicKerberosApiClient = uicKerberosApiClient;
    }

    public KerberosConfig getKerberosAuthenticationConfig(Long clusterId, Integer componentCode, String componentVersion, Integer taskType, Long projectId, Integer appType, Long userId, Long tenantId, JSONObject sftpConfig) {
        if (userId == null) {
            return null;
        }
        /**
         * 默认kerberos的type
         */
        KerberosParam kerberosParam = new KerberosParam();
        kerberosParam.setTenantId(tenantId);
        kerberosParam.setUserId(userId);
        kerberosParam.setFileType(1L);
        ApiResponse<KerberosDataVo> kerberosByTenantIdAndUserId = null;
        try {
            kerberosByTenantIdAndUserId = uicKerberosApiClient.getKerberosByTenantIdAndUserId(kerberosParam);
        } catch (Exception e) {
            LOGGER.error("get userId {} kerberos config fail ", userId, e);
        }
        if (kerberosByTenantIdAndUserId == null) {
            return null;
        }
        if (kerberosByTenantIdAndUserId.getData() == null) {
            return null;
        }
        if (sftpConfig == null) {
            return null;
        }
        KerberosDataVo kerberosDataVo = kerberosByTenantIdAndUserId.getData();
        KerberosConfigVo kerberosConfigVo = kerberosDataVo.getKerberosConfigVo();
        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setKrbName(kerberosConfigVo.getKrb5ConfName());
        kerberosConfig.setPrincipal(kerberosConfigVo.getPrincipal());
        //业务中心 keytab 放的是相对路径
        String sftpPath = sftpConfig.getString(GlobalConst.PATH);
        if (sftpPath.endsWith(GlobalConst.SEPARATOR) || kerberosConfigVo.getPath().startsWith(GlobalConst.SEPARATOR)) {
            kerberosConfig.setRemotePath(sftpPath + kerberosConfigVo.getPath());
        } else {
            kerberosConfig.setRemotePath(sftpPath + GlobalConst.SEPARATOR + kerberosConfigVo.getPath());
        }
        kerberosConfig.setName(kerberosConfigVo.getKeytabName());
        kerberosConfig.setOpenKerberos(OpenKerberosEnum.OPEN.getCode());
        kerberosConfig.setGmtModified(kerberosConfigVo.getModifyTime());
        return kerberosConfig;
    }

    @Override
    public KerberosConfig getKerberosAuthenticationConfig(KerberosReq kerberosReq) {
        return getKerberosAuthenticationConfig(kerberosReq.getClusterId(), kerberosReq.getComponentCode(),
                kerberosReq.getComponentVersion(), kerberosReq.getTaskType(),
                kerberosReq.getProjectId(), kerberosReq.getAppType(),
                kerberosReq.getUserId(), kerberosReq.getTenantId(),
                kerberosReq.getSftpConfig());
    }

    @Override
    public String name() {
        return USER;
    }
}