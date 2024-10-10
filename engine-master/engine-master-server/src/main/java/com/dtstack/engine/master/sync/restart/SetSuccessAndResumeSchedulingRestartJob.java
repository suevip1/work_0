package com.dtstack.engine.master.sync.restart;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:23
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SetSuccessAndResumeSchedulingRestartJob extends AbstractRestartJob {

    public SetSuccessAndResumeSchedulingRestartJob(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
    }

    private static final String LOGGER_MAG = "%s:置成功并恢复调度,时间:%s\n";

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        Map<String, String> resumeBatchJobs = new HashMap<>();
        for (ScheduleJob job : jobs) {
            Map<String, String> allChildJobWithSameDayByForkJoin = scheduleJobService.getAllChildJobWithSameDayByForkJoin(job.getJobId(), false);
            setSuccess(job, allChildJobWithSameDayByForkJoin,String.format(LOGGER_MAG,job.getJobName(),
                    new DateTime().toString(DateUtil.STANDARD_DATETIME_FORMAT)));

            if (MapUtils.isNotEmpty(allChildJobWithSameDayByForkJoin)) {
                resumeBatchJobs.putAll(allChildJobWithSameDayByForkJoin);
            }
            if (!"0".equalsIgnoreCase(job.getFlowJobId())) {
                ScheduleJob workFlowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), null);
                if (null != workFlowJob) {
                    resumeBatchJobs.put(workFlowJob.getJobId(), workFlowJob.getCycTime());
                }
            }
        }
        return resumeBatchJobs;
    }
}
