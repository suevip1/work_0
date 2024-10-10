package com.dtstack.schedule.common.metric.batch;

import org.junit.Test;

import static org.junit.Assert.*;

public class SyncJobMetricWithSumTest {

    private SyncJobMetricWithSum syncJobMetricWithSum = new SyncJobMetricWithSum();

    @Test
    public void buildQueryInfo() {
        syncJobMetricWithSum.buildQueryInfo();
    }
}