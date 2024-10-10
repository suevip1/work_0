package com.dtstack.engine.api.vo.diagnosis.metric;

import java.util.List;

public class Metric {

    private Metric() {
    }

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标类型
     */
    private String metricType;

    /**
     * applicationId
     */
    private String applicationId;

    /**
     * 角色，取值为driver or executor
     */
    private String role;

    /**
     * 角色编号
     */
    private String roleId;

    /**
     * 取值为0.5、0.75、0.95、0.98、0.99、0.999
     * quantile可为空字符串，当quantile为空字符串，说明该指标不是summary或者timer类型
     */
    private String quantile;

    /**
     * driver or executor所在服务器主机名
     */
    private String server;

    /**
     * 指标值 按时间顺序排列
     */
    private List<MetricValue> values;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getQuantile() {
        return quantile;
    }

    public void setQuantile(String quantile) {
        this.quantile = quantile;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<MetricValue> getValues() {
        return values;
    }

    public void setValues(List<MetricValue> values) {
        this.values = values;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String metricName;
        private String metricType;
        private String applicationId;
        private String role;
        private String roleId;
        private String quantile;
        private String server;
        private List<MetricValue> values;

        public Metric build() {
            Metric metric = new Metric();
            metric.metricName = metricName;
            metric.metricType = metricType;
            metric.applicationId = applicationId;
            metric.role = role;
            metric.roleId = roleId;
            metric.quantile = quantile;
            metric.server = server;
            metric.values = values;
            return metric;
        }

        public Builder withMetricName(String metricName) {
            this.metricName = metricName;
            return this;
        }

        public Builder withMetricType(String metricType) {
            this.metricType = metricType;
            return this;
        }

        public Builder withApplicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder withRole(String role) {
            this.role = role;
            return this;
        }

        public Builder withRoleId(String roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder withServer(String server) {
            this.server = server;
            return this;
        }

        public Builder withMetricInfo(MetricsResponse.MetricResult result) {
            this.metricName = result.queryMetricName();
            this.applicationId = result.queryApplicationId();
            this.role = result.queryRole();
            this.roleId = result.queryRoleId();
            this.quantile = result.queryQuantile();
            this.server = result.queryServer();
            return this;
        }

        public Builder withValues(List<MetricValue> values) {
            this.values = values;
            return this;
        }
    }
}
