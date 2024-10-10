package com.dtstack.engine.master.strategy;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.api.enums.BaselineTypeEnum;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.dto.BaselineBlockDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.master.mapstruct.AlterTempStruct;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertRule;
import com.dtstack.engine.po.BaselineBlockJobRecord;
import com.dtstack.engine.po.BaselineJob;
import com.dtstack.engine.po.BaselineTaskBatch;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.QueryAuthProjectParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/6/6 10:23 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractTemplateReplaceStrategy extends AbstractStrategy {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategy.class);

    public static final String RESOURCE_OVER_LIMIT = "resource_over_limit";

    public final static String JOB = "job";
    public final static String JOB_EXEC_TIME = "jobExecTime";
    public final static String JOB_STATUS = "statusStr";
    public final static String JOB_CYC_TIME = "jobCycTime";
    public final static String JOB_SCHEDULE_TYPE = "jobScheduleType";
    public final static String ERROR_MSG = "errorMsg";
    public final static String JOB_RESOURCE = "jobResource";

    public final static String BASELINE_JOB = "baselineJob";
    public final static String BASELINE_STATUS_STR = "baselineStatusStr";
    public final static String EXEC_START_TIME = "execStartTime";
    public final static String EXEC_END_TIME = "execEndTime";
    public final static String EXEC_TIME = "execTime";
    public final static String BASELINE_TASK = "baselineTask";
    public final static String SIGN = "sign";
    public final static String RELATED_TASK_NAME = "relatedTaskName";


    public final static String TASK = "task";

    @Autowired
    private AlterTempStruct alterTempStruct;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Override
    protected String getContent(AlertAlarm alertAlarm, AlertRule alertRule, AlterEventContext context, Map<String, Object> replaceParamMap) {
        String template = alertRule.getTemplate();

        Long projectId;
        Integer appType;
        Long tenantId;
        Long ownerUserId;
        if (AlarmTypeEnum.isTask(alertAlarm.getAlarmType())) {
            // 查询任务信息，任务的项目信息和任务的租户信息
            Long taskId = context.getTaskId();
            appType = context.getAppType();
            String jobId = context.getJobId();
            ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(taskId, appType);

            if (taskShade == null) {
                throw new RdosDefineException(String.format("task:%s appType:%s does not exist", taskId, appType));
            }

            projectId = taskShade.getProjectId();
            tenantId = taskShade.getDtuicTenantId();
            ownerUserId = taskShade.getOwnerUserId();

            ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());
            String execTime = DateUtil.getExecTime(scheduleJob.getExecStartTime(), scheduleJob.getExecEndTime(), scheduleJob.getExecTime(), scheduleJob.getStatus());
            if (StringUtils.isNotBlank(execTime)) {
                replaceParamMap.put(JOB_EXEC_TIME, execTime);
            } else {
                replaceParamMap.put(JOB_EXEC_TIME, " - ");
            }
            int showStatusWithoutStop = RdosTaskStatus.getShowStatusWithoutStop(scheduleJob.getStatus());
            replaceParamMap.put(JOB_STATUS, getStatus(showStatusWithoutStop));
            String cycTime = formatCycTime(scheduleJob);
            replaceParamMap.put(JOB_CYC_TIME, cycTime);

            replaceParamMap.put(JOB, scheduleJob);
            replaceParamMap.put(TASK, taskShade);
            replaceParamMap.put(JOB_SCHEDULE_TYPE, EScheduleType.getTypeName(scheduleJob.getType()));
            //元数据告警需要补充失败信息
            if (RdosTaskStatus.SUBMITFAILD.getStatus().equals(scheduleJob.getStatus())) {
                ScheduleJobExpand scheduleJobExpand = scheduleJobExpandDao.getExpandByJobId(scheduleJob.getJobId());
                if (null != scheduleJobExpand && StringUtils.isNotBlank(scheduleJobExpand.getLogInfo()) && scheduleJobExpand.getLogInfo().contains("元数据")) {
                    replaceParamMap.put(ERROR_MSG, scheduleJobExpand.getLogInfo());
                } else {
                    replaceParamMap.put(ERROR_MSG, "-");
                }
            } else {
                replaceParamMap.put(ERROR_MSG, "-");
            }

            if (ScanningAlterStrategy.RESOURCE_OVER_LIMIT.equals(alertRule.getKey())) {
                replaceParamMap.put(JOB_RESOURCE, context.getResourceUsage());
            }
        } else {
            projectId = alertAlarm.getProjectId();
            appType = alertAlarm.getAppType();
            tenantId = alertAlarm.getTenantId();
            ownerUserId = alertAlarm.getCreateUserId();

            BaselineJob baselineJob = context.getBaselineJob();
            BaselineTaskDTO baselineTaskDTO = baselineTaskService.getBaselineTaskDTO(baselineJob.getBaselineTaskId());
            String cycTime = "";
            try {
                //单基线 baseline_task_batch 中cyc_time为空 多基线有值
                if (BaselineTypeEnum.MANY_BATCH.getCode().equals(baselineJob.getBatchType())) {
                    cycTime = DateUtil.getDateStrTOFormat(baselineJob.getCycTime(), DateUtil.STANDARD_DATETIME_FORMAT, DateUtil.STANDARD_FORMAT);
                }
                BaselineTaskBatch baselineTaskBatch = baselineTaskBatchDao.selectByBaselineTaskIdAndCycTime(baselineJob.getBaselineTaskId(), cycTime);
                if (null != baselineTaskBatch) {
                    baselineTaskDTO.setReplyTime(baselineTaskBatch.getReplyTime());
                }
            } catch (ParseException e) {
            }

            // 查询任务名称
            List<AlarmChooseTaskDTO> taskVOS = baselineTaskDTO.getTaskVOS();
            Map<String, AlarmChooseTaskDTO> dtoMap = taskVOS.stream().collect(Collectors.toMap(dto -> dto.getTaskId() + "-" + dto.getAppType(), dto -> (dto)));
            Map<Integer, List<Long>> taskList = taskVOS.stream().collect(Collectors.groupingBy(AlarmChooseTaskDTO::getAppType,
                    Collectors.mapping(AlarmChooseTaskDTO::getTaskId, Collectors.toList())));

            for (Integer appTypeTask : taskList.keySet()) {
                List<Long> taskIds = taskList.get(appTypeTask);

                List<ScheduleTaskShade> tasks = scheduleTaskShadeService.getTaskByIds(taskIds, appTypeTask);

                for (ScheduleTaskShade task : tasks) {
                    AlarmChooseTaskDTO alarmChooseTaskDTO = dtoMap.get(task.getTaskId() + "-" + task.getAppType());
                    if (alarmChooseTaskDTO != null) {
                        alarmChooseTaskDTO.setTaskName(task.getName());
                    }
                }
            }

            // 计算开始时间和结束时间
            List<String> jobIds = baselineJobJobDao.selectJobIdByBaselineJobId(baselineJob.getId(), null);
            List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(jobIds);
            List<ScheduleJob> failedScheduleJobs = scheduleJobs.stream().filter(scheduleJob -> RdosTaskStatus.FAILED_STATUS.contains(scheduleJob.getStatus()))
                    .collect(Collectors.toList());

            long execStartTime = scheduleJobs.stream().map(ScheduleJob::getExecStartTime)
                    .filter(Objects::nonNull).mapToLong(Timestamp::getTime).min().orElse(0L);

            if (execStartTime != 0L) {
                replaceParamMap.put(EXEC_START_TIME, execStartTime);
            }

            long execEndTime = scheduleJobs.stream().map(ScheduleJob::getExecEndTime)
                    .filter(Objects::nonNull).mapToLong(Timestamp::getTime).max().orElse(0L);

            if (execEndTime != 0L) {
                replaceParamMap.put(EXEC_END_TIME, execEndTime);
            }

            if (CollectionUtils.isNotEmpty(failedScheduleJobs)) {
                replaceParamMap.put(BASELINE_STATUS_STR, "未按承诺时间完成（存在失败实例）");
            } else {
                if (execEndTime == 0L) {
                    replaceParamMap.put(BASELINE_STATUS_STR, "未按承诺时间完成");
                } else {
                    String businessTime = DateUtil.getFormattedDate(baselineJob.getBusinessDate().getTime(), DateUtil.DATE_FORMAT);
                    String replyTime = businessTime + " " + baselineTaskDTO.getReplyTime();
                    Date date = DateUtil.parseDate(replyTime, "yyyy-MM-dd HH:mm");
                    if (date != null) {
                        long delayTime = execEndTime - date.getTime();
                        delayTime = (delayTime / 1000) / 60;
                        replaceParamMap.put(BASELINE_STATUS_STR, String.format("未按承诺时间完成（预计延迟%s分钟）", Math.abs(delayTime)));
                    }
                }
            }

            if (execStartTime != 0L) {
                if (execEndTime == 0L) {
                    replaceParamMap.put(EXEC_TIME, DateUtil.getTimeDifference(System.currentTimeMillis() - execStartTime));
                } else {
                    replaceParamMap.put(EXEC_TIME, DateUtil.getTimeDifference(execEndTime - execStartTime));
                }
            }


            replaceParamMap.put(BASELINE_JOB, baselineJob);
            replaceParamMap.put(BASELINE_TASK, baselineTaskDTO);
        }

        BaselineBlockDTO baselineBlockDTO = context.getBaselineBlockDTO();
        if (baselineBlockDTO != null && baselineBlockDTO.getBaselineBlockJobRecord() != null) {
            BaselineBlockJobRecord baselineBlockJobRecord = baselineBlockDTO.getBaselineBlockJobRecord();
            replaceParamMap.put(RELATED_TASK_NAME, baselineBlockJobRecord.getTaskName());
        }

        String sign = environmentContext.getSign();
        replaceParamMap.put(SIGN, sign);

        setProject(projectId, appType, replaceParamMap);
        setTenant(tenantId, replaceParamMap);
        setOwnerUser(ownerUserId, replaceParamMap);
        return replaceTmp(template, replaceParamMap);
    }

    private String formatCycTime(ScheduleJob scheduleJob) {
        String cycTime = scheduleJob.getCycTime();
        try {
            return DateUtil.getDate(DateUtil.parseDate(cycTime, DateUtil.UN_STANDARD_DATETIME_FORMAT), DateUtil.STANDARD_DATETIME_FORMAT);
        } catch (Exception e) {
            return cycTime;
        }
    }

    private String getStatus(int showStatusWithoutStop) {
        if (0 == showStatusWithoutStop) {
            return "等待提交";
        }
        if (4 == showStatusWithoutStop) {
            return "运行中";
        }
        if (5 == showStatusWithoutStop) {
            return "成功";
        }
        if (6 == showStatusWithoutStop) {
            return "取消中";
        }
        if (7 == showStatusWithoutStop) {
            return "手动取消";
        }
        if (8 == showStatusWithoutStop) {
            return "运行失败";
        }
        if (9 == showStatusWithoutStop) {
            return "提交失败";
        }
        if (10 == showStatusWithoutStop) {
            return "提交中";
        }
        if (11 == showStatusWithoutStop) {
            return "重试中";
        }
        if (12 == showStatusWithoutStop) {
            return "置成功";
        }
        if (13 == showStatusWithoutStop) {
            return "已停止";
        }
        if (14 == showStatusWithoutStop) {
            return "提交";
        }
        if (16 == showStatusWithoutStop) {
            return "等待运行";
        }
        if (18 == showStatusWithoutStop) {
            return "冻结";
        }
        if (21 == showStatusWithoutStop) {
            return "上游失败";
        }
        if (22 == showStatusWithoutStop) {
            return "失败";
        }
        if (24 == showStatusWithoutStop) {
            return "自动取消";
        }
        if (25 == showStatusWithoutStop) {
            return "异常";
        }
        return null;
    }

    @Override
    protected String getTitle(AlertRule alertRule, Map<String, Object> replaceParamMap) {
        return replaceTmp(alertRule.getTitle(), replaceParamMap);
    }

    private String replaceTmp(String template, Map<String, Object> replaceParamMap) {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template t = gt.getTemplate(template);
            t.binding(replaceParamMap);
            return t.render();
        } catch (Exception e) {
            LOGGER.error("error happen during render alarm template:{}", e.getMessage(), e);
        }
        return template;
    }

    private void setOwnerUser(Long ownerUserId, Map<String, Object> replaceParamMap) {
        try {
            ApiResponse<List<UICUserVO>> uicUser = uicUserApiClient.getByUserIds(Lists.newArrayList(ownerUserId));

            if (uicUser.success) {
                List<UICUserVO> data = uicUser.getData();

                if (CollectionUtils.isNotEmpty(data)) {
                    UICUserVO uicUserVO = data.get(0);
                    replaceParamMap.put(TemUser.TEM_USER, alterTempStruct.toTemUser(uicUserVO));
                }
            }
        } catch (Exception e) {
            LOGGER.error("setOwnerUser error:{}", e.getMessage(), e);
        }
    }

    private void setTenant(Long tenantId, Map<String, Object> replaceParamMap) {
        try {
            ApiResponse<UICTenantVO> tenant = uicTenantApiClient.findTenantById(tenantId);

            if (tenant.success()) {
                UICTenantVO data = tenant.getData();
                replaceParamMap.put(TemTenant.TEM_TENANT, alterTempStruct.toTemTenant(data));
            }
        } catch (Exception e) {
            LOGGER.error("setTenant error:{}", e.getMessage(), e);
        }
    }

    private void setProject(Long projectId, Integer appType, Map<String, Object> replaceParamMap) {
        try {
            QueryAuthProjectParam queryAuthProjectParam = new QueryAuthProjectParam();
            queryAuthProjectParam.setAppType(appType);
            queryAuthProjectParam.setProjectId(projectId);

            ApiResponse<AuthProjectVO> authCenterAPIClientProject = authCenterAPIClient.findProject(queryAuthProjectParam);

            if (authCenterAPIClientProject.success()) {
                AuthProjectVO data = authCenterAPIClientProject.getData();
                replaceParamMap.put(TemProject.TEM_PROJECT, alterTempStruct.toTemProject(data));
            }
        } catch (Exception e) {
            LOGGER.error("setProject error:{}", e.getMessage(), e);
        }
    }

    public static class TemUser {
        public final static String TEM_USER = "user";

        /**
         * 用户id
         */
        private Long userId;

        /**
         * 用户名称
         */
        private String userName;

        /**
         * 完整名称
         */
        private String fullName;

        /**
         * 是否是root
         */
        private Boolean isRoot;

        /**
         * 租户id
         */
        private Long tenantId;

        /**
         * 租户名称
         */
        private String tenantName;

        /**
         * 用户手机号
         */
        private String phone;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Boolean getRoot() {
            return isRoot;
        }

        public void setRoot(Boolean root) {
            isRoot = root;
        }

        public Long getTenantId() {
            return tenantId;
        }

        public void setTenantId(Long tenantId) {
            this.tenantId = tenantId;
        }

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class TemTenant {

        public final static String TEM_TENANT = "tenant";
        /**
         * 租户id
         */
        private Long tenantId;

        /**
         * 租户名称
         */
        private String tenantName;

        /**
         * 租户是否是admin用户
         */
        private boolean tenantAdmin;

        public Long getTenantId() {
            return tenantId;
        }

        public void setTenantId(Long tenantId) {
            this.tenantId = tenantId;
        }

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        public boolean isTenantAdmin() {
            return tenantAdmin;
        }

        public void setTenantAdmin(boolean tenantAdmin) {
            this.tenantAdmin = tenantAdmin;
        }
    }

    public static class TemProject {

        public final static String TEM_PROJECT = "project";

        private Long projectId;

        /**
         * 应用类型
         */
        private Integer appType;

        /**
         * 项目名称
         */
        private String projectName;

        /**
         * 项目中文名称
         */
        private String projectAlias;

        /**
         * 项目标识
         */
        private String projectIdentifier;

        /**
         * 项目描述
         */
        private String projectDesc;

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Integer getAppType() {
            return appType;
        }

        public void setAppType(Integer appType) {
            this.appType = appType;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getProjectAlias() {
            return projectAlias;
        }

        public void setProjectAlias(String projectAlias) {
            this.projectAlias = projectAlias;
        }

        public String getProjectIdentifier() {
            return projectIdentifier;
        }

        public void setProjectIdentifier(String projectIdentifier) {
            this.projectIdentifier = projectIdentifier;
        }

        public String getProjectDesc() {
            return projectDesc;
        }

        public void setProjectDesc(String projectDesc) {
            this.projectDesc = projectDesc;
        }
    }
}
