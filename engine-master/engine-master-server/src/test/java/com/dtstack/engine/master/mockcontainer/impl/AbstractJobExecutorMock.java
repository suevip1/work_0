package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.BatchFlowWorkJobService;
import com.dtstack.engine.master.impl.ScheduleFillDataJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: dazhi
 * @Date: 2022/6/28 9:12 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AbstractJobExecutorMock extends BaseMock {


    @MockInvoke(targetClass = ZkService.class)
    public String getLocalAddress() {
        return "127.0.0.1:8090";
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    Integer deleteByJobIdAndType( String jobId,Integer type){
        return 1;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobService.class)
    public List<ScheduleFillDataJob> getFillByFillIds(List<Long> fillIds) {
        ArrayList<ScheduleFillDataJob> scheduleFillDataJobs = Lists.newArrayList();
        ScheduleFillDataJob e = new ScheduleFillDataJob();
        e.setId(2L);
        e.setFillGeneratStatus(0);
        scheduleFillDataJobs.add(e);
        return scheduleFillDataJobs;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobService.class)
    public Integer decrementParallelNum(Long id) {
        return 0;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobService.class)
    public ScheduleFillDataJob getFillById(Long fillId) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        scheduleFillDataJob.setMaxParallelNum(2);
        return scheduleFillDataJob;
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    List<ScheduleJobOperatorRecord> listJobs(Long startId, String nodeAddress, Integer type) {
        List<ScheduleJobOperatorRecord> scheduleJobOperatorRecords = Lists.newArrayList();
        ScheduleJobOperatorRecord scheduleJobOperatorRecord1 = new ScheduleJobOperatorRecord();
        scheduleJobOperatorRecord1.setJobId("1");

        ScheduleJobOperatorRecord scheduleJobOperatorRecord2 = new ScheduleJobOperatorRecord();
        scheduleJobOperatorRecord2.setJobId("2");

        ScheduleJobOperatorRecord scheduleJobOperatorRecord3 = new ScheduleJobOperatorRecord();
        scheduleJobOperatorRecord3.setJobId("3");

        scheduleJobOperatorRecords.add(scheduleJobOperatorRecord1);
        scheduleJobOperatorRecords.add(scheduleJobOperatorRecord2);
        scheduleJobOperatorRecords.add(scheduleJobOperatorRecord3);
        return scheduleJobOperatorRecords;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        if ("1".equals(jobId)) {
            return null;
        }
        ScheduleJob scheduleJob = new ScheduleJob();

        scheduleJob.setStatus(8);
        scheduleJob.setFillType(1);
        return scheduleJob;
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listExecJobByJobIds(String nodeAddress,Integer phaseStatus,Integer isRestart,Collection<String> jobIds){
        ScheduleJob scheduleJob2 = new ScheduleJob();
        scheduleJob2.setAppType(1);
        scheduleJob2.setJobId("2");
        scheduleJob2.setTaskId(2L);
        scheduleJob2.setTaskType(EScheduleJobType.SYNC.getType());
        scheduleJob2.setResourceId(2L);
        scheduleJob2.setFillId(2L);

        List<ScheduleJob> scheduleJobs = Lists.newArrayList();
        scheduleJobs.add(scheduleJob2);
        return scheduleJobs;
    }
    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public Map<Integer,List<ScheduleTaskShade>> listTaskShadeByIdAndType(Map<Integer, Set<Long>> groupByAppMap){
        Map<Integer, List<ScheduleTaskShade>> map = Maps.newHashMap();
        List<ScheduleTaskShade> shades = new ArrayList<>();
        ScheduleTaskShade shade1 = new ScheduleTaskShade();
        shade1.setTaskId(1L);
        shade1.setAppType(1);
        shade1.setVersionId(1);
        shade1.setResourceId(1L);
        shade1.setTaskType(EScheduleJobType.WORK_FLOW.getType());

        ScheduleTaskShade shade2 = new ScheduleTaskShade();
        shade2.setTaskId(2L);
        shade2.setAppType(1);
        shade2.setVersionId(1);
        shade2.setResourceId(2L);
        shade2.setTaskType(EScheduleJobType.SYNC.getType());
        shade2.setScheduleConf("{'taskRuleTimeout':600000}");

        ScheduleTaskShade shade3 = new ScheduleTaskShade();
        shade3.setTaskId(3L);
        shade3.setAppType(1);
        shade3.setVersionId(1);
        shade3.setResourceId(3L);
        shade3.setTaskType(EScheduleJobType.NOT_DO_TASK.getType());

        shades.add(shade1);
        shades.add(shade2);
        shades.add(shade3);

        map.put(1,shades);
        return map;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public void removeOperatorRecord(Collection<String> jobIds, Collection<ScheduleJobOperatorRecord> records) {
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public static Long getListMinId(String left, String right) {
        String triggerTime = new DateTime(DateUtil.getTodayStart(System.currentTimeMillis())*1000).toString(DateUtil.UN_STANDARD_DATETIME_FORMAT);
        AtomicInteger count = new AtomicInteger();
        String substring = triggerTime.substring(2, triggerTime.length() - 2);
        String increasing = String.format("%09d", count.getAndIncrement());
        return Long.parseLong(substring+increasing);
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer getJobStatus(String jobId){
        return 4;
    }

    @MockInvoke(targetClass = BatchFlowWorkJobService.class)
    public boolean checkRemoveAndUpdateFlowJobStatus(ScheduleBatchJob scheduleBatchJob) {
        return Boolean.TRUE;
    }

    @MockInvoke(targetClass = BatchFlowWorkJobService.class)
    public void batchUpdateFlowSubJobStatus(ScheduleJob scheduleJob, Integer status) {
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJob> listByJobKey(String jobKey, Integer relyType) {
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Timestamp getJobExecStartTime(String jobId){
        return new Timestamp(System.currentTimeMillis()-100000);
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer updateStatusAndLogInfoAndExecTimeById(String jobId, Integer status, String msg,Date execStartTime,Date execEndTime){
       return 1;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        return Boolean.TRUE;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer updateStatusAndLogInfoById(String jobId, Integer status, String msg) {
        return 1;
    }

    @MockInvoke(targetClass = JobRichOperator.class)
    public JobCheckRunInfo checkJobCanRun(ScheduleBatchJob scheduleBatchJob, Integer status, Integer scheduleType,
                                          ScheduleTaskShade batchTaskShade) throws ParseException {
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();
        checkRunInfo.setStatus(JobCheckStatus.CAN_EXE);
        return checkRunInfo;
    }

    @MockInvoke(targetClass = JobRichOperator.class)
    public Pair<String, String> getCycTimeLimitEndNow(Boolean mindJobId) {
        // 当前时间
        String endTime = "1";
        String startTime = "2";
        return new ImmutablePair<>(startTime, endTime);
    }

    private Integer count = 0 ;
    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listExecJobByCycTimeTypeAddress(Long startId, String nodeAddress, Integer scheduleType, String cycStartTime, String cycEndTime, Integer phaseStatus,
                                                      Boolean isEq, Timestamp lastTime, Integer isRestart) {

        if (count!=0L) {
            return Lists.newArrayList();
        } else {
            count++;
        }
        ScheduleJob scheduleJob1 = new ScheduleJob();
        scheduleJob1.setAppType(1);
        scheduleJob1.setJobId("1");
        scheduleJob1.setTaskId(1L);
        scheduleJob1.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob1.setResourceId(2L);
        scheduleJob1.setVersionId(1);

        ScheduleJob scheduleJob2 = new ScheduleJob();
        scheduleJob2.setAppType(1);
        scheduleJob2.setJobId("1");
        scheduleJob2.setTaskId(2L);
        scheduleJob2.setTaskType(EScheduleJobType.SYNC.getType());
        scheduleJob2.setResourceId(2L);

        ScheduleJob scheduleJob3 = new ScheduleJob();
        scheduleJob3.setAppType(1);
        scheduleJob3.setJobId("1");
        scheduleJob3.setTaskId(3L);
        scheduleJob3.setTaskType(EScheduleJobType.NOT_DO_TASK.getType());
        scheduleJob3.setResourceId(2L);

        List<ScheduleJob> scheduleJobs = Lists.newArrayList();
        scheduleJobs.add(scheduleJob1);
        scheduleJobs.add(scheduleJob2);
        scheduleJobs.add(scheduleJob3);
        return scheduleJobs;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateStatusByJobId(String jobId, Integer status, Integer versionId, Date execStartTime, Date execEndTime) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateResourceIdByJobId(String jobId, Long resourceId) {
        return 1;
    }
}
