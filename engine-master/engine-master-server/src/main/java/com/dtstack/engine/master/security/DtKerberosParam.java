package com.dtstack.engine.master.security;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-08 10:25
 */
public class DtKerberosParam {
    private Integer taskType;

    private Long projectId;

    private Integer appType;

    DtKerberosParam(Integer taskType, Long projectId, Integer appType) {
        this.taskType = taskType;
        this.projectId = projectId;
        this.appType = appType;
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

    public static KerberosParamBuilder builder() {
        return new KerberosParamBuilder();
    }

    public static class KerberosParamBuilder {
        private Integer taskType;
        private Long projectId;
        private Integer appType;

        KerberosParamBuilder() {
        }

        public KerberosParamBuilder taskType(Integer taskType) {
            this.taskType = taskType;
            return this;
        }

        public KerberosParamBuilder projectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public KerberosParamBuilder appType(Integer appType) {
            this.appType = appType;
            return this;
        }

        public DtKerberosParam build() {
            return new DtKerberosParam(taskType, projectId, appType);
        }
    }
}
