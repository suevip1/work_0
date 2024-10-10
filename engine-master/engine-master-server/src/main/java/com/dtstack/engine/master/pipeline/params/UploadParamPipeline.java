package com.dtstack.engine.master.pipeline.params;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.utils.SpringUtil;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.master.pipeline.IPipeline;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.utils.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class UploadParamPipeline extends IPipeline.AbstractPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadParamPipeline.class);

    public static final String fileUploadPathKey = "fileUploadPath";
    public static final String workOperatorKey = "workOperator";
    public static final String rdosWrapperKey = "rdosWrapper";
    public static final String pluginInfoKey = "pluginInfo";
    public static final String pipelineKey = "uploadPath";
    public static final String tenantIdKey ="tenantId";

    public UploadParamPipeline() {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws RdosDefineException {
        if (pipelineParam.containsKey(pipelineKey)) {
            return;
        }
        ScheduleTaskShade taskShade = (ScheduleTaskShade) pipelineParam.get(taskShadeKey);
        if (null == taskShade) {
            throw new RdosDefineException("upload param pipeline task shade can not be null");
        }
        ScheduleJob scheduleJob = (ScheduleJob) pipelineParam.get(scheduleJobKey);
        if (null == scheduleJob) {
            throw new RdosDefineException("upload param pipeline schedule job can not be null");
        }
        String fileUploadPath = (String) pipelineParam.get(fileUploadPathKey);
        if (StringUtils.isBlank(fileUploadPath)) {
            throw new RdosDefineException("upload param pipeline fileUploadPath can not be null");
        }
        RdosWrapper rdosWrapper = (RdosWrapper) pipelineParam.get(rdosWrapperKey);
        if (null == rdosWrapper) {
            throw new RdosDefineException("upload param pipeline rdosWrapper can not be null");
        }

        Long tenantId = (Long) pipelineParam.get(tenantIdKey);
        if (null == tenantId) {
            throw new RdosDefineException("upload param pipeline tenantId can not be null");
        }

        JSONObject pluginInfo = (JSONObject) pipelineParam.get(pluginInfoKey);
        if (null == pluginInfo) {
            throw new RdosDefineException("upload param pipeline pluginInfo can not be null");
        }
        @SuppressWarnings("unchecked")
        List<ScheduleTaskParamShade> taskParamShades = (List) pipelineParam.get(taskParamsToReplaceKey);

        String uploadPath = this.uploadSqlTextToHdfs(taskShade.getSqlText(), taskShade.getTaskType(), taskParamShades, scheduleJob.getCycTime(),
                fileUploadPath, pluginInfo, rdosWrapper, scheduleJob.getJobId(),tenantId,scheduleJob.getType(), taskShade.getProjectId());

        pipelineParam.put(pipelineKey, uploadPath);
    }


    private String uploadSqlTextToHdfs(String content, Integer taskType,
                                       List<ScheduleTaskParamShade> taskParamShades, String cycTime, String fileUploadPath,
                                       JSONObject pluginInfo, RdosWrapper rdosWrapper, String jobId,Long dtuicTenantId,Integer scheduleType, Long projectId) throws RdosDefineException {
        String fileName = FileUtil.getUploadFileName(taskType,jobId);
        try {
            //content统一处理参数
            if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamShades)) {
                content = SpringUtil.getBean(JobParamReplace.class).paramReplace(content, taskParamShades, cycTime,scheduleType, projectId);
            }
            String hdfsPath = fileUploadPath + fileName;
            if (EScheduleJobType.SHELL.getVal().equals(taskType)) {
                content = content.replaceAll("\r\n", System.getProperty("line.separator"));
            }
            String typeName = pluginInfo.getString(ConfigConstant.TYPE_NAME_KEY);
            Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
            pluginInfo.put("dataSourceType",dataSourceCodeByDiceName);
            IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCodeByDiceName);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtuicTenantId);
            String hdfsUploadPath = hdfs.uploadStringToHdfs(sourceDTO,content,hdfsPath);
            if (StringUtils.isBlank(hdfsUploadPath)) {
                throw new RdosDefineException("Update task to HDFS failure hdfsUploadPath is blank");
            }
            return hdfsUploadPath;
        } catch (Exception e) {
            LOGGER.error("Update task to HDFS failure: ERROR {}", jobId, e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }
    }


}
