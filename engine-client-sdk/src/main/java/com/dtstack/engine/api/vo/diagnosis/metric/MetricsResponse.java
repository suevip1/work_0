package com.dtstack.engine.api.vo.diagnosis.metric;

import java.util.List;

public class MetricsResponse {
    String status;
    Result data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Result getData() {
        return data;
    }

    public void setData(Result data) {
        this.data = data;
    }

    public String getResultType() {
        return this.getData().getResultType();
    }


    public static class Result {
        String resultType;
        List<MetricResult> result;

        public Result() {

        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public List<MetricResult> getResult() {
            return result;
        }

        public void setResult(List<MetricResult> result) {
            this.result = result;
        }
    }

    public static class MetricResult {
        MetricInfo metric;
        List<Object> value;

        public MetricResult() {

        }

        public String queryMetricName() {
            return this.getMetric().get__name__();
        }

        public String queryApplicationId() {
            return this.getMetric().getJob();
        }

        public String queryRole() {
            return this.getMetric().getRole();
        }

        public String queryRoleId() {
            return this.getMetric().getNumber();
        }

        public String queryQuantile() {
            return this.getMetric().getQuantile();
        }

        public String queryServer() {
            return this.getMetric().getInstance();
        }

        public MetricInfo getMetric() {
            return metric;
        }

        public void setMetric(MetricInfo metric) {
            this.metric = metric;
        }

        public MetricValue getValue() {
            Double timestamp = (Double) value.get(0);
            String realValue = value.get(1) + "";
            return new MetricValue(timestamp, realValue);
        }

        public void setValue(List<Object> value) {
            this.value = value;
        }
    }

    public static class MetricInfo {
        String __name__;

        String instance;
        String job;
        String number;
        String quantile;
        String role;

        public MetricInfo() {

        }

        public String get__name__() {
            return __name__;
        }

        public void set__name__(String __name__) {
            this.__name__ = __name__;
        }

        public String getInstance() {
            return instance;
        }

        public void setInstance(String instance) {
            this.instance = instance;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getQuantile() {
            return quantile;
        }

        public void setQuantile(String quantile) {
            this.quantile = quantile;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "Metric{" +
                    "metricName='" + __name__ + '\'' +
                    ", instance='" + instance + '\'' +
                    ", job='" + job + '\'' +
                    ", number='" + number + '\'' +
                    ", quantile='" + quantile + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MetricsResponse{" +
                "status='" + status + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
