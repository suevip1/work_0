package com.dtstack.engine.master.sync.restart;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.sync.AbstractRestart;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/11/18 下午7:15
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractRestartJob extends AbstractRestart {

    public AbstractRestartJob(ScheduleJobDao scheduleJobDao, EnvironmentContext environmentContext, ScheduleJobJobDao scheduleJobJobDao, ScheduleJobService scheduleJobService) {
        super(scheduleJobDao, environmentContext, scheduleJobJobDao, scheduleJobService);
    }

    /**
     * 计算resumeBatch集合
     *
     * @param jobs 重跑实例
     * @return
     */
    public abstract Map<String, String> computeResumeBatchJobs(List<ScheduleJob> jobs);
}
