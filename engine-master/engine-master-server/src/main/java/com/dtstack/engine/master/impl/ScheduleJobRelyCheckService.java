package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobRelyCheckDao;
import com.dtstack.engine.po.ScheduleJobRelyCheck;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 10:30 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobRelyCheckService {

    @Autowired
    private ScheduleJobRelyCheckDao scheduleJobRelyCheckDao;

    @Autowired
    private EnvironmentContext environmentContext;

    public Integer insertBatch(List<ScheduleJobRelyCheck> scheduleJobRelyCheckList) {
        if (CollectionUtils.isNotEmpty(scheduleJobRelyCheckList)) {
            if (scheduleJobRelyCheckList.size() > environmentContext.getBatchJobJobInsertSize()) {
                List<List<ScheduleJobRelyCheck>> partition = Lists.partition(scheduleJobRelyCheckList, environmentContext.getBatchJobJobInsertSize());

                Integer count = 0;
                for (List<ScheduleJobRelyCheck> scheduleJobRelyChecks : partition) {
                    count += scheduleJobRelyCheckDao.insertBatch(scheduleJobRelyChecks);
                }

                return count;
            } else  {
                return scheduleJobRelyCheckDao.insertBatch(scheduleJobRelyCheckList);
            }
        }
        return 0;
    }

    public Integer deleteByJobId(String jobId) {
        if (StringUtils.isNotBlank(jobId)) {
            return scheduleJobRelyCheckDao.deleteByJobId(jobId);
        }
        return 0;
    }

    public Integer deleteByJobIds(List<String> jobIds) {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            return scheduleJobRelyCheckDao.deleteByJobIds(jobIds);
        }

        return 0;
    }

    public Integer deleteByParentJobIds(List<String> jobIds) {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            return scheduleJobRelyCheckDao.deleteByParentJobIds(jobIds);
        }

        return 0;
    }

    public List<ScheduleJobRelyCheck> findByJobId(String jobId) {
        if (StringUtils.isNotBlank(jobId)) {
            return scheduleJobRelyCheckDao.findByJobId(jobId);
        }

        return Lists.newArrayList();
    }

    public List<ScheduleJobRelyCheck> findByJobIds(List<String> jobIds) {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            return scheduleJobRelyCheckDao.findByJobIds(jobIds);
        }

        return Lists.newArrayList();
    }

    public List<ScheduleJobRelyCheck> findByParentsJobIds(List<String> parentJobIds) {
        if (CollectionUtils.isNotEmpty(parentJobIds)) {
            return scheduleJobRelyCheckDao.findByParentsJobIds(parentJobIds);
        }

        return Lists.newArrayList();
    }


}
