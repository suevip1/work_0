package com.dtstack.schedule.common.metric.batch;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.OmniConstructor;
import com.dtstack.schedule.common.metric.MetricResult;
import com.dtstack.schedule.common.metric.QueryInfo;
import com.dtstack.schedule.common.metric.prometheus.PrometheusMetricQuery;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(BaseMetricTest.Mock.class)
public class BaseMetricTest {

    public static class Mock {

        @MockInvoke(targetClass = PrometheusMetricQuery.class)
        public MetricResult queryRange(String metricName, long startTime, long endTime, QueryInfo queryInfo, String tagName) {
            MetricResult metricResult = new MetricResult();
            return metricResult;
        }

        }

    private SyncJobMetric syncJobMetric = new SyncJobMetric();

    @Test
    public void getChartName() {
        syncJobMetric.getChartName();
    }

    @Test
    public void getMetric() {
        syncJobMetric.getMetric();
    }

    @Test
    public void formatData() {
        syncJobMetric.formatData(OmniConstructor.newInstance(MetricResult.class));
    }

    @Test
    public void setPrometheusMetricQuery() {
        syncJobMetric.setPrometheusMetricQuery(OmniConstructor.newInstance(PrometheusMetricQuery.class));
    }

    @Test
    public void setMetricName() {
        syncJobMetric.setMetricName("setMetricName");
    }

    @Test
    public void setJobId() {
        syncJobMetric.setJobId("sdsd");
    }

    @Test
    public void setStartTime() {
        syncJobMetric.setStartTime(System.currentTimeMillis());
    }

    @Test
    public void setEndTime() {
        syncJobMetric.setEndTime(System.currentTimeMillis());
    }

    @Test
    public void setGranularity() {
        syncJobMetric.setGranularity("");
    }

    @Test
    public void getTagName() {
        syncJobMetric.getTagName();
    }
}