package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.NodeRecoverService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.queue.JobPartitioner;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobGraphBuilderTrigger;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.SimpleScheduleJobPO;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/6/26 8:52 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FailoverStrategyMock extends BaseMock {

    private Integer count = 0;

    @MockInvoke(targetClass = EngineJobCacheDao.class)
    List<EngineJobCache> listByStage(Long id, String nodeAddress, Integer stage, String jobResource) {
        if (count != 0) {
            return Lists.newArrayList();
        }

        count++;
        List<EngineJobCache> list = Lists.newArrayList();

        EngineJobCache engineJobCache1 = new EngineJobCache();
        engineJobCache1.setJobId("1");
        engineJobCache1.setStage(EJobCacheStage.DB.getStage());

        EngineJobCache engineJobCache2 = new EngineJobCache();
        engineJobCache2.setJobId("2");
        engineJobCache2.setStage(EJobCacheStage.SUBMITTED.getStage());

        list.add(engineJobCache1);
        list.add(engineJobCache2);

        return list;

    }


    @MockInvoke(targetClass = EngineJobCacheDao.class)
    Integer updateNodeAddressFailover(@Param("nodeAddress") String nodeAddress, @Param("jobIds") List<String> ids, @Param("stage") Integer stage){
        return 1;

    }

    @MockInvoke(targetClass = NodeRecoverService.class)
    public void masterTriggerNode() {
    }

    @MockInvoke(targetClass = JobGraphBuilderTrigger.class)
    public void dealMaster(boolean isMaster) {
    }


    @MockInvoke(targetClass = JobGraphBuilder.class)
    public void buildTaskJobGraph(String triggerDay) {
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<SimpleScheduleJobPO> listSimpleJobByStatusAddress(Long startId, List<Integer> statuses, String nodeAddress) {
        if (startId != 0L) {
            return Lists.newArrayList();
        }

        List<SimpleScheduleJobPO> simpleScheduleJobPOS = Lists.newArrayList();
        SimpleScheduleJobPO simpleScheduleJobPO1 = new SimpleScheduleJobPO();
        simpleScheduleJobPO1.setJobId("1");
        simpleScheduleJobPO1.setId(1L);
        simpleScheduleJobPO1.setPhaseStatus(JobPhaseStatus.JOIN_THE_TEAM.getCode());
        simpleScheduleJobPO1.setType(EScheduleType.NORMAL_SCHEDULE.getType());


        SimpleScheduleJobPO simpleScheduleJobPO2 = new SimpleScheduleJobPO();
        simpleScheduleJobPO2.setJobId("2");
        simpleScheduleJobPO2.setId(2L);
        simpleScheduleJobPO2.setPhaseStatus(JobPhaseStatus.JOIN_THE_TEAM.getCode());
        simpleScheduleJobPO2.setType(EScheduleType.FILL_DATA.getType());

        simpleScheduleJobPOS.add(simpleScheduleJobPO1);
        simpleScheduleJobPOS.add(simpleScheduleJobPO2);
        return simpleScheduleJobPOS;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateNodeAddress(String nodeAddress, List<String> ids){
        return 0;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer updateListPhaseStatus( List<String> ids, Integer update){
        return 0;
    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    void updateNodeAddressByIds(String nodeAddress, List<String> value){
    }

    @MockInvoke(targetClass = JobPartitioner.class)
    public Map<String, Integer> computeBatchJobSize(Integer type, int jobSize) {
        Map<String, Integer> map = new HashMap<>();
        map.put("127.0.0.1:8091",1);
        map.put("127.0.0.1:8092",1);
        return map;
    }

    @MockInvoke(targetClass = JobPartitioner.class)
    public Map<String, Integer> computeJobCacheSize(String jobResource, int jobSize) {
        Map<String, Integer> map = new HashMap<>();
        map.put("127.0.0.1:8091",1);
        map.put("127.0.0.1:8092",1);
        return map;
    }
}
