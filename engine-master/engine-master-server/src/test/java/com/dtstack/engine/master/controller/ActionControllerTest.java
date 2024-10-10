package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.OmniConstructor;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IYarn;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationStatus;
import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDescriptionDTO;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.AppTypeVO;
import com.dtstack.engine.api.vo.JobLogVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@MockWith(ActionControllerTest.ActionControllerMock.class)
public class ActionControllerTest {

    private static final ActionController controller = new ActionController();

    static class ActionControllerMock {
        @MockInvoke(targetClass = ActionService.class)
        public List<ActionJobStatusVO> listJobStatusByJobIds(List<String> jobIds) throws Exception {
            return Lists.newArrayList(OmniConstructor.newArray(ActionJobStatusVO.class,5));
        }

        @MockInvoke(targetClass = ActionService.class)
        public Boolean start(ParamActionExt paramActionExt){
            return true;
        }

        @MockInvoke(targetClass = ActionService.class)
        public Boolean startJob(ScheduleTaskShade batchTask, String jobId, String flowJobId) {
            return true;
        }

        @MockInvoke(targetClass = ActionService.class)
        public ParamActionExt paramActionExt(ScheduleTaskShade batchTask, String jobId, String flowJobId) throws Exception {
            return OmniConstructor.newInstance(ParamActionExt.class);
        }

        @MockInvoke(targetClass = ActionService.class)
        public Boolean stop(List<String> jobIds,Long operateId) {
            return true;
        }

        @MockInvoke(targetClass = ActionService.class)
        public Boolean stop(List<String> jobIds, Integer isForce,Long operateId) {
            return true;
        }

        @MockInvoke(targetClass = ActionService.class)
        public Integer status( String jobId) throws Exception {
            return 4;
        }

        @MockInvoke(targetClass = ActionService.class)
        public Long startTime( String jobId ) throws Exception {
            return System.currentTimeMillis();
        }

        @MockInvoke(targetClass = ActionService.class)
        public ActionLogVO log(String jobId, Integer computeType) throws Exception {
            return OmniConstructor.newInstance(ActionLogVO.class);
        }

        @MockInvoke(targetClass = ActionService.class)
        public JobLogVO logUnite(String jobId, Integer pageInfo)  throws Exception {
            return OmniConstructor.newInstance(JobLogVO.class);
        }

        @MockInvoke(targetClass = ActionService.class)
        public String logFromEs(String jobId) {
            return "";
        }

        @MockInvoke(targetClass = ActionService.class)
        public List<ActionRetryLogVO> retryLog(String jobId) throws Exception {
            return Lists.newArrayList(OmniConstructor.newArray(ActionRetryLogVO.class,3));
        }

        @MockInvoke(targetClass = ActionService.class)
        public ActionRetryLogVO retryLogDetail( String jobId, Integer retryNum) throws Exception {
            return OmniConstructor.newInstance(ActionRetryLogVO.class);
        }

        @MockInvoke(targetClass = ActionService.class)
        public List<ActionJobEntityVO> entitys(List<String> jobIds) throws Exception {
            return Lists.newArrayList(OmniConstructor.newArray(ActionJobEntityVO.class,3));
        }

        @MockInvoke(targetClass = ActionService.class)
        public List<String> containerInfos(ParamAction paramAction) throws Exception {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ActionService.class)
        public String resetTaskStatus( String jobId) {
            return "4";
        }

        @MockInvoke(targetClass = ActionService.class)
        public List<ActionJobStatusVO> listJobStatus( Long time,Integer appType) {
            return Lists.newArrayList(OmniConstructor.newArray(ActionJobStatusVO.class,3));
        }

        @MockInvoke(targetClass = ActionService.class)
        public List<ScheduleJob> listJobStatusScheduleJob(Long time, Integer appType) {
            return Lists.newArrayList(OmniConstructor.newArray(ScheduleJob.class,3));
        }

        @MockInvoke(targetClass = ActionService.class)
        public String generateUniqueSign() {
            return "sdsd";
        }
        @MockInvoke(targetClass = ActionService.class)
        public List<AppTypeVO> getAllAppType() {
            AppType[] appTypes = AppType.values();
            List<AppTypeVO> appTypeVOS = Lists.newArrayList();

            for (AppType appType : appTypes) {
                AppTypeVO appTypeVO = new AppTypeVO();
                appTypeVO.setCode(appType.getType());
                appTypeVO.setMsg(appType.getName());

                appTypeVOS.add(appTypeVO);
            }

            return appTypeVOS;
        }

        @MockInvoke(targetClass = ActionService.class)
        public ScheduleJob buildScheduleJob(ScheduleTaskShade batchTask, String jobId, String flowJobId) throws IOException, ParseException {
            return new ScheduleJob();
        }

        @MockInvoke(targetClass = ScheduleJobService.class)
        public void addOrUpdateScheduleJob(ScheduleJob scheduleJob) {}

        @MockInvoke(targetClass = ActionService.class)
        public Boolean refreshStatus(ParamActionExt paramActionExt) {
            return true;
        }

        @MockInvoke(targetClass = EnvironmentContext.class)
        public Boolean checkRefreshStatusName() {
            return false;
        }

        @MockInvoke(targetClass = TenantService.class)
        public Long getClusterIdByDtUicTenantId(Long dtuicTenantId) {
            return 1L;
        }

        @MockInvoke(targetClass = ClusterService.class)
        public JSONObject getYarnInfo(Long clusterId, Long dtUicTenantId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeName","YARN");
            jsonObject.put("yarnConf",JSONObject.parseObject("{\"yarn.resourcemanager.zk-address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"kudu1:8033\",\"yarn.resourcemanager.webapp.address.rm2\":\"kudu2:8088\",\"yarn.log.server.url\":\"http://kudu3:19888/jobhistory/logs/\",\"yarn.resourcemanager.admin.address.rm2\":\"kudu2:8033\",\"yarn.resourcemanager.webapp.address.rm1\":\"kudu1:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\",\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-election\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"kudu1:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"kudu2:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"kudu1:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"8000\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"kudu2:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"kudu1:8031\",\"yarn.nodemanager.resource.cpu-vcores\":\"10\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"kudu2:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"false\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.resourcemanager.hostname.rm2\":\"kudu2\",\"yarn.nodemanager.webapp.address\":\"kudu1:8042\",\"yarn.resourcemanager.hostname.rm1\":\"kudu1\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\"}"));
            return jsonObject;
        }

        @MockInvoke(targetClass = RdosWrapper.class)
        public Integer getDataSourceCodeByDiceName(String diceName, EComponentType eComponentType) {
            return 3;
        }

        @MockInvoke(targetClass = ClientCache.class)
        public static IYarn getYarn(Integer dataSourceType) {
            return new IYarn() {
                @Override
                public List<YarnApplicationInfoDTO> listApplication(ISourceDTO source, YarnApplicationStatus status, String taskName, String applicationId) {
                    return Lists.newArrayList(OmniConstructor.newArray(YarnApplicationInfoDTO.class,3));
                }

                @Override
                public YarnResourceDTO getYarnResource(ISourceDTO source) {
                    return null;
                }

                @Override
                public YarnResourceDescriptionDTO getYarnResourceDescription(ISourceDTO source) {
                    return null;
                }

                @Override
                public List<YarnApplicationInfoDTO> listApplicationByTag(ISourceDTO source, String tag) {
                    return null;
                }
            };
        }

        @MockInvoke(targetClass = ClusterService.class)
        public JSONObject getYarnInfo(Long clusterId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("selfConf","{\"selfConf\":{\"a\":\"b\"}}");
            return jsonObject;
        }

        }

    @Test
    public void listJobStatusByJobIds() throws Exception {
        controller.listJobStatusByJobIds(Lists.newArrayList("1"));
    }

    @Test
    public void start() {
        controller.start(OmniConstructor.newInstance(ParamActionExt.class));
    }

    @Test
    public void startJob() {
        controller.startJob(OmniConstructor.newInstance(ParamTaskAction.class));
    }

    @Test
    public void paramActionExt() throws Exception {
        controller.paramActionExt(OmniConstructor.newInstance(ParamTaskAction.class));
    }

    @Test
    public void stop() throws Exception {
        controller.stop(Lists.newArrayList("1"),1L);
        controller.stop(Lists.newArrayList("1"),1,1L);
    }


    @Test
    public void status() throws Exception {
        controller.status("1");
    }

    @Test
    public void startTime() throws Exception {
        controller.startTime("1");
    }

    @Test
    public void log() throws Exception {
        controller.log("1",1);
    }

    @Test
    public void logUnite() throws Exception {
        controller.logUnite("1",1);
    }

    @Test
    public void logFromEs() throws Exception {
        controller.logFromEs("1");
    }

    @Test
    public void retryLog() throws Exception {
        controller.retryLog("1",1L);
    }

    @Test
    public void retryLogDetail() throws Exception {
        controller.retryLogDetail("1",1);
    }

    @Test
    public void entitys() throws Exception {
        controller.entitys(Lists.newArrayList("1"));
    }

    @Test
    public void containerInfos() throws Exception {
        controller.containerInfos(OmniConstructor.newInstance(ParamAction.class));
    }

    @Test
    public void resetTaskStatus() {
        controller.resetTaskStatus("1");
    }

    @Test
    public void listJobStatus() {
        controller.listJobStatus(System.currentTimeMillis(),1);
    }

    @Test
    public void listJobStatusScheduleJob() {
        controller.listJobStatusScheduleJob(System.currentTimeMillis(),1);
    }

    @Test
    public void generateUniqueSign() {
        controller.generateUniqueSign();
    }

    @Test
    public void appType() {
        controller.appType();
    }

    @Test
    public void addOrUpdateJob() {
        ParamTaskAction paramTaskAction = OmniConstructor.newInstance(ParamTaskAction.class);
        paramTaskAction.setJobId("sds");
        controller.addOrUpdateJob(paramTaskAction);
    }

    @Test
    public void refreshStatus() {
        ParamActionExt paramActionExt = OmniConstructor.newInstance(ParamActionExt.class);
        paramActionExt.setTaskId("1");
        paramActionExt.setApplicationId("1");
        controller.refreshStatus(paramActionExt);
    }

    @Test
    public void listApplication() {
        controller.listApplication(1L,Lists.newArrayList("1"));
    }

    @Test
    public void convertToRdosTaskStatus() {
        PrivateAccessor.invoke(controller,"getApplicationInfo",1L,"1","1");
    }


}