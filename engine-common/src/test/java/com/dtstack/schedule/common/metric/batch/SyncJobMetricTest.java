package com.dtstack.schedule.common.metric.batch;

import org.junit.Test;

import static org.junit.Assert.*;

public class SyncJobMetricTest {

    private SyncJobMetric syncJobMetric = new SyncJobMetric();

    @Test
    public void buildQueryInfo() {
        syncJobMetric.buildQueryInfo();
    }
}