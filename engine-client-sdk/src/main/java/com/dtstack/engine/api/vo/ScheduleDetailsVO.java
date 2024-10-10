package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.vo.task.TaskCustomParamVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/22 12:50 下午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class ScheduleDetailsVO {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 用户id
     */
    private Long ownerUserId;

    /**
     * 周期类型
     */
    private Integer periodType;

    /**
     * 提交时间
     */
    private String gmtCreate;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 规则任务类型
     */
    private Integer taskRule;

    /**
     * 任务状态
     */
    private Integer scheduleStatus;

    /**
     * 任务生成状态
     */
    private Integer projectScheduleStatus;

    /**
     * 项目别名
     */
    private String projectAlias;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 资源组名称
     */
    private String resourceName;

    /**
     * 等待原因
     */
    private String waitReason;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 业务日期
     */
    private String businessDate;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 开始时间
     */
    private String execStartTime;

    /**
     * 结束时间
     */
    private String execEndTime;

    /**
     * 实例状态
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryNum;

    /**
     * 运行次数
     */
    private Integer runNum;


    private Long calenderId;

    private String calenderName;

    private String scheduleConf;

    private Integer taskIsDeleted;

    private TaskCustomParamVO taskCustomParamVO;

    private Boolean enableJobMonitor;

    private List<Long> tagIds;

    /**
     * 任务优先级
     */
    private Integer priority;

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public String getCalenderName() {
        return calenderName;
    }

    public void setCalenderName(String calenderName) {
        this.calenderName = calenderName;
    }

    private List<ScheduleDetailsVO> scheduleDetailsVOList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Integer getProjectScheduleStatus() {
        return projectScheduleStatus;
    }

    public void setProjectScheduleStatus(Integer projectScheduleStatus) {
        this.projectScheduleStatus = projectScheduleStatus;
    }

    public String getProjectAlias() {
        return projectAlias;
    }

    public void setProjectAlias(String projectAlias) {
        this.projectAlias = projectAlias;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getWaitReason() {
        return waitReason;
    }

    public void setWaitReason(String waitReason) {
        this.waitReason = waitReason;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public String getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(String execStartTime) {
        this.execStartTime = execStartTime;
    }

    public String getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(String execEndTime) {
        this.execEndTime = execEndTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public Integer getRunNum() {
        return runNum;
    }

    public void setRunNum(Integer runNum) {
        this.runNum = runNum;
    }

    public String getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public List<ScheduleDetailsVO> getScheduleDetailsVOList() {
        return scheduleDetailsVOList;
    }

    public void setScheduleDetailsVOList(List<ScheduleDetailsVO> scheduleDetailsVOList) {
        this.scheduleDetailsVOList = scheduleDetailsVOList;
    }

    public Integer getTaskIsDeleted() {
        return taskIsDeleted;
    }

    public void setTaskIsDeleted(Integer taskIsDeleted) {
        this.taskIsDeleted = taskIsDeleted;
    }

    public TaskCustomParamVO getTaskCustomParamVO() {
        return taskCustomParamVO;
    }

    public void setTaskCustomParamVO(TaskCustomParamVO taskCustomParamVO) {
        this.taskCustomParamVO = taskCustomParamVO;
    }

    public Boolean getEnableJobMonitor() {
        return enableJobMonitor;
    }

    public void setEnableJobMonitor(Boolean enableJobMonitor) {
        this.enableJobMonitor = enableJobMonitor;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
