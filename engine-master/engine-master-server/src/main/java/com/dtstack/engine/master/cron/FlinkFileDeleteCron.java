package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.FileFindDTO;
import com.dtstack.dtcenter.loader.dto.FileFindResultDTO;
import com.dtstack.dtcenter.loader.dto.FileStatus;
import com.dtstack.dtcenter.loader.dto.HDFSContentSummary;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.CheckpointType;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.Pair;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.StreamTaskService;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.po.ScheduleJobHistory;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2023/11/9
 */
@Component
public class FlinkFileDeleteCron {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkFileDeleteCron.class);


    @Autowired
    private ScheduleJobHistoryService scheduleJobHistoryService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private StreamTaskService streamTaskService;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Value("${flink.jar.file.path:/user/{username}/.flink}")
    private String flinkJarFilePath;

    HashBasedTable<Long, String, String> cpPathCache = HashBasedTable.create();
    HashBasedTable<Long, String, String> spPathCache = HashBasedTable.create();
    HashBasedTable<Long, String, String> completedLogsCache = HashBasedTable.create();
    HashBasedTable<Long, String, String> haStorageDirPathCache = HashBasedTable.create();
    Map<String, String> versionValCache = new HashMap<>();
    Set<String> canClearJobIds = new HashSet<>();
    Set<String> hadDeletedJobs = new HashSet<>();
    Map<Long, List<String>> componentVersion = new HashMap<>();
    Map<String, Long> jobTenantIdMap = new HashMap<>();
    Map<Long, String> hadoopUserNameMap = new HashMap<>();
    Map<Long, Pair<IHdfsFile, ISourceDTO>> clientCache = new HashMap<>();

    @EngineCron
    @Scheduled(cron = "${flink.file.delete.cron:0 0 2 * * ? } ")
    public void deleteFile() {
        LOGGER.info("start to delete flink file,save day is {}", environmentContext.getFlinkFileSaveDays());
        /**
         * 1. 删除是直接全部清理任务相关的所有文件信息；
         * 2. 下线、正常多次启停、取消、失败等导致Flink上生成了新的任务情况，只保留最近3个月生成
         */
        try {
            hadDeletedJobs = new HashSet<>(scheduleJobService.queryDeletedJobs(AppType.STREAM.getType()));
            deleteExpireJobHistory();
            if (!CollectionUtils.isEmpty(hadDeletedJobs)) {
                LOGGER.info("start to delete flink file which job is deleted {}",hadDeletedJobs);
                deleteJobFileWhenJobHadDeleted(hadDeletedJobs);
            }
            LOGGER.info("end to delete flink file");
        } finally {
            clearAll();
        }
    }

    private void deleteExpireJobHistory() {
        AtomicLong startId = new AtomicLong(0L);
        DateTime expireDateTime = DateTime.now().plusDays(-Math.abs(environmentContext.getFlinkFileSaveDays()));
        List<ScheduleJobHistory> jobHistories = scheduleJobHistoryService.listJobHistoriesByStartId(startId.get(), 100, new Timestamp(expireDateTime.getMillis()));
        while (CollectionUtils.isNotEmpty(jobHistories)) {
            jobHistories.stream()
                    .max(Comparator.comparing(ScheduleJobHistory::getId))
                    .ifPresent(j -> {
                        if (j.getId() > startId.get()) {
                            startId.set(j.getId());
                        }
                    });
            Map<String, List<ScheduleJobHistory>> jobIdHistoryMaps = jobHistories
                    .stream()
                    .collect(Collectors.groupingBy(ScheduleJobHistory::getJobId, Collectors.toList()));
            for (String jobId : jobIdHistoryMaps.keySet()) {

                List<ScheduleJobHistory> scheduleJobHistories = jobIdHistoryMaps.get(jobId);
                try {
                    if (hadDeletedJobs.contains(jobId)) {
                        continue;
                    }
                    /*
                      saveDay内 产生过一次cp 那saveDay之前的数据可以清理
                     */
                    boolean canClear = canClearJobIds.contains(jobId);
                    if (!canClear) {
                        //job 表为最新一条提交记录
                        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, null);
                        if (null == scheduleJob) {
                            continue;
                        }
                        if (StringUtils.isNotBlank(scheduleJob.getEngineJobId()) && checkNewestJobFileInHdfs(scheduleJob)) {
                            //最新提交生成了ck 可以删除
                            canClearJobIds.add(jobId);
                            canClear = true;
                            scheduleJobHistories = scheduleJobHistories.stream()
                                    .filter(a -> !a.getEngineJobId().equals(scheduleJob.getEngineJobId()))
                                    .collect(Collectors.toList());
                        } else {
                            //最新提交失败 递归寻找ck
                            Collections.reverse(scheduleJobHistories);
                            Iterator<ScheduleJobHistory> iterator = scheduleJobHistories.iterator();
                            while (iterator.hasNext()) {
                                ScheduleJobHistory timeBackJobHistory = iterator.next();
                                scheduleJob.setEngineJobId(timeBackJobHistory.getEngineJobId());
                                scheduleJob.setApplicationId(timeBackJobHistory.getApplicationId());
                                if (checkNewestJobFileInHdfs(scheduleJob)) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    if (canClear) {
                        deleteJobFile(jobTenantIdMap.get(jobId), scheduleJobHistories);
                    }
                } catch (Exception e) {
                    LOGGER.error("check can delete {} flink file error ", jobId, e);
                }
            }
            jobHistories = scheduleJobHistoryService.listJobHistoriesByStartId(startId.get(), 100, new Timestamp(expireDateTime.getMillis()));
            LOGGER.info("start to delete flink file startId is  {}", startId.get());
        }
    }

    private void deleteJobFileWhenJobHadDeleted(Set<String> deleteJobIds) {
        for (String deleteJobId : deleteJobIds) {
            ScheduleJob deleteJob = scheduleJobService.getByJobId(deleteJobId, null);
            if (deleteJob == null) {
                LOGGER.info("delete job {} is empty so break", deleteJobId);
                continue;
            }
            /*
                  已删除任务直接清理 只删除 不考虑时间范围
                 */
            int pageSize = 100;
            int currentPage = 1;
            while (true) {
                PageResult<List<ScheduleJobHistory>> jobPage = scheduleJobHistoryService.pageByJobId(deleteJobId, pageSize, currentPage);
                if (CollectionUtils.isEmpty(jobPage.getData())) {
                    break;
                }
                List<ScheduleJobHistory> deleteJobHistory = jobPage.getData();
                if (!cpPathCache.containsRow(deleteJob.getDtuicTenantId())) {
                    //reload cache
                    checkNewestJobFileInHdfs(deleteJob);
                }
                deleteJobFile(deleteJob.getDtuicTenantId(), deleteJobHistory);
                hadDeletedJobs.add(deleteJobId);
                currentPage++;
            }

        }
    }

    private void clearAll() {
        canClearJobIds.clear();
        cpPathCache.clear();
        spPathCache.clear();
        completedLogsCache.clear();
        haStorageDirPathCache.clear();
        componentVersion.clear();
        clientCache.clear();
        versionValCache.clear();
        hadoopUserNameMap.clear();
    }

    /**
     * 任务的信息清理，包括以下内容：
     * checkpoint
     * savepoint
     * high-availability.storageDir
     * /user/username/.flink
     * jobmanager.archive.fs.dir
     *
     * @param scheduleJobHistories
     */
    private void deleteJobFile(Long tenantId, List<ScheduleJobHistory> scheduleJobHistories) {
        if (CollectionUtils.isEmpty(scheduleJobHistories)) {
            return;
        }
        List<Long> historyIds = new ArrayList<>(scheduleJobHistories.size());
        for (ScheduleJobHistory jobHistory : scheduleJobHistories) {
            historyIds.add(jobHistory.getId());
            List<String> version = new ArrayList<>();
            if (StringUtils.isBlank(jobHistory.getVersionName())) {
                //历史任务  不确定版本 112 和 116 都得处理
                version = componentVersion.get(tenantId);
            } else {
                String versionVal = versionValCache.computeIfAbsent(jobHistory.getVersionName(),
                        (v) -> scheduleDictService.convertVersionNameToValue(v, EngineType.Flink.name()));
                version.add(versionVal);
            }

            for (String v : version) {
                Pair<IHdfsFile, ISourceDTO> hdfsFileISourceDTOPair = clientCache.get(tenantId);
                IHdfsFile client = hdfsFileISourceDTOPair.getKey();
                ISourceDTO sourceDTO = hdfsFileISourceDTOPair.getValue();
                /**
                 * cp -> /dtInsight/flink112/checkpoints/352a1142a7fc727ee2033ec839f0395b
                 * sp -> /dtInsight/flink112/savepoints/savepoint-:shortjobid-:savepointid/
                 * completeLog -> /dtInsight/flink112/completed-jobs/352a1142a7fc727ee2033ec839f0395b
                 * ha -> /dtInsight/flink112/ha/application_1697677280384_6143
                 * .flink -> /user/admin/.flink/application_1697677280384_6143
                 */
                List<String> deletePaths = new ArrayList<>(5);
                if (StringUtils.isNotBlank(jobHistory.getEngineJobId())) {
                    deletePaths.add(cpPathCache.get(tenantId, v) + ConfigConstant.SP + jobHistory.getEngineJobId());
                    String spPath = null;
                    try {
                        spPath = findSpPath(tenantId, jobHistory, v);
                    } catch (Exception e) {
                        LOGGER.error("get job {} engineJobId {} sp path error", jobHistory.getJobId(), jobHistory.getEngineJobId(), e);
                    }
                    if (StringUtils.isNotBlank(spPath)) {
                        deletePaths.add(spPath);
                    }
                    deletePaths.add(completedLogsCache.get(tenantId, v) + ConfigConstant.SP + jobHistory.getEngineJobId());
                }

                if (StringUtils.isNotBlank(jobHistory.getApplicationId())) {
                    deletePaths.add(haStorageDirPathCache.get(tenantId, v) + ConfigConstant.SP + jobHistory.getApplicationId());
                    deletePaths.add(flinkJarFilePath.replace("{username}", hadoopUserNameMap.getOrDefault(tenantId, environmentContext.getHadoopUserName()))
                            + ConfigConstant.SP + jobHistory.getApplicationId());
                }

                for (String path : deletePaths) {
                    if (StringUtils.isEmpty(path)) {
                        continue;
                    }
                    boolean delete = client.delete(sourceDTO, path, true);
                    LOGGER.info("remove jobId {} hdfs file path {} result {}", jobHistory.getJobId(), path, delete);
                }
            }
        }
        scheduleJobHistoryService.deleteByJobIds(historyIds);
    }

    public String findSpPath(Long tenantId, ScheduleJobHistory jobHistory, String version) {
        if (StringUtils.isEmpty(jobHistory.getEngineJobId())) {
            return null;
        }
        /*
          savepoint-shortjobid-savepointid
          savepoint-fc8f26-0692bea22eff
         */

        String regexPath = streamTaskService.getCheckpointDir(spPathCache.get(tenantId, version), jobHistory.getEngineJobId(), CheckpointType.SAVEPOINT);

        FileFindDTO findDTO = FileFindDTO.builder()
                .isPathPattern(true)
                .uniqueKey(jobHistory.getEngineJobId())
                .path(regexPath)
                .build();


        List<FileFindResultDTO> fileFindResultDTOS = clientCache.get(tenantId).getKey().listFiles(
                clientCache.get(tenantId).getValue(), Lists.newArrayList(findDTO));
        if (CollectionUtils.isEmpty(fileFindResultDTOS)) {
            return null;
        }
        if (fileFindResultDTOS.size() == 1) {
            if (CollectionUtils.isEmpty(fileFindResultDTOS.get(0).getFileStatusList())) {
                // path is empty
                return null;
            }
            return fileFindResultDTOS.get(0).getFileStatusList().get(0).getPath();
        }
        // regex path is more then one ,find path in complete-log
        String logPath = completedLogsCache.get(tenantId, version) + ConfigConstant.SP + jobHistory.getEngineJobId();
        String logJson = clientCache.get(tenantId).getKey().getHdfsWithScript(
                clientCache.get(tenantId).getValue(), logPath);
        return readSpPathFromLogJSON(logJson);
    }

    public String readSpPathFromLogJSON(String logJson) {
        JSONObject jsonObject = JSONObject.parseObject(logJson);
        JSONArray archive = jsonObject.getJSONArray("archive");
        if (CollectionUtils.isEmpty(archive)) {
            return null;
        }
        for (int i = 0; i < archive.size(); i++) {
            JSONObject ckJSON = archive.getJSONObject(i).getJSONObject("json");
            if (null == ckJSON) {
                continue;
            }
            JSONArray history = ckJSON.getJSONArray("history");
            if (CollectionUtils.isEmpty(history)) {
                continue;
            }
            for (int j = 0; j < history.size(); j++) {
                JSONObject historyJson = history.getJSONObject(j);
                if ("SAVEPOINT".equalsIgnoreCase(historyJson.getString("checkpoint_type"))) {
                    return historyJson.getString(ConfigConstant.CHECKPOINT_SAVEPATH_KEY);
                }
            }
        }
        return null;
    }

    public void getHdfsFilePath(Long tenantId) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);
        List<com.dtstack.engine.api.domain.Component> componentList = componentService.listByClusterId(clusterId, EComponentType.FLINK.getTypeCode(), false);
        List<String> versions = new ArrayList<>();
        for (com.dtstack.engine.api.domain.Component component : componentList) {
            Map<Integer, String> componentVersionMap = new HashMap<>();
            componentVersionMap.put(EComponentType.FLINK.getTypeCode(), component.getHadoopVersion());
            // flink 组件配置
            String flinkConf = clusterService.getConfigByKey(tenantId, EComponentType.FLINK.getConfName(), false,
                    componentVersionMap, false);
            JSONObject perJobJson = JSONObject.parseObject(flinkConf).getJSONObject(EDeployMode.PERJOB.getMode());
            String checkpointsDir = perJobJson.getString(ConfigConstant.CHECK_POINTS_DIR);
            String savePointsDir = perJobJson.getString(ConfigConstant.SAVE_POINTS_DIR);
            String completeDir = perJobJson.getString(ConfigConstant.COMPLETE_LOGS_DIR);
            String haDir = perJobJson.getString(ConfigConstant.HA_STORAGE_DIR);
            cpPathCache.put(tenantId, component.getHadoopVersion(), checkpointsDir);
            spPathCache.put(tenantId, component.getHadoopVersion(), savePointsDir);
            completedLogsCache.put(tenantId, component.getHadoopVersion(), completeDir);
            haStorageDirPathCache.put(tenantId, component.getHadoopVersion(), haDir);
            if (perJobJson.containsKey(PluginInfoConst.HADOOP_USER_NAME_DOT)) {
                hadoopUserNameMap.put(tenantId, perJobJson.getString(PluginInfoConst.HADOOP_USER_NAME_DOT));
            }
            if (perJobJson.containsKey(PluginInfoConst.HADOOP_USER_NAME)) {
                hadoopUserNameMap.put(tenantId, perJobJson.getString(PluginInfoConst.HADOOP_USER_NAME));
            }
            versions.add(component.getHadoopVersion());
        }
        componentVersion.put(tenantId, versions);

    }

    private boolean checkNewestJobFileInHdfs(ScheduleJob scheduleJob) {
        if (null == scheduleJob) {
            return false;
        }
        jobTenantIdMap.putIfAbsent(scheduleJob.getJobId(), scheduleJob.getDtuicTenantId());
        if (!cpPathCache.containsRow(scheduleJob.getDtuicTenantId())) {
            getHdfsFilePath(scheduleJob.getDtuicTenantId());
        }
        //hdfs校验
        JSONObject hdfsPluginInfo = pluginInfoManager.buildTaskPluginInfo(
                scheduleJob.getProjectId(), scheduleJob.getAppType(),
                scheduleJob.getTaskType(), scheduleJob.getDtuicTenantId(),
                ScheduleEngineType.Hadoop.getEngineName(), scheduleJob.getCreateUserId(),
                null, null, null);
        for (String version : componentVersion.get(scheduleJob.getDtuicTenantId())) {
            String checkpointDir = streamTaskService.getCheckpointDir(cpPathCache.get(scheduleJob.getDtuicTenantId(), version), scheduleJob.getEngineJobId(), CheckpointType.CHECKPOINT);
            FileFindDTO findDTO = FileFindDTO.builder()
                    .isPathPattern(false)
                    .uniqueKey(scheduleJob.getApplicationId())
                    .path(checkpointDir)
                    .build();
            IHdfsFile hdfsClient = streamTaskService.fillPluginInfoAndGetHdfsClient(hdfsPluginInfo, scheduleJob.getDtuicTenantId());
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(hdfsPluginInfo.toJSONString(), scheduleJob.getDtuicTenantId());
            clientCache.putIfAbsent(scheduleJob.getDtuicTenantId(), new Pair<>(hdfsClient, sourceDTO));
            List<FileFindResultDTO> fileFindResultDTOS = hdfsClient.listFiles(sourceDTO, Lists.newArrayList(findDTO));
            if (CollectionUtils.isEmpty(fileFindResultDTOS)) {
                continue;
            }
            for (FileFindResultDTO fileFindResultDTO : fileFindResultDTOS) {
                List<FileStatus> fileStatusList = fileFindResultDTO.getFileStatusList()
                        .stream()
                        .filter(fileStatus -> fileStatus.getPath().contains("ck"))
                        .collect(Collectors.toList());
                for (FileStatus fileStatus : fileStatusList) {
                    //最新的ck 得确认是否生成完成
                    List<HDFSContentSummary> contentSummary = hdfsClient.getContentSummary(sourceDTO, Lists.newArrayList(fileStatus.getPath()));
                    if (CollectionUtils.isNotEmpty(contentSummary) && contentSummary.get(0).getSpaceConsumed() > 0) {
                        LOGGER.info("jobId {} appId {} engineJobId {} path {} had create cp {}", scheduleJob.getJobId(), scheduleJob.getApplicationId(),
                                scheduleJob.getEngineJobId(), fileStatus.getPath(), fileStatus.getModification_time());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}