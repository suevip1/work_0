package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JobGraphUtilsTest {

    @Test
    public void buildFlowReplaceId() {
        JobGraphUtils.buildFlowReplaceId(1L, "20220627", 8);
    }

    @Test
    public void doSetFlowJobIdForSubTasks() {
        List<ScheduleBatchJob> jobList = new ArrayList<>();
        ScheduleJob job1 = new ScheduleJob();
        job1.setFlowJobId("1");
        ScheduleBatchJob job = new ScheduleBatchJob(job1);
        Map<String, String> flowJobId = new HashMap<>();
        flowJobId.put("1","1");
        jobList.add(job);
        JobGraphUtils.doSetFlowJobIdForSubTasks(jobList,flowJobId);
    }

    @Test
    public void generateBizDateFromCycTime() {
    }

    @Test
    public void parseCycTimeFromJobKey() {
    }

    @Test
    public void parseScheduleTypeFromJobKey() {
    }

    @Test
    public void getPrePeriodJobTriggerDateStr() {
    }

    @Test
    public void generateJobKey() {
    }

    @Test
    public void getPreJob() {
    }

    @Test
    public void getSortDayList() {
    }

    @Test
    public void getSortTimeList() {
    }

    @Test
    public void getFatherLastJobBusinessDate() {
    }

    @Test
    public void getCloseInDateTimeOfMonth() {
    }

    @Test
    public void getCloseInDateTimeOfWeek() {
    }

    @Test
    public void getCloseInDateTimeOfDay() {
    }

    @Test
    public void getCloseInDateTimeOfHour() {
    }

    @Test
    public void getCloseInDateTimeOfMin() {
    }
}