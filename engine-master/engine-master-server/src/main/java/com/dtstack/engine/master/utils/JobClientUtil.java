package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.pojo.FsyncSqlParam;
import com.dtstack.engine.api.pojo.GrammarCheckParam;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.catalog.Catalog;
import com.dtstack.engine.api.pojo.sftp.SftpConfig;
import com.dtstack.engine.common.CatalogInfo;
import com.dtstack.engine.common.FsyncSqlParamInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/11/5 1:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobClient.class);

    public static JobClient conversionJobClient(ParamAction paramAction) throws IOException {
        JobClient jobClient = new JobClient();
        jobClient.setSql(paramAction.getSqlText());
        jobClient.setTaskParams(paramAction.getTaskParams());
        jobClient.setJobName(paramAction.getName());
        jobClient.setTaskId(paramAction.getTaskId());
        jobClient.setEngineTaskId(paramAction.getEngineTaskId());
        jobClient.setApplicationId(paramAction.getApplicationId());
        jobClient.setJobType(EJobType.getEjobType(EScheduleJobType.getEngineJobType(paramAction.getTaskType())));
        jobClient.setComputeType(ComputeType.getType(paramAction.getComputeType()));
        jobClient.setExternalPath(paramAction.getExternalPath());
        jobClient.setEngineType(paramAction.getEngineType());
        jobClient.setClassArgs(paramAction.getExeArgs());
        jobClient.setGenerateTime(paramAction.getGenerateTime());
        jobClient.setLackingCount(paramAction.getLackingCount());
        jobClient.setTenantId(paramAction.getTenantId());
        jobClient.setUserId(paramAction.getUserId());
        jobClient.setAppType(paramAction.getAppType());
        jobClient.setQueueSourceType(EQueueSourceType.NORMAL.getCode());
        jobClient.setSubmitExpiredTime(paramAction.getSubmitExpiredTime());
        jobClient.setRetryIntervalTime(paramAction.getRetryIntervalTime());
        jobClient.setComponentVersion(paramAction.getComponentVersion());
        jobClient.setProjectId(paramAction.getProjectId());
        jobClient.setAppType(paramAction.getAppType());
        jobClient.setTaskType(paramAction.getTaskType());
        jobClient.setRetryType(paramAction.getRetryType());
        jobClient.setClassArgs(paramAction.getExeArgs());
        jobClient.setTaskSourceId(paramAction.getTaskSourceId());
        jobClient.setMaxRetryNum(paramAction.getMaxRetryNum() == null ? 0 : paramAction.getMaxRetryNum());
        if (paramAction.getPluginInfo() != null) {
            jobClient.setPluginInfo(PublicUtil.objToString(paramAction.getPluginInfo()));
        }
        if (jobClient.getTaskParams() != null) {
            jobClient.setConfProperties(PublicUtil.trimProperties(PublicUtil.stringToProperties(jobClient.getTaskParams())));
        }
        if (paramAction.getPriority() <= 0) {
            String valStr = jobClient.getConfProperties() == null ? null : jobClient.getConfProperties().getProperty(ConfigConstant.CUSTOMER_PRIORITY_VAL);
            jobClient.setPriorityLevel( valStr == null ? JobClient.DEFAULT_PRIORITY_LEVEL_VALUE : MathUtil.getIntegerVal(valStr));
            //设置priority值, 值越小，优先级越高
            jobClient.setPriority(paramAction.getGenerateTime() + (long)jobClient.getPriorityLevel() * JobClient.PRIORITY_LEVEL_WEIGHT) ;
        } else {
            jobClient.setPriority(paramAction.getPriority());
        }
        String groupName = paramAction.getGroupName();
        if (StringUtils.isBlank(groupName)) {
            groupName = ConfigConstant.DEFAULT_GROUP_NAME;
        }
        jobClient.setClientType(ClientTypeEnum.getClientTypeEnum(paramAction.getClientType()));
        jobClient.setGroupName(groupName);
        //将任务id 标识为对象id
        jobClient.setId(paramAction.getTaskId());
        jobClient.setResourceId(paramAction.getResourceId());
        //catalog
        JobClientUtil.convertCatalogList(paramAction.getCatalogInfo(), jobClient);

        // 离线数据同步ftp自定义解析jar包路径
        jobClient.setUserCustomDefineJarPath(paramAction.getUserCustomDefineJarPath());
        // @see com.dtstack.engine.master.impl.StreamTaskService.executeSql
        jobClient.setExtraInfo(paramAction.getExtraInfo());
        jobClient.setFsyncSql(paramAction.getFsyncSql());
        JobClientUtil.convertFsyncSqlParamInfo(paramAction.getFsyncSqlParam(), jobClient);
        return jobClient;
    }

    public static ParamAction getParamAction(JobClient jobClient) {
        if (jobClient == null) {
            return null;
        }
        ParamAction action = new ParamAction();
        action.setSqlText(jobClient.getSql());
        action.setTaskParams(jobClient.getTaskParams());
        action.setName(jobClient.getJobName());
        action.setTaskId(jobClient.getTaskId());
        action.setEngineTaskId(jobClient.getEngineTaskId());
        action.setComputeType(jobClient.getComputeType().getType());
        action.setExternalPath(jobClient.getExternalPath());
        action.setEngineType(jobClient.getEngineType());
        action.setExeArgs(jobClient.getClassArgs());
        action.setGroupName(jobClient.getGroupName());
        action.setGenerateTime(jobClient.getGenerateTime());
        action.setPriority(jobClient.getPriority());
        action.setApplicationId(jobClient.getApplicationId());
        action.setMaxRetryNum(jobClient.getMaxRetryNum());
        action.setRetryType(jobClient.getRetryType());
        action.setLackingCount(jobClient.getLackingCount());
        action.setTenantId(jobClient.getTenantId());
        action.setUserId(jobClient.getUserId());
        action.setAppType(jobClient.getAppType());
        action.setRetryIntervalTime(jobClient.getRetryIntervalTime());
        action.setSubmitExpiredTime(jobClient.getSubmitExpiredTime());
        action.setComponentVersion(jobClient.getComponentVersion());
        action.setTaskType(jobClient.getTaskType());
        action.setAppType(jobClient.getAppType());
        action.setProjectId(jobClient.getProjectId());
        action.setResourceId(jobClient.getResourceId());
        action.setExeArgs(jobClient.getClassArgs());
        if (!Strings.isNullOrEmpty(jobClient.getPluginInfo())) {
            try {
                action.setPluginInfo(PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), Map.class));
            } catch (Exception e) {
                //不应该走到这个异常,这个数据本身是由map转换过来的
                LOGGER.error("", e);
            }
        }

        ClientTypeEnum clientType = jobClient.getClientType();

        if (clientType != null) {
            action.setClientType(clientType.getCode());
        }
        action.setUserCustomDefineJarPath(jobClient.getUserCustomDefineJarPath());

        // @see com.dtstack.engine.master.impl.StreamTaskService.executeSql
        action.setExtraInfo(jobClient.getExtraInfo());
        action.setFsyncSql(jobClient.getFsyncSql());
        convert2FsyncSqlParam(jobClient.getFsyncSqlParamInfo(), action);
        return action;
    }

    private static void convert2FsyncSqlParam(FsyncSqlParamInfo fsyncSqlParamInfo, ParamAction action) {
        if (fsyncSqlParamInfo == null) {
            return;
        }
        FsyncSqlParam fsyncSqlParam = new FsyncSqlParam();
        BeanUtils.copyProperties(fsyncSqlParamInfo, fsyncSqlParam);
        action.setFsyncSqlParam(fsyncSqlParam);
    }

    public static JobClient convert2JobClient(GrammarCheckParam grammarCheckParam) {
        JobClient jobClient = new JobClient();
        jobClient.setSql(grammarCheckParam.getSqlText());
        jobClient.setJobType(EJobType.getEjobType(EScheduleJobType.getEngineJobType(grammarCheckParam.getTaskType())));
        jobClient.setComputeType(ComputeType.getType(grammarCheckParam.getComputeType()));
        jobClient.setQueueSourceType(EQueueSourceType.NORMAL.getCode());
        jobClient.setGroupName(ConfigConstant.DEFAULT_GROUP_NAME);
        BeanUtils.copyProperties(grammarCheckParam, jobClient);
        return jobClient;
    }

    /**
     * convert sdk catalogList to jobClient catalogInfoList
     *
     * @param catalogList sdk 接受 catalogList
     * @param jobClient   转换后的 jobClient
     */
    public static void convertCatalogList(List<Catalog> catalogList, JobClient jobClient) {
        if (CollectionUtils.isEmpty(catalogList)) {
            return;
        }
        List<CatalogInfo> catalogInfoList = Lists.newArrayList();
        for (Catalog catalog : catalogList) {
            if (Objects.isNull(catalog)) {
                continue;
            }
            CatalogInfo apiCatalog = new CatalogInfo();
            BeanUtils.copyProperties(catalog, apiCatalog);
            SftpConfig sftpConfig = catalog.getSftpConfig();
            BeanUtils.copyProperties(catalog, apiCatalog);
            if (sftpConfig != null) {
                com.dtstack.engine.common.sftp.SftpConfig apiSftpConfig = new com.dtstack.engine.common.sftp.SftpConfig();
                BeanUtils.copyProperties(sftpConfig, apiSftpConfig);
                apiCatalog.setSftpConfig(apiSftpConfig);
            }
            catalogInfoList.add(apiCatalog);
        }
        jobClient.setCatalogInfo(catalogInfoList);
    }

    /**
     * convert sdk catalog to jobClient catalogInfo
     *
     * @param catalogInfo sdk 接受 catalog
     * @param jobClient   转换后的 jobClient
     */
    public static void convertCatalog(Catalog catalogInfo, JobClient jobClient) {
        convertCatalogList(Lists.newArrayList(catalogInfo), jobClient);
    }

    private static void convertFsyncSqlParamInfo(FsyncSqlParam fsyncSqlParam, JobClient jobClient) {
        if (fsyncSqlParam == null) {
            return;
        }
        FsyncSqlParamInfo fsyncSqlParamInfo = new FsyncSqlParamInfo();
        BeanUtils.copyProperties(fsyncSqlParam, fsyncSqlParamInfo);
        jobClient.setFsyncSqlParamInfo(fsyncSqlParamInfo);
    }
}
