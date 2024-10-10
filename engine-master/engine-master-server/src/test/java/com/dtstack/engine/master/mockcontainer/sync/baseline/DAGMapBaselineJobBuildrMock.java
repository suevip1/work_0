package com.dtstack.engine.master.mockcontainer.sync.baseline;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.dto.BaselineJobJobDTO;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.mapstruct.BaselineJobStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.BaselineJobJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 3:02 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class DAGMapBaselineJobBuildrMock extends BaseMock {


    private Map<String,ScheduleJob> scheduleJobMap = Maps.newHashMap();

    public DAGMapBaselineJobBuildrMock() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("1");
        scheduleJob.setAppType(1);
        scheduleJob.setTaskId(1L);
        scheduleJob.setJobKey("1");
        scheduleJob.setCycTime("20221212000000");
        scheduleJobMap.put("1",scheduleJob);

        ScheduleJob scheduleJob2 = new ScheduleJob();
        scheduleJob2.setJobId("2");
        scheduleJob2.setAppType(1);
        scheduleJob2.setTaskId(2L);
        scheduleJob2.setJobKey("2");
        scheduleJob2.setCycTime("20221212010000");
        scheduleJobMap.put("2",scheduleJob2);

        ScheduleJob scheduleJob3 = new ScheduleJob();
        scheduleJob3.setJobId("3");
        scheduleJob3.setAppType(1);
        scheduleJob3.setTaskId(3L);
        scheduleJob3.setJobKey("3");
        scheduleJob3.setCycTime("20221212020000");
        scheduleJobMap.put("3",scheduleJob3);

        ScheduleJob scheduleJob4 = new ScheduleJob();
        scheduleJob4.setJobId("4");
        scheduleJob4.setAppType(1);
        scheduleJob4.setTaskId(4L);
        scheduleJob4.setJobKey("4");
        scheduleJob4.setCycTime("20221212030000");
        scheduleJobMap.put("4",scheduleJob4);

        ScheduleJob scheduleJob5 = new ScheduleJob();
        scheduleJob5.setJobId("5");
        scheduleJob5.setAppType(1);
        scheduleJob5.setTaskId(5L);
        scheduleJob5.setJobKey("5");
        scheduleJob5.setCycTime("20221212040000");
        scheduleJobMap.put("5",scheduleJob5);

        ScheduleJob scheduleJob6 = new ScheduleJob();
        scheduleJob6.setJobId("6");
        scheduleJob6.setAppType(1);
        scheduleJob6.setTaskId(6L);
        scheduleJob6.setJobKey("6");
        scheduleJob6.setCycTime("20221212050000");
        scheduleJobMap.put("6",scheduleJob6);
    }

    @MockInvoke(targetClass = BaselineJobStruct.class)
    public BaselineJobJob toBaselineJobJob(BaselineJobJobDTO baselineJobJob){
        return new BaselineJobJob();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJob> listJobByJobKeys(List<String> parentJobKeys) {
        List<ScheduleJob> scheduleJobs = Lists.newArrayList();

        for (String parentJobKey : parentJobKeys) {
            ScheduleJob scheduleJob = scheduleJobMap.get(parentJobKey);
            if (scheduleJob != null) {
                scheduleJobs.add(scheduleJob);
            }
        }

        return scheduleJobs;
    }


    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<Integer> selectExecTimeByTaskIdAndAppType(Long taskId, Integer appType, Integer status, Integer maxBaselineJobNum) {
        return Lists.newArrayList(23,44,23,44,23,54,34,23,567,32,23,45);
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJob> selectJobByTaskIdAndJobExecuteOrder(Long start, Long end, List<Long> taskIds, Integer appType) {
        ScheduleJob job = new ScheduleJob();
        job.setJobKey("1");
        List<ScheduleJob> scheduleJobs = Lists.newArrayList();
        scheduleJobs.add(job);
        return scheduleJobs;
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    public List<ScheduleJobJob> listByJobKeys(Collection<String> jobKeys, Integer relyType) {
        List<ScheduleJobJob> scheduleJobJobs = Lists.newArrayList();
        if (jobKeys.size() == 1) {
            ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
            scheduleJobJob.setJobKey("1");
            scheduleJobJob.setParentJobKey("2");
            scheduleJobJobs.add(scheduleJobJob);

            ScheduleJobJob scheduleJobJob1 = new ScheduleJobJob();
            scheduleJobJob1.setJobKey("1");
            scheduleJobJob1.setParentJobKey("3");
            scheduleJobJobs.add(scheduleJobJob1);
        }

        if (jobKeys.size() == 2) {
            ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
            scheduleJobJob.setJobKey("2");
            scheduleJobJob.setParentJobKey("4");
            scheduleJobJobs.add(scheduleJobJob);

            ScheduleJobJob scheduleJobJob1 = new ScheduleJobJob();
            scheduleJobJob1.setJobKey("3");
            scheduleJobJob1.setParentJobKey("5");
            scheduleJobJobs.add(scheduleJobJob1);

            ScheduleJobJob scheduleJobJob2 = new ScheduleJobJob();
            scheduleJobJob2.setJobKey("3");
            scheduleJobJob2.setParentJobKey("6");
            scheduleJobJobs.add(scheduleJobJob2);
        }

        return scheduleJobJobs;
    }




}
