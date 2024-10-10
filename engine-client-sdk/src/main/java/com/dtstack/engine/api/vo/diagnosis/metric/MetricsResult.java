package com.dtstack.engine.api.vo.diagnosis.metric;

import java.util.List;

public class MetricsResult {
    /**
     applicationId
     */
    private String applicationId;

    /**
     * 指标汇总
     */
    private List<Metric> metrics;

    /**
     * 建议汇总
     */
    private List<Suggestion> suggestions;


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String applicationId;
        private List<Metric> metrics;
        private List<Suggestion> suggestions;

        public MetricsResult build() {
            MetricsResult result = new MetricsResult();
            result.applicationId = applicationId;
            result.metrics = metrics;
            result.suggestions = suggestions;
            return result;
        }

        public Builder withApplicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder withMetrics(List<Metric> metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder withSuggestions(List<Suggestion> suggestions) {
            this.suggestions = suggestions;
            return this;
        }
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public String toString() {
        return "MetricsResult{" +
                "applicationId='" + applicationId + '\'' +
                ", metrics=" + metrics +
                ", suggestions=" + suggestions +
                '}';
    }
}
