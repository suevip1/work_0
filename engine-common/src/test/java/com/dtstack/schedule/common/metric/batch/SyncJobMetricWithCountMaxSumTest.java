package com.dtstack.schedule.common.metric.batch;

import com.dtstack.schedule.common.metric.MetricResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class SyncJobMetricWithCountMaxSumTest {

    private SyncJobMetricWithCountMaxSum syncJobMetricWithCountMaxSum = new SyncJobMetricWithCountMaxSum();

    @Test
    public void buildQueryInfo() {
        syncJobMetricWithCountMaxSum.buildQueryInfo();
    }

    @Test
    public void formatData() {
        syncJobMetricWithCountMaxSum.formatData(new MetricResult());
    }

    @Test
    public void getTagName() {
        syncJobMetricWithCountMaxSum.getTagName();
    }
}