package com.dtstack.engine.master.sync.restart;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.sync.restart.AbstractRestartJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2022/8/8
 */
public class SetSuccessFully extends AbstractRestartJob {


    private static final String LOGGER_MAG = "%s:置成功不恢复调度\n";

    public SetSuccessFully(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
    }

    @Override
    public Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs) {
        for (ScheduleJob job : jobs) {
            setSuccess(job,null,String.format(LOGGER_MAG,job.getJobName()));
        }
        return new HashMap<>(0);
    }
}
