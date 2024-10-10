package com.dtstack.schedule.common.metric.batch;

import com.dtstack.schedule.common.metric.prometheus.PrometheusMetricQuery;
import org.junit.Test;

import static org.junit.Assert.*;

public class MetricBuilderTest {

    private MetricBuilder metricBuilder = new MetricBuilder();

    @Test
    public void buildMetric() {
        MetricBuilder.buildMetric("test","1232",System.currentTimeMillis(),System.currentTimeMillis(),new PrometheusMetricQuery());
    }
}