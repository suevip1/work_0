package com.dtstack.engine.master.security.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.po.ConsoleKerberosProject;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.enums.OpenKerberosEnum;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.google.common.base.Splitter;

public class ProjectKerberosAuthentication implements IKerberosAuthentication {

    public static final String PROJECT = "Project";

    private ConsoleKerberosProjectService consoleKerberosProjectService;

    public ProjectKerberosAuthentication(ConsoleKerberosProjectService consoleKerberosProjectService) {
        this.consoleKerberosProjectService = consoleKerberosProjectService;
    }

    public KerberosConfig getKerberosAuthenticationConfig(Long clusterId, Integer componentCode, String componentVersion, Integer taskType, Long projectId, Integer appType, Long userId, Long tenantId, JSONObject sftpConfig) {
        if (projectId == null) {
            return null;
        }
        ConsoleKerberosProject kerberosProject = consoleKerberosProjectService.findKerberosProject(projectId, appType);
        if (kerberosProject == null) {
            return null;
        }
        if (!OpenKerberosEnum.OPEN.getCode().equals(kerberosProject.getOpenKerberos())) {
            return null;
        }
        if (StringUtils.isBlank(kerberosProject.getTaskTypeList())) {
            return null;
        }
        boolean contains = Splitter.on(",").omitEmptyStrings().splitToList(kerberosProject.getTaskTypeList()).contains(taskType + "");
        if (!contains) {
            return null;
        }
        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setOpenKerberos(OpenKerberosEnum.OPEN.getCode());
        kerberosConfig.setRemotePath(kerberosProject.getRemotePath());
        kerberosConfig.setPrincipal(kerberosProject.getPrincipal());
        kerberosConfig.setName(kerberosProject.getKeytabName());
        kerberosConfig.setKrbName(kerberosProject.getConfName());
        kerberosConfig.setGmtModified(kerberosProject.getGmtModified());
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
        return PROJECT;
    }
}