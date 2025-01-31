package com.dtstack.engine.api.pojo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.catalog.Catalog;
import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
@ApiModel
public class ParamAction {

	private static final Logger logger = LoggerFactory.getLogger(ParamAction.class);

	private String taskId;

	/**
	 * 任务id
	 */
	private Long taskSourceId;

	private String engineTaskId;

	private String applicationId;

	private String name;

    private Integer taskType;

    private String engineType;

    private Integer computeType;

	//实时独有
	private String externalPath;

	private String sqlText;

	private String taskParams;

	private String exeArgs;

	private String groupName;

	//选填参数,如果请求指定集群信息的话需要填写
	private Map<String, Object> pluginInfo;

	/**
	 * 0 是从web端发起，1是有内部节点发起，如果是1就会直接执行不会再判断node运行的task任务在进行路由选择
	 */
	private Integer requestStart = 0;

	private Integer sourceType;

	private long priority;

	private long generateTime = System.currentTimeMillis();

	private Integer maxRetryNum;

	/**
	 * 任务重试类型, 默认为续跑
	 * 续跑：CONTINUE
	 * 重跑：RERUN
	 */
	private String retryType;

	private long stopJobId;

	private long lackingCount;

	private Long tenantId;

	private Long dtuicTenantId;

	private Long userId;

	private String ldapUserName;

	private String ldapPassword;

	private String deployMode;

	private String dbName;

	private Integer appType;

	/**
	 * 重试超时时间
	 */
	private long submitExpiredTime;

	/**
	 * 重试间隔时间
	 */
	private Long retryIntervalTime;

    /**
     * 任务运行版本 如flink 1.8 1.10
     */
	private String componentVersion;

	private String businessType;

	private Long resourceId;

	private Integer clientType;

	private Long projectId;

    private List<Catalog> catalogInfo;

	// 离线数据同步ftp自定义解析jar包路径
	private String userCustomDefineJarPath;

	// @see com.dtstack.engine.master.impl.StreamTaskService.executeSql
	private Map<String,String> extraInfo;

	public Map<String, String> getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(Map<String, String> extraInfo) {
		this.extraInfo = extraInfo;
	}

	/**
	 * 离线落盘 sql
	 * @see FsyncSqlParam
	 */
	@Deprecated
	private String fsyncSql;

	/**
	 * 离线落盘 sql
	 */
	private FsyncSqlParam fsyncSqlParam;

	public FsyncSqlParam getFsyncSqlParam() {
		return fsyncSqlParam;
	}

	public void setFsyncSqlParam(FsyncSqlParam fsyncSqlParam) {
		this.fsyncSqlParam = fsyncSqlParam;
	}

	public String getFsyncSql() {
		return fsyncSql;
	}

	public void setFsyncSql(String fsyncSql) {
		this.fsyncSql = fsyncSql;
	}

	public String getUserCustomDefineJarPath() {
		return userCustomDefineJarPath;
	}

	public void setUserCustomDefineJarPath(String userCustomDefineJarPath) {
		this.userCustomDefineJarPath = userCustomDefineJarPath;
	}

	public List<Catalog> getCatalogInfo() {
		return catalogInfo;
	}

	public void setCatalogInfo(List<Catalog> catalogInfo) {
		this.catalogInfo = catalogInfo;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getComponentVersion() {
		return componentVersion;
	}

	public void setComponentVersion(String componentVersion) {
		this.componentVersion = componentVersion;
	}

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public String getLdapUserName() {
		return ldapUserName;
	}

	public void setLdapUserName(String ldapUserName) {
		this.ldapUserName = ldapUserName;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getDeployMode() {
		return deployMode;
	}

	public void setDeployMode(String deployMode) {
		this.deployMode = deployMode;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getRequestStart() {
		return requestStart;
	}

	public void setRequestStart(Integer requestStart) {
		this.requestStart = requestStart;
	}

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

	public Integer getComputeType() {
		return computeType;
	}

	public void setComputeType(Integer computeType) {
		this.computeType = computeType;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(String taskParams) {
		this.taskParams = taskParams;
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

	public String getExternalPath() {
		return externalPath;
	}

	public void setExternalPath(String externalPath) {
		this.externalPath = externalPath;
	}

	public String getEngineType() {
		return engineType;
	}

	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}


	public String getExeArgs() {
		return exeArgs;
	}


	public void setExeArgs(String exeArgs) {
		this.exeArgs = exeArgs;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Map<String, Object> getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(Map<String, Object> pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public long getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(long generateTime) {
		this.generateTime = generateTime;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getMaxRetryNum() {
		return maxRetryNum;
	}

	public void setMaxRetryNum(Integer maxRetryNum) {
		this.maxRetryNum = maxRetryNum;
	}

	public long getStopJobId() {
		return stopJobId;
	}

	public void setStopJobId(long stopJobId) {
		this.stopJobId = stopJobId;
	}

	public long getLackingCount() {
		return lackingCount;
	}

	public void setLackingCount(long lackingCount) {
		this.lackingCount = lackingCount;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getDtuicTenantId() {
		return dtuicTenantId;
	}

	public void setDtuicTenantId(Long dtuicTenantId) {
		this.dtuicTenantId = dtuicTenantId;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
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

	public Integer getClientType() {
		return clientType;
	}

	public void setClientType(Integer clientType) {
		this.clientType = clientType;
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
		String jsonStr = "";
		try{
			jsonStr = JSONObject.toJSONString(this);
		}catch (Exception e){
			//不应该发生
			logger.error("", e);
		}

		return jsonStr;
	}
}
