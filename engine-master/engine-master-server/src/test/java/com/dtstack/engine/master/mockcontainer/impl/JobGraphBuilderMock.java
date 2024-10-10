package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.JobGraphTriggerService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.listener.JobGraphEvent;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.feign.Param;
import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 11:12 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobGraphBuilderMock extends BaseMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getJobGraphBuilderSwitch() {
        return false;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public boolean getJobGraphWhiteList() {
        return true;
    }


    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getBuildJobErrorRetry() {
        return 3;
    }


    @MockInvoke(targetClass = JobGraphTriggerService.class)
    public boolean checkHasBuildJobGraph(Timestamp todayTime){
        return false;
    }

     @MockInvoke(targetClass = JobGraphTriggerService.class)
     public int addJobTrigger(Timestamp timestamp,Long minJobId){
        return 1;
     }

     @MockInvoke(targetClass = JobGraphEvent.class)
    public void successEvent(final String triggerDay) {

    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer countByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType) {
        return 12;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJob> listByCyctimeAndJobName(Long startId, String preCycTime, String preJobName, Integer scheduleType, Integer batchJobSize) {
        List<ScheduleJob> scheduleJobs = Lists.newArrayList();


        for (int i = 0; i < 12; i++) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobKey(UUID.randomUUID().toString());
            scheduleJobs.add(scheduleJob);
        }

        return scheduleJobs;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public void deleteJobsByJobKey( List<String> jobKeyList) {
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public void insertJobList(Collection<ScheduleBatchJob> batchJobCollection, Integer scheduleType) {

    }

    @MockInvoke(targetClass = ScheduleJobOperatorRecordDao.class)
    public Long insertBatch(Collection<ScheduleJobOperatorRecord> records){
        return 0L;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer updateFlowJob(String placeholder, String flowJob) {
        return 1;
    }

    @MockInvoke(targetClass = JobGraphBuilder.class)
    public boolean saveJobGraph(String triggerDay) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public Integer countTaskByStatus(Integer submitStatus, Integer projectSubmitStatus, Collection<Long> projectIds, Integer appType) {
        return getTaskJson().size();
    }


    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getFlowWorkSubTasks( Long taskId,  Integer appType,List<Integer> taskTypes,Long ownerId) {
        List<String> taskJson = getTaskJson();
        List<ScheduleTaskShade> taskShades = Lists.newArrayList();
        for (String json : taskJson) {
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(json, ScheduleTaskShade.class);

            Long flowId = scheduleTaskShade.getFlowId();

            if (taskId.equals(flowId) && appType.equals(scheduleTaskShade.getAppType())) {
                taskShades.add(scheduleTaskShade);
            }
        }
        return taskShades;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> findChildTaskRuleByTaskId(Long taskId, Integer appType) {
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> listTaskByStatus(Long startId, Integer submitStatus, Integer projectSubmitStatus, Integer batchTaskSize, Collection<Long> projectIds, Integer appType) {
        List<String> taskJson = getTaskJson();
        List<ScheduleTaskShade> taskShades = Lists.newArrayList();
        for (String json : taskJson) {
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(json, ScheduleTaskShade.class);
            taskShades.add(scheduleTaskShade);
        }
        return taskShades;
    }

    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    public List<ScheduleEngineProject> listWhiteListProject() {
        List<ScheduleEngineProject> scheduleEngineProjects = Lists.newArrayList();

        for (int i = 0; i < 10; i++) {
            ScheduleEngineProject scheduleEngineProject = new ScheduleEngineProject();
            scheduleEngineProject.setProjectId(100065L);
            scheduleEngineProject.setAppType(1);
            scheduleEngineProjects.add(scheduleEngineProject);
        }
        return scheduleEngineProjects;
    }

    @MockInvoke(targetClass = ActionService.class)
    public String generateUniqueSign() {
        return UUID.randomUUID().toString();
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public List<ScheduleTaskTaskShade> getAllParentTask(Long taskId, Integer appType) {
        String side = "  {\n" +
                "    \"id\": 209951,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"parent_app_type\": 1,\n" +
                "    \"task_id\": 49359,\n" +
                "    \"parent_task_id\": 49357,\n" +
                "    \"gmt_create\": \"2022-06-23 21:40:08\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:40:08\",\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_key\": \"49359-1\",\n" +
                "    \"parent_task_key\": \"49357-1\"\n" +
                "  }";
        List<ScheduleTaskTaskShade> shades = Lists.newArrayList();
        if (49359L == taskId && appType.equals(1)) {
            ScheduleTaskTaskShade scheduleTaskTaskShade = JSON.parseObject(side, ScheduleTaskTaskShade.class);
            shades.add(scheduleTaskTaskShade);
        }

        return shades;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
        List<String> taskJson = getTaskJson();
        for (String json : taskJson) {
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(json, ScheduleTaskShade.class);
            if (scheduleTaskShade.getTaskId().equals(taskId)
                    && scheduleTaskShade.getAppType().equals(appType)) {
                return scheduleTaskShade;
            }

        }
        return null;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        List<String> taskJson = getTaskJson();
        List<ScheduleTaskShade> shades = Lists.newArrayList();
        for (String json : taskJson) {
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(json, ScheduleTaskShade.class);
            if (taskIds.contains(scheduleTaskShade.getTaskId())
                    && scheduleTaskShade.getAppType().equals(appType)) {
                shades.add(scheduleTaskShade);
            }
        }

        return shades;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public ScheduleJob getJobByJobKeyAndType(String jobKey, int type) {
        return null;
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    public ApiResponse<List<UICUserVO>> getByUserIds(@Param("userIds") List<Long> var1) {
        ApiResponse<List<UICUserVO>> objectApiResponse = new ApiResponse<>();

        objectApiResponse.setSuccess(Boolean.TRUE);
        ArrayList<UICUserVO> objects = Lists.newArrayList();

        UICUserVO vo = new UICUserVO();
        vo.setUserId(1L);
        vo.setEmail("1L");
        vo.setPhone("1L");
        vo.setUserName("1L");
        objects.add(vo);
        objectApiResponse.setData(objects);
        return objectApiResponse;
    }

    private List<String> getTaskJson() {
        String shellDaySon = "  {\n" +
                "    \"id\": 64627,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day_son\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49359,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":0, \\\"min\\\":0,\\\"hour\\\":0,\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\"}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 21:40:51\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:40:11\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30739,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day_son\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day_son\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day_son\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49359}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String fatherJson = "  {\n" +
                "    \"id\": 64625,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day_father\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49357,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":0, \\\"min\\\":0,\\\"hour\\\":0,\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\"}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 21:40:40\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:39:46\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30737,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day_father\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day_father\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day_father\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49357}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String minJson = "  {\n" +
                "    \"id\": 64147,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_min\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_min\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:49:43\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 48375,\n" +
                "    \"schedule_conf\": \"{\\\"beginMin\\\":0,\\\"endMin\\\":59,\\\"beginHour\\\":0,\\\"endHour\\\":23,\\\"gapMin\\\":5,\\\"periodType\\\":\\\"0\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"scheduleStatus\\\":false,\\\"isFailRetry\\\":true,\\\"selfReliance\\\":0,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"isLastInstance\\\":true}\",\n" +
                "    \"period_type\": 0,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-13 13:49:43\",\n" +
                "    \"gmt_modified\": \"2022-06-13 13:49:43\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30035,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_min\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_min\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:49:43\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_min\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_min\\\",\\\"tenantId\\\":1,\\\"taskId\\\":48375}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String hourJson = "  {\n" +
                "    \"id\": 64151,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_hour\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_hour\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:50:20\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 48379,\n" +
                "    \"schedule_conf\": \"{\\\"beginHour\\\":0,\\\"endHour\\\":23,\\\"beginMin\\\":0,\\\"endMin\\\":59,\\\"gapHour\\\":5,\\\"periodType\\\":\\\"1\\\",\\\"scheduleStatus\\\":false,\\\"isFailRetry\\\":true,\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"selfReliance\\\":0,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"isLastInstance\\\":true}\",\n" +
                "    \"period_type\": 1,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-13 13:50:21\",\n" +
                "    \"gmt_modified\": \"2022-06-13 13:50:16\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30037,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_hour\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_hour\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:50:20\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_hour\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_hour\\\",\\\"tenantId\\\":1,\\\"taskId\\\":48379}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String dayJson = "  {\n" +
                "    \"id\": 64145,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 48373,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":0, \\\"min\\\":0,\\\"hour\\\":0,\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\"}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-13 13:48:53\",\n" +
                "    \"gmt_modified\": \"2022-06-13 13:48:44\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30033,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day\\\",\\\"tenantId\\\":1,\\\"taskId\\\":48373}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String weekJSon = "  {\n" +
                "    \"id\": 64153,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_week\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_week\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:54:30\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 48383,\n" +
                "    \"schedule_conf\": \"{\\\"weekDay\\\":\\\"3,1,2,4,5,6,7\\\",\\\"min\\\":\\\"0\\\",\\\"hour\\\":\\\"3\\\",\\\"periodType\\\":\\\"3\\\",\\\"isFailRetry\\\":true,\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"selfReliance\\\":0,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"isLastInstance\\\":true,\\\"yarnResourceId\\\":663}\",\n" +
                "    \"period_type\": 3,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-13 13:54:31\",\n" +
                "    \"gmt_modified\": \"2022-06-13 13:54:46\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30049,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_week\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_week\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:54:30\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_week\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_week\\\",\\\"tenantId\\\":1,\\\"taskId\\\":48383}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String monJson = "  {\n" +
                "    \"id\": 64161,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_month\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_month\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 14:03:49\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 48387,\n" +
                "    \"schedule_conf\": \"{\\\"day\\\":\\\"1,2,3,4,5,6,7,9,8,10,11,12,13,14,15,16,17,18,19,20,21,22,23,31,30,29,27,28,25,26,24\\\",\\\"hour\\\":\\\"0\\\",\\\"min\\\":\\\"23\\\",\\\"periodType\\\":\\\"4\\\",\\\"isFailRetry\\\":true,\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"selfReliance\\\":0,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"isLastInstance\\\":true,\\\"yarnResourceId\\\":663}\",\n" +
                "    \"period_type\": 4,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-13 14:03:50\",\n" +
                "    \"gmt_modified\": \"2022-06-13 15:00:55\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30051,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_month\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_month\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 14:03:49\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_month\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_month\\\",\\\"tenantId\\\":1,\\\"taskId\\\":48387}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String cornJson = "  {\n" +
                "    \"id\": 64163,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_corn\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_corn\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 15:01:20\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 48395,\n" +
                "    \"schedule_conf\": \"{\\\"cron\\\":\\\"0 0 0 3 * ? \\\",\\\"periodType\\\":\\\"5\\\",\\\"isFailRetry\\\":true,\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"selfReliance\\\":0,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"isLastInstance\\\":true,\\\"yarnResourceId\\\":663}\",\n" +
                "    \"period_type\": 5,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-13 15:01:20\",\n" +
                "    \"gmt_modified\": \"2022-06-13 15:02:15\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30053,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_corn\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_corn\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 15:01:20\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_corn\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_corn\\\",\\\"tenantId\\\":1,\\\"taskId\\\":48395}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String selfSuccessJson = "  {\n" +
                "    \"id\": 64617,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day_self_success\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49345,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":1,\\\"min\\\":\\\"0\\\",\\\"hour\\\":\\\"0\\\",\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"yarnResourceId\\\":663,\\\"isLastInstance\\\":true}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 21:36:54\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:37:07\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30729,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day_self_success\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day_self_success\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day_self_success\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49345}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String selfFinishJson = "  {\n" +
                "    \"id\": 64619,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day_self_finish\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49347,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":3,\\\"min\\\":\\\"0\\\",\\\"hour\\\":\\\"0\\\",\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"yarnResourceId\\\":663,\\\"isLastInstance\\\":true}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 21:38:27\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:37:41\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30731,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day_self_finish\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day_self_finish\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day_self_finish\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49347}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String downstreamFinishJson = "  {\n" +
                "    \"id\": 64621,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day_downstream_finish\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49349,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":4,\\\"min\\\":\\\"0\\\",\\\"hour\\\":\\\"0\\\",\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"yarnResourceId\\\":663,\\\"isLastInstance\\\":true}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 21:39:33\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:38:46\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30733,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day_downstream_finish\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day_downstream_finish\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day_downstream_finish\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49349}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String downstreamSuccessJson = "  {\n" +
                "    \"id\": 64623,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"shell_day_downstream_success\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name shell_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-13 13:48:52\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49355,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":2,\\\"min\\\":\\\"0\\\",\\\"hour\\\":\\\"0\\\",\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\",\\\"retryIntervalTime\\\":2,\\\"yarnResourceId\\\":663,\\\"isLastInstance\\\":true}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 21:40:04\",\n" +
                "    \"gmt_modified\": \"2022-06-23 21:39:15\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30735,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"shell_day_downstream_success\\\"}\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name shell_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-13 13:48:52\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name shell_day_downstream_success\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"shell_day_downstream_success\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49355}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String work = "  {\n" +
                "    \"id\": 64641,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"work\",\n" +
                "    \"task_type\": 10,\n" +
                "    \"engine_type\": 1,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"[{\\\"vertex\\\":true,\\\"edge\\\":false,\\\"data\\\":{\\\"id\\\":49407,\\\"name\\\":\\\"s_1_day\\\",\\\"type\\\":\\\"file\\\",\\\"taskType\\\":7,\\\"parentId\\\":142069,\\\"catalogueType\\\":\\\"TaskDevelop\\\",\\\"nodePid\\\":142069,\\\"submitStatus\\\":0,\\\"version\\\":0,\\\"readWriteLockVO\\\":{\\\"result\\\":0,\\\"gmtModified\\\":1655994250201,\\\"getLock\\\":true,\\\"isDeleted\\\":0,\\\"lastKeepLockUserName\\\":\\\"admin@dtstack.com\\\",\\\"modifyUserId\\\":1,\\\"relationId\\\":49407,\\\"id\\\":54007,\\\"type\\\":\\\"BATCH_TASK\\\",\\\"lockName\\\":\\\"49407_100065_BATCH_TASK\\\",\\\"version\\\":1}},\\\"x\\\":600,\\\"y\\\":300,\\\"value\\\":null,\\\"id\\\":49407},{\\\"vertex\\\":true,\\\"edge\\\":false,\\\"data\\\":{\\\"id\\\":49409,\\\"name\\\":\\\"s_2_day\\\",\\\"type\\\":\\\"file\\\",\\\"taskType\\\":7,\\\"parentId\\\":142069,\\\"catalogueType\\\":\\\"TaskDevelop\\\",\\\"nodePid\\\":142069,\\\"submitStatus\\\":0,\\\"version\\\":0,\\\"readWriteLockVO\\\":{\\\"result\\\":0,\\\"gmtModified\\\":1655994261657,\\\"getLock\\\":true,\\\"isDeleted\\\":0,\\\"lastKeepLockUserName\\\":\\\"admin@dtstack.com\\\",\\\"modifyUserId\\\":1,\\\"relationId\\\":49409,\\\"id\\\":54009,\\\"type\\\":\\\"BATCH_TASK\\\",\\\"lockName\\\":\\\"49409_100065_BATCH_TASK\\\",\\\"version\\\":1}},\\\"x\\\":600,\\\"y\\\":410,\\\"value\\\":null,\\\"id\\\":49409},{\\\"vertex\\\":false,\\\"edge\\\":true,\\\"x\\\":0,\\\"y\\\":0,\\\"value\\\":null,\\\"id\\\":\\\"49410\\\",\\\"source\\\":{\\\"vertex\\\":true,\\\"edge\\\":false,\\\"data\\\":{\\\"id\\\":49407,\\\"name\\\":\\\"s_1_day\\\",\\\"type\\\":\\\"file\\\",\\\"taskType\\\":7,\\\"parentId\\\":142069,\\\"catalogueType\\\":\\\"TaskDevelop\\\",\\\"nodePid\\\":142069,\\\"submitStatus\\\":0,\\\"version\\\":0,\\\"readWriteLockVO\\\":{\\\"result\\\":0,\\\"gmtModified\\\":1655994250201,\\\"getLock\\\":true,\\\"isDeleted\\\":0,\\\"lastKeepLockUserName\\\":\\\"admin@dtstack.com\\\",\\\"modifyUserId\\\":1,\\\"relationId\\\":49407,\\\"id\\\":54007,\\\"type\\\":\\\"BATCH_TASK\\\",\\\"lockName\\\":\\\"49407_100065_BATCH_TASK\\\",\\\"version\\\":1}},\\\"x\\\":600,\\\"y\\\":300,\\\"value\\\":null,\\\"id\\\":49407},\\\"target\\\":{\\\"vertex\\\":true,\\\"edge\\\":false,\\\"data\\\":{\\\"id\\\":49409,\\\"name\\\":\\\"s_2_day\\\",\\\"type\\\":\\\"file\\\",\\\"taskType\\\":7,\\\"parentId\\\":142069,\\\"catalogueType\\\":\\\"TaskDevelop\\\",\\\"nodePid\\\":142069,\\\"submitStatus\\\":0,\\\"version\\\":0,\\\"readWriteLockVO\\\":{\\\"result\\\":0,\\\"gmtModified\\\":1655994261657,\\\"getLock\\\":true,\\\"isDeleted\\\":0,\\\"lastKeepLockUserName\\\":\\\"admin@dtstack.com\\\",\\\"modifyUserId\\\":1,\\\"relationId\\\":49409,\\\"id\\\":54009,\\\"type\\\":\\\"BATCH_TASK\\\",\\\"lockName\\\":\\\"49409_100065_BATCH_TASK\\\",\\\"version\\\":1}},\\\"x\\\":600,\\\"y\\\":410,\\\"value\\\":null,\\\"id\\\":49409}}]\",\n" +
                "    \"task_params\": \"\",\n" +
                "    \"task_id\": 49405,\n" +
                "    \"schedule_conf\": \"{\\\"selfReliance\\\":0, \\\"min\\\":0,\\\"hour\\\":0,\\\"periodType\\\":\\\"2\\\",\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"endDate\\\":\\\"2121-01-01\\\",\\\"isFailRetry\\\":true,\\\"maxRetryNum\\\":\\\"3\\\"}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 22:23:59\",\n" +
                "    \"gmt_modified\": \"2022-06-23 22:23:52\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30757,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"\",\n" +
                "    \"flow_id\": 0,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"[{\\\\\\\"vertex\\\\\\\":true,\\\\\\\"edge\\\\\\\":false,\\\\\\\"data\\\\\\\":{\\\\\\\"id\\\\\\\":49407,\\\\\\\"name\\\\\\\":\\\\\\\"s_1_day\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"file\\\\\\\",\\\\\\\"taskType\\\\\\\":7,\\\\\\\"parentId\\\\\\\":142069,\\\\\\\"catalogueType\\\\\\\":\\\\\\\"TaskDevelop\\\\\\\",\\\\\\\"nodePid\\\\\\\":142069,\\\\\\\"submitStatus\\\\\\\":0,\\\\\\\"version\\\\\\\":0,\\\\\\\"readWriteLockVO\\\\\\\":{\\\\\\\"result\\\\\\\":0,\\\\\\\"gmtModified\\\\\\\":1655994250201,\\\\\\\"getLock\\\\\\\":true,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"lastKeepLockUserName\\\\\\\":\\\\\\\"admin@dtstack.com\\\\\\\",\\\\\\\"modifyUserId\\\\\\\":1,\\\\\\\"relationId\\\\\\\":49407,\\\\\\\"id\\\\\\\":54007,\\\\\\\"type\\\\\\\":\\\\\\\"BATCH_TASK\\\\\\\",\\\\\\\"lockName\\\\\\\":\\\\\\\"49407_100065_BATCH_TASK\\\\\\\",\\\\\\\"version\\\\\\\":1}},\\\\\\\"x\\\\\\\":600,\\\\\\\"y\\\\\\\":300,\\\\\\\"value\\\\\\\":null,\\\\\\\"id\\\\\\\":49407},{\\\\\\\"vertex\\\\\\\":true,\\\\\\\"edge\\\\\\\":false,\\\\\\\"data\\\\\\\":{\\\\\\\"id\\\\\\\":49409,\\\\\\\"name\\\\\\\":\\\\\\\"s_2_day\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"file\\\\\\\",\\\\\\\"taskType\\\\\\\":7,\\\\\\\"parentId\\\\\\\":142069,\\\\\\\"catalogueType\\\\\\\":\\\\\\\"TaskDevelop\\\\\\\",\\\\\\\"nodePid\\\\\\\":142069,\\\\\\\"submitStatus\\\\\\\":0,\\\\\\\"version\\\\\\\":0,\\\\\\\"readWriteLockVO\\\\\\\":{\\\\\\\"result\\\\\\\":0,\\\\\\\"gmtModified\\\\\\\":1655994261657,\\\\\\\"getLock\\\\\\\":true,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"lastKeepLockUserName\\\\\\\":\\\\\\\"admin@dtstack.com\\\\\\\",\\\\\\\"modifyUserId\\\\\\\":1,\\\\\\\"relationId\\\\\\\":49409,\\\\\\\"id\\\\\\\":54009,\\\\\\\"type\\\\\\\":\\\\\\\"BATCH_TASK\\\\\\\",\\\\\\\"lockName\\\\\\\":\\\\\\\"49409_100065_BATCH_TASK\\\\\\\",\\\\\\\"version\\\\\\\":1}},\\\\\\\"x\\\\\\\":600,\\\\\\\"y\\\\\\\":410,\\\\\\\"value\\\\\\\":null,\\\\\\\"id\\\\\\\":49409},{\\\\\\\"vertex\\\\\\\":false,\\\\\\\"edge\\\\\\\":true,\\\\\\\"x\\\\\\\":0,\\\\\\\"y\\\\\\\":0,\\\\\\\"value\\\\\\\":null,\\\\\\\"id\\\\\\\":\\\\\\\"49410\\\\\\\",\\\\\\\"source\\\\\\\":{\\\\\\\"vertex\\\\\\\":true,\\\\\\\"edge\\\\\\\":false,\\\\\\\"data\\\\\\\":{\\\\\\\"id\\\\\\\":49407,\\\\\\\"name\\\\\\\":\\\\\\\"s_1_day\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"file\\\\\\\",\\\\\\\"taskType\\\\\\\":7,\\\\\\\"parentId\\\\\\\":142069,\\\\\\\"catalogueType\\\\\\\":\\\\\\\"TaskDevelop\\\\\\\",\\\\\\\"nodePid\\\\\\\":142069,\\\\\\\"submitStatus\\\\\\\":0,\\\\\\\"version\\\\\\\":0,\\\\\\\"readWriteLockVO\\\\\\\":{\\\\\\\"result\\\\\\\":0,\\\\\\\"gmtModified\\\\\\\":1655994250201,\\\\\\\"getLock\\\\\\\":true,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"lastKeepLockUserName\\\\\\\":\\\\\\\"admin@dtstack.com\\\\\\\",\\\\\\\"modifyUserId\\\\\\\":1,\\\\\\\"relationId\\\\\\\":49407,\\\\\\\"id\\\\\\\":54007,\\\\\\\"type\\\\\\\":\\\\\\\"BATCH_TASK\\\\\\\",\\\\\\\"lockName\\\\\\\":\\\\\\\"49407_100065_BATCH_TASK\\\\\\\",\\\\\\\"version\\\\\\\":1}},\\\\\\\"x\\\\\\\":600,\\\\\\\"y\\\\\\\":300,\\\\\\\"value\\\\\\\":null,\\\\\\\"id\\\\\\\":49407},\\\\\\\"target\\\\\\\":{\\\\\\\"vertex\\\\\\\":true,\\\\\\\"edge\\\\\\\":false,\\\\\\\"data\\\\\\\":{\\\\\\\"id\\\\\\\":49409,\\\\\\\"name\\\\\\\":\\\\\\\"s_2_day\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"file\\\\\\\",\\\\\\\"taskType\\\\\\\":7,\\\\\\\"parentId\\\\\\\":142069,\\\\\\\"catalogueType\\\\\\\":\\\\\\\"TaskDevelop\\\\\\\",\\\\\\\"nodePid\\\\\\\":142069,\\\\\\\"submitStatus\\\\\\\":0,\\\\\\\"version\\\\\\\":0,\\\\\\\"readWriteLockVO\\\\\\\":{\\\\\\\"result\\\\\\\":0,\\\\\\\"gmtModified\\\\\\\":1655994261657,\\\\\\\"getLock\\\\\\\":true,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"lastKeepLockUserName\\\\\\\":\\\\\\\"admin@dtstack.com\\\\\\\",\\\\\\\"modifyUserId\\\\\\\":1,\\\\\\\"relationId\\\\\\\":49409,\\\\\\\"id\\\\\\\":54009,\\\\\\\"type\\\\\\\":\\\\\\\"BATCH_TASK\\\\\\\",\\\\\\\"lockName\\\\\\\":\\\\\\\"49409_100065_BATCH_TASK\\\\\\\",\\\\\\\"version\\\\\\\":1}},\\\\\\\"x\\\\\\\":600,\\\\\\\"y\\\\\\\":410,\\\\\\\"value\\\\\\\":null,\\\\\\\"id\\\\\\\":49409}}]\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"\\\",\\\"engineType\\\":\\\"spark\\\",\\\"taskParams\\\":\\\"\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":-1,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"work\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49405}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": null\n" +
                "  }";

        String workJson1 = "  {\n" +
                "    \"id\": 64639,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"s_2_day\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name s_2_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-23 22:24:21\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49409,\n" +
                "    \"schedule_conf\": \"{\\\"isFailRetry\\\":true,\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"min\\\":0,\\\"periodType\\\":\\\"2\\\",\\\"hour\\\":0,\\\"selfReliance\\\":0,\\\"endDate\\\":\\\"2121-01-01\\\",\\\"maxRetryNum\\\":\\\"3\\\"}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 22:24:22\",\n" +
                "    \"gmt_modified\": \"2022-06-23 22:23:52\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30759,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"s_2_day\\\"}\",\n" +
                "    \"flow_id\": 49405,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name s_2_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-23 22:24:21\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name s_2_day\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"s_2_day\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49409}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";

        String workJson2 = "  {\n" +
                "    \"id\": 64637,\n" +
                "    \"tenant_id\": 1,\n" +
                "    \"project_id\": 100065,\n" +
                "    \"dtuic_tenant_id\": 1,\n" +
                "    \"app_type\": 1,\n" +
                "    \"node_pid\": 142069,\n" +
                "    \"name\": \"s_1_day\",\n" +
                "    \"task_type\": 7,\n" +
                "    \"engine_type\": 6,\n" +
                "    \"compute_type\": 1,\n" +
                "    \"sql_text\": \"#name s_1_day\\n#type Shell\\n#author admin@dtstack.com\\n#create time 2022-06-23 22:24:10\\n#desc \\n\",\n" +
                "    \"task_params\": \"## 每个worker所占内存，比如512m\\n# dtscript.worker.memory=512m\\n\\n## 每个worker所占的cpu核的数量\\n# dtscript.worker.cores=1\\n\\n## worker数量\\n# dtscript.worker.num=1\\n\\n## 是否独占机器节点\\n# dtscript.worker.exclusive=false\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\njob.priority=10\\n\\n## 指定work运行节点，需要注意不要写ip应填写对应的hostname\\n# dtscript.worker.nodes=\\n\\n## 指定work运行机架\\n# dtscript.worker.racks=\\n\\n## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\nlogLevel=INFO\",\n" +
                "    \"task_id\": 49407,\n" +
                "    \"schedule_conf\": \"{\\\"isFailRetry\\\":true,\\\"beginDate\\\":\\\"2001-01-01\\\",\\\"min\\\":0,\\\"periodType\\\":\\\"2\\\",\\\"hour\\\":0,\\\"selfReliance\\\":0,\\\"endDate\\\":\\\"2121-01-01\\\",\\\"maxRetryNum\\\":\\\"3\\\"}\",\n" +
                "    \"period_type\": 2,\n" +
                "    \"schedule_status\": 1,\n" +
                "    \"project_schedule_status\": 0,\n" +
                "    \"submit_status\": 1,\n" +
                "    \"gmt_create\": \"2022-06-23 22:24:10\",\n" +
                "    \"gmt_modified\": \"2022-06-23 22:23:52\",\n" +
                "    \"modify_user_id\": 1,\n" +
                "    \"create_user_id\": 1,\n" +
                "    \"owner_user_id\": 1,\n" +
                "    \"version_id\": 30761,\n" +
                "    \"is_deleted\": 0,\n" +
                "    \"task_desc\": \"\",\n" +
                "    \"main_class\": \"\",\n" +
                "    \"exe_args\": \"{\\\"operateModel\\\":1,\\\"--python-version\\\":0,\\\"--app-type\\\":\\\"shell\\\",\\\"--app-name\\\":\\\"s_1_day\\\"}\",\n" +
                "    \"flow_id\": 49405,\n" +
                "    \"is_publish_to_produce\": 0,\n" +
                "    \"extra_info\": \"{\\\"info\\\":{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name s_1_day\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2022-06-23 22:24:10\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name s_1_day\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"job.priority=10\\\\nlogLevel=INFO\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"s_1_day\\\",\\\"tenantId\\\":1,\\\"taskId\\\":49407}}\",\n" +
                "    \"is_expire\": 0,\n" +
                "    \"task_rule\": 0,\n" +
                "    \"component_version\": null,\n" +
                "    \"business_type\": null,\n" +
                "    \"resource_id\": 663\n" +
                "  }";
        List<String> taskJson = Lists.newArrayList();

        taskJson.add(shellDaySon);
        taskJson.add(fatherJson);
        taskJson.add(downstreamSuccessJson);
        taskJson.add(downstreamFinishJson);
        taskJson.add(selfFinishJson);
        taskJson.add(selfSuccessJson);
        taskJson.add(cornJson);
        taskJson.add(monJson);
        taskJson.add(weekJSon);
        taskJson.add(dayJson);
        taskJson.add(hourJson);
        taskJson.add(minJson);
        taskJson.add(workJson1);
        taskJson.add(workJson2);
        taskJson.add(work);
        return taskJson;
    }


}
