package com.dtstack.engine.master.sync.restart;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:33
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RestartCurrentAndDownStreamNodeRestartJob extends AbstractRestartJob {

    public RestartCurrentAndDownStreamNodeRestartJob(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService,Long operateId) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
    }

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        Map<String, String> resumeBatchJobs = new HashMap<>(jobs.stream().collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getCycTime)));

        for (ScheduleJob job : jobs) {
            Map<String, String> allChildJobWithSameDayByForkJoin = scheduleJobService.getAllChildJobWithSameDayByForkJoin(job.getJobId(), false);
            if (MapUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                resumeBatchJobs.putAll(allChildJobWithSameDayByForkJoin);
            }
        }

        return resumeBatchJobs;
    }
}
