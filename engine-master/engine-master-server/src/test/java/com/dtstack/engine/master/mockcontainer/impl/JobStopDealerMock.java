package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 3:19 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobStopDealerMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    public Long insert(ScheduleJobOperatorRecord engineJobStopRecord) {
        return 1L;
    }


    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    public List<String> listStopRecordByJobIds(@Param("jobIds") List<String> jobIds) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleJobOperateService.class)
    public Boolean addScheduleJobOperate(String jobId, Integer operateType, String operateContent, Long operateId) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    List<String> listByJobIds(List<String> jobIds) {
        List<String> ids = Lists.newArrayList();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        return ids;
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    Integer delete(@Param("id") Long id){
        return 1;
    }



    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    List<ScheduleJobOperatorRecord> listStopJob(Long startId) {
        if (startId!=0L) {
            return Lists.newArrayList();
        }

        List<ScheduleJobOperatorRecord> scheduleJobOperatorRecords = Lists.newArrayList();
        ScheduleJobOperatorRecord scheduleJobOperatorRecord = new ScheduleJobOperatorRecord();
        scheduleJobOperatorRecord.setId(1);
        scheduleJobOperatorRecord.setJobId("1");
        scheduleJobOperatorRecord.setGmtCreate(new Timestamp(System.currentTimeMillis()+20000));
        scheduleJobOperatorRecords.add(scheduleJobOperatorRecord);
        return scheduleJobOperatorRecords;
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    Integer updateOperatorExpiredVersion(Long id, Timestamp operatorExpired, Integer version){
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateJobStatusByIds(Integer status, List<String> jobIds){
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateJobStatusANdExceTimeByIds(Integer status,List<String> jobIds){
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId){
        ScheduleJob scheduleJob = new ScheduleJob();

        return scheduleJob;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<EngineJobCache> getByJobIds(List<String> jobIds) {
        List<EngineJobCache> engineJobCaches = Lists.newArrayList();
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId("1");
        engineJobCache.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        engineJobCaches.add(engineJobCache);
        return engineJobCaches;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    EngineJobCache getOne(@Param("jobId")String jobId) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        engineJobCache.setJobInfo("{\"taskType\":1,\"computeType\":1,\"maxRetryNum\":10,\"engineType\":\"kylin\",\"pluginInfo\":{}}");
        engineJobCache.setEngineType("kylin");
        return engineJobCache;
    }


    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    Timestamp getJobCreateTimeById(@Param("id") Long id) {
        return new Timestamp(System.currentTimeMillis() + 1000L);
    }


}
