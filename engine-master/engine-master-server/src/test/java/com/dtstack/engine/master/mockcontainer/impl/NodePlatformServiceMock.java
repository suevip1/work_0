package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobStatusCallbackFailDao;
import com.dtstack.engine.dao.ScheduleJobStatusMonitorDao;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.ScheduleJobStatusCallbackFail;
import com.dtstack.engine.po.ScheduleJobStatusMonitor;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-28 16:54
 */
public class NodePlatformServiceMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        if (jobId.equals("jobId")) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobId(jobId);
            scheduleJob.setAppType(13);
            scheduleJob.setTaskType(0);
            scheduleJob.setStatus(5);
            return scheduleJob;
        }
        return null;
    }

    @MockInvoke(targetClass = ScheduleJobStatusMonitorDao.class)
    List<ScheduleJobStatusMonitor> listAll() {
        ScheduleJobStatusMonitor monitor = mockMonitor();
        return Lists.newArrayList(monitor);
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    ScheduleJobStatusCallbackFail selectByJobIdAndMonitorId(String jobId, Integer monitorId) {
        ScheduleJobStatusCallbackFail fail = new ScheduleJobStatusCallbackFail();
        fail.setJobId("jobId");
        fail.setRetryNum(3);
        fail.setMonitorId(1);
        fail.setId(1);
        fail.setGmtCreate(new Date());
        fail.setGmtModified(new Date());
        return fail;
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    int updateByPrimaryKeySelective(ScheduleJobStatusCallbackFail record) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    Integer checkExists() {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    void deleteWhenNoAppType() {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    void deleteByThreshold(Integer failRemainLimitInDay,  Integer failMaxRetryNum) {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    int deleteByPrimaryKey(Integer id) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobStatusCallbackFailDao.class)
    List<ScheduleJobStatusCallbackFail> listByAppTypeKey(String appTypeKey) {
        ScheduleJobStatusCallbackFail fail = new ScheduleJobStatusCallbackFail();
        fail.setJobId("jobId");
        fail.setRetryNum(3);
        fail.setMonitorId(1);
        fail.setId(1);
        fail.setGmtCreate(new Date());
        fail.setGmtModified(new Date());
        return Lists.newArrayList(fail);
    }

    @MockInvoke(targetClass = ScheduleJobStatusMonitorDao.class)
    int updateByPrimaryKeySelective(ScheduleJobStatusMonitor record) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobStatusMonitorDao.class)
    ScheduleJobStatusMonitor getByAppTypeKey(String appTypeKey) {
        return mockMonitor();
    }

    @MockInvoke(targetClass = RedisTemplate.class)
    public <K> Boolean expire(K key, final long timeout, final TimeUnit unit) {
        return true;
    }

    @MockInvoke(targetClass = RedisTemplate.class)
    public HashOperations opsForHash() {
        return (HashOperations) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                HashOperations.class
        }, (proxy, method, args) -> {
            return null;
        });
    }

    @MockInvoke(targetClass = PoolHttpClient.class)
    public static String post(String url, Object bodyData) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(Boolean.TRUE);
        response.setMessage("");
        return JSONObject.toJSONString(response);
    }

    public static ScheduleJobStatusMonitor mockMonitor() {
        ScheduleJobStatusMonitor monitor = new ScheduleJobStatusMonitor();
        monitor.setListenerPolicy("{\"listenerAppType\":[\"RDOS\"],\"listenerJobType\":[\"SPARK_SQL\",\"HIVE_SQL\",\"LIBRA_SQL\",\"IMPALA_SQL\",\"ANALYTICDB_FOR_PG\",\"MYSQL\",\"TIDB_SQL\",\"SYNC\"],\"listenerStatus\":[5,12]}");
        monitor.setCallbackUrl("http://172.16.20.16:8875/dmetadata/v1/callback/engine");
        monitor.setAppTypeKey("METADATA");
        monitor.setId(1);
        return monitor;
    }
}
