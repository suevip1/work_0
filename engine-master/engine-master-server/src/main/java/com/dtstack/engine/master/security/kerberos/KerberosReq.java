package com.dtstack.engine.master.security.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.convert.Consistent;
import com.dtstack.engine.common.constrant.GlobalConst;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 17:50
 */
public class KerberosReq {

    private Long clusterId;

    private Integer componentCode;

    private String componentVersion;

    private Integer taskType;

    private Long projectId;

    private Integer appType;

    private Long userId;

    private Long tenantId;

    private JSONObject sftpConfig;

    private JSONObject hadoopConfig;

    private JSONObject dataJson;

    public JSONObject getDataJson() {
        return dataJson;
    }

    public JSONObject getHadoopConfig() {
        return hadoopConfig;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public Integer getComponentCode() {
        return componentCode;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public JSONObject getSftpConfig() {
        return sftpConfig;
    }

    KerberosReq(Long clusterId, Integer componentCode,
                String componentVersion, Integer taskType,
                Long projectId, Integer appType,
                Long userId, Long tenantId,
                JSONObject sftpConfig, JSONObject hadoopConfig, JSONObject dataJson) {
        this.clusterId = clusterId;
        this.componentCode = componentCode;
        this.componentVersion = componentVersion;
        this.taskType = taskType;
        this.projectId = projectId;
        this.appType = appType;
        this.userId = userId;
        this.tenantId = tenantId;
        this.sftpConfig = sftpConfig;
        this.hadoopConfig = hadoopConfig;
        this.dataJson = dataJson;
    }

    public static KerberosReqBuilder builder() {
        return new KerberosReqBuilder();
    }

    public String prettyLog() {
        return new ToStringBuilder(this)
                .append("clusterId", clusterId)
                .append("componentCode", componentCode)
                .append("componentVersion", componentVersion)
                .append("taskType", taskType)
                .append("projectId", projectId)
                .append("appType", appType)
                .append("userId", userId)
                .append("tenantId", tenantId)
                .toString();
    }

    public static class KerberosReqBuilder {
        private Long clusterId;
        private Integer componentCode;
        private String componentVersion;
        private Integer taskType;
        private Long projectId;
        private Integer appType;
        private Long userId;
        private Long tenantId;
        private JSONObject sftpConfig;
        private JSONObject hadoopConfig;
        private JSONObject dataJson;

        KerberosReqBuilder() {
        }

        public KerberosReqBuilder clusterId(Long clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public KerberosReqBuilder componentCode(Integer componentCode) {
            this.componentCode = componentCode;
            return this;
        }

        public KerberosReqBuilder componentVersion(String componentVersion) {
            this.componentVersion = componentVersion;
            return this;
        }

        public KerberosReqBuilder taskType(Integer taskType) {
            this.taskType = taskType;
            return this;
        }

        public KerberosReqBuilder projectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public KerberosReqBuilder appType(Integer appType) {
            this.appType = appType;
            return this;
        }

        public KerberosReqBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public KerberosReqBuilder tenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public KerberosReqBuilder sftpConfig(JSONObject sftpConfig) {
            this.sftpConfig = sftpConfig;
            return this;
        }

        public KerberosReqBuilder hadoopConfig(JSONObject hadoopConfig) {
            this.hadoopConfig = hadoopConfig;
            return this;
        }

        public KerberosReqBuilder dataJson(JSONObject dataJson) {
            this.dataJson = dataJson;
            return this;
        }

        public KerberosReq build() {
            return new KerberosReq(clusterId, componentCode,
                    componentVersion, taskType,
                    projectId, appType,
                    userId, tenantId,
                    sftpConfig, hadoopConfig, dataJson);
        }
    }
}
