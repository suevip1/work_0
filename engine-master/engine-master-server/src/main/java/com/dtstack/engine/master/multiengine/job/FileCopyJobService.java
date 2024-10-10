package com.dtstack.engine.master.multiengine.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.ScriptJobStartTrigger;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.schedule.common.enums.DataBaseType;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * @author yuebai
 * date 2020-12-08
 */
@Component
public class FileCopyJobService {

    private static final Logger LOG = LoggerFactory.getLogger(FileCopyJobService.class);

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private ScriptJobStartTrigger scriptJobStartTrigger;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;

    public String buildFileCopyTask(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob batchJob) throws Exception {
        Object sqlTextObj = actionParam.get("sqlText");
        Map sqlText;
        if (sqlTextObj instanceof String) {
            sqlText = JSONObject.parseObject((String) sqlTextObj);
        } else {
            sqlText = (Map) sqlTextObj;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        //写入对应的数据库地址
        String reader = jobParamReplace.paramReplace((String) sqlText.get("reader"), taskParamsToReplace, batchJob.getCycTime(),batchJob.getType(), taskShade.getProjectId());
        String writer = jobParamReplace.paramReplace((String) sqlText.get("writer"), taskParamsToReplace, batchJob.getCycTime(),batchJob.getType(), taskShade.getProjectId());

        Integer readerSourceType = (Integer) JSONPath.read(reader, "$.parameter.sourceType");
        Integer writerSourceType = (Integer) JSONPath.read(writer, "$.parameter.sourceType");

        Map<String, String> replaceParams;
        String fileCopyShell;
        if (Objects.nonNull(readerSourceType) && Objects.nonNull(writerSourceType) &&
                DataSourceType.HIVE.getVal() == readerSourceType && DataSourceType.FTP.getVal() == writerSourceType) {
            //hive 到sftp
            replaceParams = this.getHiveToSftpParamToReplace(reader, writer, batchJob, taskParamsToReplace);
            fileCopyShell = this.getFileShellByName("hiveToSftp.sh");
        } else {
            //兼容老数据 sftp 到hive
            replaceParams = this.getSftpToHiveParamToReplace(reader, writer, batchJob, taskParamsToReplace);
            fileCopyShell = this.getFileShellByName("sftpToHive.sh");
        }
        // 统一替换对应的变量
        StrSubstitutor strSubstitutor = new StrSubstitutor(replaceParams, "${", "}");
        fileCopyShell = strSubstitutor.replace(fileCopyShell);

        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        if (userId == null) {
            userId = taskShade.getOwnerUserId();
        }

        // maxAppAttempts container 分配不重试
        String taskExeArgs = String.format("--files ${uploadPath} --app-type shell --app-name %s --maxAppAttempts 0", taskShade.getName());
        taskExeArgs = taskExeArgs.replace(TaskConstant.UPLOADPATH, scriptJobStartTrigger.uploadSqlTextToHdfs(taskShade.getProjectId(), batchJob.getAppType(), batchJob.getDtuicTenantId(), fileCopyShell, taskShade.getTaskType(),
                taskParamsToReplace, batchJob.getCycTime(), taskShade.getName(), batchJob.getType(),userId));
        return taskExeArgs;
    }

    private Map<String, String> getHiveToSftpParamToReplace(String reader, String writer, ScheduleJob batchJob, List<ScheduleTaskParamShade> taskParamsToReplace) {
        String readerSourceIdStr = (String) JSONPath.read(reader, "$.parameter.dtCenterSourceId");
        String readSource = getSource(readerSourceIdStr);
        String writerSourceIdStr = (String) JSONPath.read(writer, "$.parameter.dtCenterSourceId");
        String writerSource = getSource(writerSourceIdStr);

        JSONObject readSourceJson = JSONObject.parseObject(readSource);
        JSONObject writerSourceJson = JSONObject.parseObject(writerSource);

        Map<String, String> replaceParams = new HashMap<>();
        String jdbcUrl = getFiled(reader, readSourceJson, "jdbcUrl", "$.parameter.jdbcUrl");
        replaceParams.put("jdbcUrl", jdbcUrl);

        String jdbcPassword = getFiled(reader, readSourceJson, "password", "$.parameter.password");
        if (StringUtils.isNotBlank(jdbcPassword)) {
            replaceParams.put("jdbcPassword", jdbcPassword);
        }

        String jdbcUserName = getFiled(reader, readSourceJson, "username", "$.parameter.username");
        if (StringUtils.isNotBlank(jdbcUserName)) {
            replaceParams.put("jdbcUserName", jdbcUserName);
        }

        boolean removeHeaderLine = (boolean) JSONPath.read(writer, "$.parameter.removeHeaderLine");
        boolean checkFileExists = (boolean) JSONPath.read(writer, "$.parameter.checkFileExists");
        replaceParams.put("skip", removeHeaderLine ? "0" : "1");
        replaceParams.put("checkfile_status", checkFileExists ? "0" : "1");

        //读取sftp

        replaceParams.put("sftpPass", getFiled(writer, writerSourceJson, "password", "$.parameter.password"));
        replaceParams.put("sftpPort", getFiled(writer, writerSourceJson, "port", "$.parameter.port"));
        replaceParams.put("sftpUser", getFiled(writer, writerSourceJson, "username", "$.parameter.username"));
        replaceParams.put("serverIP", getFiled(writer, writerSourceJson, "host", "$.parameter.host"));
        String sftpPath = getFiled(writer, writerSourceJson, "rootDirectory", "$.parameter.rootDirectory");
        replaceParams.put("sftpPath", StringUtils.isBlank(sftpPath) ? "/" : sftpPath);
        //编码
        replaceParams.put("encode_status", this.convertCoding((String) JSONPath.read(writer, "$.parameter.encode.from"), (String) JSONPath.read(writer, "$.parameter.encode.to")));
        JSONArray mappingRels = (JSONArray) JSONPath.read(writer, "$.parameter.mappingRels");
        if (CollectionUtils.isEmpty(mappingRels)) {
            throw new RdosDefineException("映射信息不能为空");
        }

        boolean isZip = "zip".equalsIgnoreCase((String) JSONPath.read(writer, "$.parameter.unCompress"));

        StringBuilder loadPath = new StringBuilder();
        for (int i = 0; i < mappingRels.size(); i++) {
            JSONObject rel = mappingRels.getJSONObject(i);
            //filename|database.tablename|pt='20200602'|1
            String fileName = rel.getString("sourcePath");
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            loadPath.append(fileName)
                    .append("|")
                    .append(rel.getString("targetSchema"))
                    .append(".")
                    .append(rel.getString("targetTable"))
                    .append("|");

            //分区信息
            if (StringUtils.isNotBlank(rel.getString("targetPartition"))) {
                //替换环境变量
                String targetPartition = rel.getString("targetPartition");
                targetPartition = jobParamReplace.paramReplace(targetPartition, taskParamsToReplace, batchJob.getCycTime(),batchJob.getType(), batchJob.getProjectId());
                loadPath.append(targetPartition).append("|");
            }

            loadPath.append(isZip ? "1" : "0");
            //多行用逗号隔开
            if (i != mappingRels.size() - 1) {
                loadPath.append(",");
            }
        }
        replaceParams.put("exportInfo", loadPath.toString());
        //扩展名
        replaceParams.put("extName", (String) JSONPath.read(reader, "$.parameter.extName"));
        replaceParams.put("jobId",batchJob.getJobId());
        return replaceParams;
    }


    private Map<String, String> getSftpToHiveParamToReplace(String reader, String writer, ScheduleJob batchJob, List<ScheduleTaskParamShade> taskParamsToReplace) {
        String readerSourceIdStr = (String) JSONPath.read(reader, "$.parameter.dtCenterSourceId");
        String readSource = getSource(readerSourceIdStr);
        String writerSourceIdStr = (String) JSONPath.read(writer, "$.parameter.dtCenterSourceId");
        String writerSource = getSource(writerSourceIdStr);

        JSONObject readSourceJson = JSONObject.parseObject(readSource);
        JSONObject writerSourceJson = JSONObject.parseObject(writerSource);

        // 映射关系可能有多条
        String jdbcUrl = getFiled(writer,writerSourceJson,"jdbcUrl","$.parameter.jdbcUrl");

        JSONArray mappingRels = (JSONArray) JSONPath.read(writer, "$.parameter.mappingRels");
        if (CollectionUtils.isEmpty(mappingRels)) {
            throw new RdosDefineException("映射信息不能为空");
        }

        boolean removeHeaderLine = (boolean) JSONPath.read(writer, "$.parameter.removeHeaderLine");
        boolean checkFileExists = (boolean) JSONPath.read(writer, "$.parameter.checkFileExists");

        Map<String, String> replaceParams = new HashMap<>();

        replaceParams.put("jdbcUrl", jdbcUrl);
        replaceParams.put("skip", removeHeaderLine ? "0" : "1");
        replaceParams.put("checkfile_status", checkFileExists ? "0" : "1");

        // 数据库用户名密码-n -p
        String jdbcPassword = getFiled(writer,writerSourceJson,"password","$.parameter.password");
        if (StringUtils.isNotBlank(jdbcPassword)) {
            replaceParams.put("jdbcPassword", "-p " + jdbcPassword);
        }
        String jdbcUserName = getFiled(writer,writerSourceJson,"username","$.parameter.username");
        if (StringUtils.isNotBlank(jdbcUserName)) {
            replaceParams.put("jdbcUserName", "-n " + jdbcUserName);
        }

        boolean isZip = "zip".equalsIgnoreCase((String) JSONPath.read(writer, "$.parameter.unCompress"));

        StringBuilder loadPath = new StringBuilder();
        for (int i = 0; i < mappingRels.size(); i++) {
            JSONObject rel = mappingRels.getJSONObject(i);
            //支持这两种格式 /root/test/test.txt|test.import2|partition|0 和 /root/test/test.txt|test.import2|0
            loadPath.append(rel.getString("sourcePath"))
                    .append("|")
                    .append(rel.getString("targetSchema"))
                    .append(".")
                    .append(rel.getString("targetTable"))
                    .append("|");

            //分区信息
            if (StringUtils.isNotBlank(rel.getString("targetPartition"))) {
                //替换环境变量
                String targetPartition = rel.getString("targetPartition");
                loadPath.append(targetPartition).append("|");
                targetPartition = jobParamReplace.paramReplace(targetPartition, taskParamsToReplace, batchJob.getCycTime(),batchJob.getType(), batchJob.getProjectId());
                Map<String, String> formattedMap = this.formatPartitionReturnMap(targetPartition);
                String join = Joiner.on("',").withKeyValueSeparator("='").join(formattedMap);
                targetPartition = join + "'";
                this.createPartitionWithRetry(DataSourceType.HIVE.getVal(), jdbcUserName, jdbcPassword, jdbcUrl, rel.getString("targetTable"), targetPartition, batchJob.getDtuicTenantId());
            }

            loadPath.append("overwrite".equalsIgnoreCase(rel.getString("writeMode")) ? 1 : 0);
            loadPath.append("|").append(isZip ? "1" : "0");
            //多行用逗号隔开
            if (i != mappingRels.size() - 1) {
                loadPath.append(",");
            }
        }

        //读取sftp


        replaceParams.put("sftpPass", getFiled(reader,readSourceJson,"password","$.parameter.password"));
        replaceParams.put("sftpPort", getFiled(reader,readSourceJson,"port","$.parameter.port"));
        replaceParams.put("sftpUser", getFiled(reader,readSourceJson,"username","$.parameter.username"));
        replaceParams.put("serverIP", getFiled(reader,readSourceJson,"host","$.parameter.host"));

        replaceParams.put("remotePath", loadPath.toString());

        //扩展名
        replaceParams.put("extName", (String) JSONPath.read(reader, "$.parameter.extName"));
        //编码
        replaceParams.put("encode_status", this.convertCoding((String) JSONPath.read(writer, "$.parameter.encode.from"), (String) JSONPath.read(writer, "$.parameter.encode.to")));
        replaceParams.put("jobId",batchJob.getJobId());
        return replaceParams;
    }

    private String getFiled(String writer,JSONObject writerSourceJson, String key, String defaultKet) {
        if (writerSourceJson != null) {
            String value = writerSourceJson.getString(key);

            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }

        Object read = JSONPath.read(writer, defaultKet);

        if (read != null) {
            return String.valueOf(read);
        }

        return "";
    }

    private String getSource(String sourceIdStr) {
        if (StringUtils.isNotBlank(sourceIdStr)) {
            ApiResponse<DsServiceInfoDTO> readSourceDs = dataSourceAPIClient.getDsInfoById(Long.parseLong(sourceIdStr));

            DsServiceInfoDTO data = readSourceDs.getData();
            if (null != data && StringUtils.isNotBlank(data.getDataJson())) {
                return data.getDataJson();
            }
        }

        return "";
    }

    private void createPartitionWithRetry(Integer sourceType, String username, String password, String jdbcUrl, String table, String partition, Long dtUicTenantId) {
        String sql = String.format("alter table %s add if not exists partition (%s)", table, partition);
        try {
            RetryUtil.executeWithRetry(() -> {
                LOG.info("create partition {}", sql);
                JSONObject pluginInfo = new JSONObject();
                pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, DataBaseType.getHiveTypeName(DataSourceType.getSourceType(sourceType)));
                // selfConf
                JSONObject selfConf = new JSONObject();
                selfConf.put(ConfigConstant.JDBCURL,jdbcUrl);
                selfConf.put(ConfigConstant.USERNAME, username);
                selfConf.put(ConfigConstant.PASSWORD, password);
                pluginInfo.put(PluginInfoConst.SELF_CONF, selfConf);
                pluginInfo.put(ConfigConstant.DATASOURCE_TYPE,sourceType);
                IClient client = ClientCache.getClient(sourceType);
                ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtUicTenantId);
                client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(sql).build());
                return null;
            }, environmentContext.getRetryFrequency(), environmentContext.getRetryInterval(), false, null);
        } catch (Exception e) {
            LOG.error("create partition error:", e);
            throw new RdosDefineException("create partition error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * 根据文件名称获取 shell 脚本内容, 优先读取部署路径下 shellPath 文件夹下的脚本, 如果没有则写出一份到该文件夹下
     *
     * @param fileName shell 脚本名称
     * @return shell 脚本名称
     * @throws Exception 异常信息
     */
    public String getFileShellByName(String fileName) throws Exception {
        // 脚本不存时到的错误信息
        Supplier<String> errMsgSupplier = () -> String.format("shell 脚本: [%s] 不存在", fileName);
        if (StringUtils.isBlank(fileName)) {
            throw new RdosDefineException("文件名称不能为空");
        }
        String shellPath = System.getProperty("user.dir") + File.separator + "shellPath";
        FileUtil.mkdirsIfNotExist(shellPath);

        File localShellFile = new File(shellPath + File.separator + fileName);
        if (!localShellFile.exists()) {
            // sync copy
            synchronized (fileName.intern()) {
                if (!localShellFile.exists()) {
                    URL shellResourceUrl = this.getClass().getResource(File.separator + "shellPath" + File.separator + fileName);
                    if (Objects.isNull(shellResourceUrl)) {
                        throw new RdosDefineException(errMsgSupplier.get());
                    }
                    // 写出一份到本地
                    FileUtils.copyURLToFile(shellResourceUrl, localShellFile);
                }
            }
        }
        if (!localShellFile.exists()) {
            throw new RdosDefineException(errMsgSupplier.get());
        }
        String fileCopyShell = FileUtils.readFileToString(localShellFile);
        if (StringUtils.isBlank(fileCopyShell)) {
            throw new RdosDefineException(errMsgSupplier.get());
        }
        return fileCopyShell;
    }


    private String convertCoding(String from, String to) {
        if ("gbk".equalsIgnoreCase(from) && "utf-8".equalsIgnoreCase(to)) {
            return "0";
        }
        if ("gbk".equalsIgnoreCase(to) && "utf-8".equalsIgnoreCase(from)) {
            return "1";
        }
        return "-1";
    }


    private Map<String, String> formatPartitionReturnMap(String partition) {
        Map<String, String> split = new HashMap<>();
        if (StringUtils.countMatches(partition, "=") == 1) {
            //pt=2020/04 分区中带/
            String[] splits = partition.split("=");
            split.put(splits[0], splits[1]);
        } else {
            //pt='asdfasd'/ds='1231231' 2级分区
            split = Splitter.on("/").withKeyValueSeparator("=").split(partition);
        }
        Map<String, String> formattedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : split.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (value.startsWith("'") || value.startsWith("\"")) {
                value = value.substring(1);
            }
            if (value.endsWith("'") || value.endsWith("\"")) {
                value = value.substring(0, value.length() - 1);
            }
            formattedMap.put(key, value);
        }
        return formattedMap;
    }
}
