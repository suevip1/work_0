package com.dtstack.engine.common;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.queue.OrderObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class JobClient extends OrderObject {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(JobClient.class);

    /**
     * 默认的优先级，值越小，优先级越高
     */
    public static final int DEFAULT_PRIORITY_LEVEL_VALUE = 10;

    /**
     * 用户填写的优先级占的比重
     */
    public static final int PRIORITY_LEVEL_WEIGHT = 100000;


    private transient JobClientCallBack jobClientCallBack;

    private List<JarFileInfo> attachJarInfos = Lists.newArrayList();

    private JarFileInfo coreJarInfo;

    private Properties confProperties;

    private String sql;

    private String taskParams;

    private String jobName;

    private String taskId;

    private String engineTaskId;

    private String applicationId;

    private EJobType jobType;

    private ComputeType computeType;

    private String engineType;

    private JobResult jobResult;

    /**
     * externalPath 不为null则为从保存点恢复
     */
    private String externalPath;

    /**
     * 提交MR执行的时候附带的执行参数
     */
    private String classArgs;

    private int again = 1;

    private String groupName;

    private int priorityLevel = 0;

    private String pluginInfo;

    private long generateTime;

    private int maxRetryNum;

    private String retryType;

    private volatile long lackingCount;

    /**
     * uic租户信息
     **/
    private Long tenantId;

    private Long userId;

    private Integer appType;

    private Integer queueSourceType;

    private Long submitCacheTime;

    private Boolean isForceCancel;

    /**
     * 重试超时时间
     */
    private long submitExpiredTime;

    /**
     * 重试间隔时间
     */
    private Long retryIntervalTime;

    /**
     * 任务运行版本 1.12
     */
    private String componentVersion;
    /**
     * 0正常调度 1补数据 2临时运行
     */
    private Integer type;

    private Integer deployMode;

    private Long resourceId;

    private ClientTypeEnum clientType;

    private Integer taskType;

    private Long projectId;


    private List<CatalogInfo> catalogInfo;

    // 离线数据同步ftp自定义解析jar包路径
    private String userCustomDefineJarPath;

    // @see com.dtstack.engine.master.impl.StreamTaskService.executeSql
    private Map<String,String> extraInfo;

    private Long taskSourceId;

    private boolean hotReloading;

    public boolean isHotReloading() {
        return hotReloading;
    }

    public void setHotReloading(boolean hotReloading) {
        this.hotReloading = hotReloading;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * 离线落盘 sql
     * @see FsyncSqlParamInfo
     */
    @Deprecated
    private String fsyncSql;

    /**
     * 离线落盘 sql
     */
    private FsyncSqlParamInfo fsyncSqlParamInfo;

    public FsyncSqlParamInfo getFsyncSqlParamInfo() {
        return fsyncSqlParamInfo;
    }

    public void setFsyncSqlParamInfo(FsyncSqlParamInfo fsyncSqlParamInfo) {
        this.fsyncSqlParamInfo = fsyncSqlParamInfo;
    }

    public String getFsyncSql() {
        return fsyncSql;
    }

    public void setFsyncSql(String fsyncSql) {
        this.fsyncSql = fsyncSql;
    }

    public JobClientCallBack getJobClientCallBack() {
        return jobClientCallBack;
    }

    public void setJobClientCallBack(JobClientCallBack jobClientCallBack) {
        this.jobClientCallBack = jobClientCallBack;
    }

    public String getUserCustomDefineJarPath() {
        return userCustomDefineJarPath;
    }

    public void setUserCustomDefineJarPath(String userCustomDefineJarPath) {
        this.userCustomDefineJarPath = userCustomDefineJarPath;
    }

    public List<CatalogInfo> getCatalogInfo() {
        return catalogInfo;
    }

    public void setCatalogInfo(List<CatalogInfo> catalogInfo) {
        this.catalogInfo = catalogInfo;
    }

    public JobClient() {

    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(Integer deployMode) {
        this.deployMode = deployMode;
    }

    public Boolean getForceCancel() {
        return isForceCancel;
    }

    public void setForceCancel(Boolean forceCancel) {
        isForceCancel = forceCancel;
    }

    public void setPluginWrapperInfo(Map pluginInfoMap) {
        if (null == pluginInfoMap) {
            throw new RdosDefineException("pluginInfo map must not be null.");
        }
        this.pluginInfo = JSONObject.toJSONString(pluginInfoMap);
    }

    public Integer getQueueSourceType() {
        return queueSourceType;
    }

    public void setQueueSourceType(Integer queueSourceType) {
        this.queueSourceType = queueSourceType;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getEngineTaskId() {
        return engineTaskId;
    }

    public void setEngineTaskId(String engineTaskId) {
        this.engineTaskId = engineTaskId;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public EJobType getJobType() {
        return jobType;
    }

    public void setJobType(EJobType jobType) {
        this.jobType = jobType;
    }

    public JobResult getJobResult() {
        return jobResult;
    }

    public void setJobResult(JobResult jobResult) {
        this.jobResult = jobResult;
    }

    public ComputeType getComputeType() {
        return computeType;
    }

    public void setComputeType(ComputeType computeType) {
        this.computeType = computeType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Properties getConfProperties() {
        return confProperties;
    }

    public void setConfProperties(Properties confProperties) {
        this.confProperties = confProperties;
    }

    public List<JarFileInfo> getAttachJarInfos() {
        return attachJarInfos;
    }

    public void setAttachJarInfos(List<JarFileInfo> attachJarInfos) {
        this.attachJarInfos = attachJarInfos;
    }

    public void addAttachJarInfo(JarFileInfo jarFileInfo) {
        attachJarInfos.add(jarFileInfo);
    }

    public void doStatusCallBack(Integer status) {
        if (jobClientCallBack == null) {
            throw new RdosDefineException("not set jobClientCallBak...");
        }
        jobClientCallBack.updateStatus(status);
    }

    public void setCallBack(JobClientCallBack jobClientCallBack) {
        this.jobClientCallBack = jobClientCallBack;
    }

    public JobClientCallBack getJobCallBack() {
        return jobClientCallBack;
    }

    public String getSql() {
        return sql;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public String getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(String classArgs) {
        this.classArgs = classArgs;
    }

    public int getAgain() {
        return again;
    }

    public void setAgain(int again) {
        this.again = again;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public JarFileInfo getCoreJarInfo() {
        return coreJarInfo;
    }

    public void setCoreJarInfo(JarFileInfo coreJarInfo) {
        this.coreJarInfo = coreJarInfo;
    }

    public long getGenerateTime() {
        if (generateTime <= 0) {
            generateTime = System.currentTimeMillis();
        }
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public int getApplicationPriority() {
        return Integer.MAX_VALUE - (int) (getPriority() / 1000);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public int getMaxRetryNum() {
        return maxRetryNum;
    }

    public boolean getIsFailRetry() {
        return maxRetryNum > 0;
    }

    public void setMaxRetryNum(int maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public String getClusterType() {
        return null;
    }

    public String getResourceType() {
        return null;
    }

    public long getLackingCount() {
        return lackingCount;
    }

    public void setLackingCount(long lackingCount) {
        this.lackingCount = lackingCount;
    }

    public long lackingCountIncrement() {
        return lackingCount++;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getAppType() {
        return appType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobClient jobClient = (JobClient) o;
        return taskId != null ? taskId.equals(jobClient.taskId) : jobClient.taskId == null;
    }

    public Long getSubmitCacheTime() {
        return submitCacheTime;
    }

    public void setSubmitCacheTime(Long submitCacheTime) {
        this.submitCacheTime = submitCacheTime;
    }

    public long getSubmitExpiredTime() {
        return submitExpiredTime;
    }

    public void setSubmitExpiredTime(long submitExpiredTime) {
        this.submitExpiredTime = submitExpiredTime;
    }

    public Long getRetryIntervalTime() {
        return retryIntervalTime;
    }

    public void setRetryIntervalTime(Long retryIntervalTime) {
        this.retryIntervalTime = retryIntervalTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
    
    public ClientTypeEnum getClientType() {
        return clientType;
    }

    public void setClientType(ClientTypeEnum clientType) {
        this.clientType = clientType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRetryType() {
        return retryType;
    }

    public void setRetryType(String retryType) {
        this.retryType = retryType;
    }

    public Long getTaskSourceId() {
        return taskSourceId;
    }

    public void setTaskSourceId(Long taskSourceId) {
        this.taskSourceId = taskSourceId;
    }

    @Override
    public String toString() {
        return "JobClient{" +
                "jobClientCallBack=" + jobClientCallBack +
                ", attachJarInfos=" + attachJarInfos +
                ", coreJarInfo=" + coreJarInfo +
                ", confProperties=" + confProperties +
                ", sql='" + sql + '\'' +
                ", jobName='" + jobName + '\'' +
                ", taskId='" + taskId + '\'' +
                ", engineTaskId='" + engineTaskId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", jobType=" + jobType +
                ", computeType=" + computeType +
                ", engineType='" + engineType + '\'' +
                ", jobResult=" + jobResult +
                ", externalPath='" + externalPath + '\'' +
                ", classArgs='" + classArgs + '\'' +
                ", again=" + again +
                ", groupName='" + groupName + '\'' +
                ", priorityLevel=" + priorityLevel +
                ", generateTime=" + generateTime +
                ", maxRetryNum=" + maxRetryNum +
                ", lackingCount=" + lackingCount +
                ", tenantId=" + tenantId +
                ", userId=" + userId +
                ", resourceId=" + resourceId +
                ", appType=" + appType +
                '}';
    }


}
