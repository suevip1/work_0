package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 重跑任务的执行器
 */
@Component
public class RestartJobExecutor extends AbstractJobExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(RestartJobExecutor.class);

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    private Long operatorRecordStartId = 0L;

    @Override
    public EScheduleType getScheduleType() {
        return EScheduleType.RESTART;
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        LOGGER.info("---stop RestartJobExecutor----");
    }

    @Override
    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, Boolean isEq) {
        Integer fillJobExecutorJobLimit = environmentContext.getFillJobExecutorJobLimit();

        if (executorConfigDTO != null && executorConfigDTO.getScanNum() != null) {
            fillJobExecutorJobLimit = executorConfigDTO.getScanNum();
        }
        //添加需要重跑的数据
        List<ScheduleJobOperatorRecord> records = scheduleJobOperatorRecordDao.listJobs(operatorRecordStartId, nodeAddress, Lists.newArrayList(OperatorType.RESTART.getType()), fillJobExecutorJobLimit);
        if (CollectionUtils.isEmpty(records)) {
            operatorRecordStartId = 0L;
            return new ArrayList<>();
        }

        Optional<ScheduleJobOperatorRecord> max = records.stream().max(Comparator.comparing(ScheduleJobOperatorRecord::getId));
        max.ifPresent(scheduleJobOperatorRecord -> operatorRecordStartId = scheduleJobOperatorRecord.getId());
        records = removeOperatorRecord(records,OperatorType.RESTART.getType());
        List<String> jobIds = records.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toList());
        //insert ignore 重复操作 会导致scheduleJob和record表的nodeAddress不一致 这里以record为准
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByJobIds(null, JobPhaseStatus.CREATE.getCode(), Restarted.RESTARTED.getStatus(), jobIds);
        LOGGER.info("getRestartDataJob nodeAddress {} start scanning since when startId:{}  queryJobSize {} ", nodeAddress, startId, scheduleJobs.size());
        if (jobIds.size() > scheduleJobs.size()) {
            //check lost operator records can remove
            Set<String> needSubmit = scheduleJobs.stream().map(ScheduleJob::getJobId).collect(Collectors.toSet());
            jobIds.removeAll(needSubmit);
            scheduleJobService.removeOperatorRecord(jobIds, records);
        }
        return getScheduleBatchJobList(scheduleJobs);
    }

    @Override
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        return 0L;
    }
}
