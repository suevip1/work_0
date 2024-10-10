package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobRestartDealer;
import com.dtstack.engine.master.jobdealer.JobSubmitDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 2:31 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobSubmittedDealerMock {

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateJobSubmitSuccess(String jobId,  String engineId, String appId){
    }

    @MockInvoke(targetClass = JobSubmitDealer.class)
    public static LinkedBlockingQueue<JobClient> getSubmittedQueue() {
        LinkedBlockingQueue<JobClient> submittedQueue = new LinkedBlockingQueue<>();

        try {
            submittedQueue.put(new JobClient());


            JobClient e = new JobClient();
            JobResult jobResult = new JobResult();
            jobResult.setData("extid","1234567");
            e.setJobResult(jobResult);
            e.setEngineTaskId("12345");
            e.setCallBack((status)->{});
            submittedQueue.put(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return submittedQueue;
    }

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    int delete(String jobId){
        return 1;
    }

    @MockInvoke(targetClass = ShardCache.class)
    public boolean updateLocalMemTaskStatus(String jobId, Integer status, Consumer<String> consumer) {
        return true;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public void updateCache(JobClient jobClient, int stage) {
    }


    @MockInvoke(targetClass = JobRestartDealer.class)
    public boolean checkAndRestartForSubmitResult(JobClient jobClient){
        return false;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void jobFail( String jobId,  int status){

    }

    @MockInvoke(targetClass = ScheduleJobHistoryService.class)
    public void insertScheduleJobHistory(JobClient jobClient, String applicationId) {
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer updateExtraInfoAndLog( String jobId,  String jobExtraInfo, String logInfo,
                                   String engineLog,  String runSqlText){
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobExpandDao.class)
    Integer updateLogInfoByJobId(String jobId, String logInfo){
        return 1;
    }
}
