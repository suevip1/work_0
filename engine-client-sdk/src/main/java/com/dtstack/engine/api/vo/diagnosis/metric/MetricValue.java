package com.dtstack.engine.api.vo.diagnosis.metric;

public class MetricValue {
    private Double timestamp;
    private String value;

    public MetricValue(Double timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public String getValue() {
        return value;
    }
}
