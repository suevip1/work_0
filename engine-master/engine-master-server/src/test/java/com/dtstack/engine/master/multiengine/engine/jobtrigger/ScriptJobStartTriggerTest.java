package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.FileFindDTO;
import com.dtstack.dtcenter.loader.dto.FileFindResultDTO;
import com.dtstack.dtcenter.loader.dto.FileStatus;
import com.dtstack.dtcenter.loader.dto.HDFSContentSummary;
import com.dtstack.dtcenter.loader.dto.HdfsQueryDTO;
import com.dtstack.dtcenter.loader.dto.HdfsWriterDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.dtcenter.loader.enums.FileFormat;
import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleJobResourceFileDao;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.ScheduleJobResourceFile;
import com.dtstack.rpc.annotation.RpcNodeSign;
import com.dtstack.rpc.download.IDownloader;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptJobStartTriggerTest {
    ScriptJobStartTrigger scriptJobStartTrigger = new ScriptJobStartTrigger();

    @Test
    public void testDtScriptHadoopJobTrigger() {
        try {
            ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
            scheduleTaskShade.setTaskId(0L);
            scheduleTaskShade.setExtraInfo("{}");
            scheduleTaskShade.setTenantId(1L);
            scheduleTaskShade.setProjectId(1L);
            scheduleTaskShade.setNodePid(1L);
            scheduleTaskShade.setName("testJob");
            scheduleTaskShade.setTaskType(1);
            scheduleTaskShade.setEngineType(2);
            scheduleTaskShade.setComputeType(1);
            scheduleTaskShade.setSqlText("select");
            scheduleTaskShade.setTaskParams("null");
            scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
            scheduleTaskShade.setPeriodType(1);
            scheduleTaskShade.setScheduleStatus(1);
            scheduleTaskShade.setSubmitStatus(1);
            scheduleTaskShade.setGmtCreate(new Timestamp(1592559742000L));
            scheduleTaskShade.setGmtModified(new Timestamp(1592559742000L));
            scheduleTaskShade.setModifyUserId(1L);
            scheduleTaskShade.setCreateUserId(1L);
            scheduleTaskShade.setOwnerUserId(1L);
            scheduleTaskShade.setVersionId(1);
            scheduleTaskShade.setTaskDesc("null");
            scheduleTaskShade.setAppType(1);
            scheduleTaskShade.setIsDeleted(0);
            scheduleTaskShade.setMainClass("DataCollection");
            scheduleTaskShade.setExeArgs("null");
            scheduleTaskShade.setFlowId(0L);
            scheduleTaskShade.setDtuicTenantId(1L);
            scheduleTaskShade.setProjectScheduleStatus(0);
            scheduleTaskShade.setAppType(AppType.RDOS.getType());
            scheduleTaskShade.setEngineType(1);
            scheduleTaskShade.setTaskType(0);
            scheduleTaskShade.setComputeType(1);
            scheduleTaskShade.setTaskId(1487L);
            scheduleTaskShade.setAppType(AppType.RDOS.getType());
            scheduleTaskShade.setEngineType(6);
            scheduleTaskShade.setTaskType(EScheduleJobType.SHELL.getType());
            scheduleTaskShade.setComputeType(1);
            scheduleTaskShade.setTaskId(1487L);
            scheduleTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name s6\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2020-11-24 14:38:05\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name s6\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"worker.memory=512m\\\\nworker.cores=1\\\\nexclusive=false\\\\nworker.num=1\\\\njob.priority=10\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"s6\\\",\\\"tenantId\\\":1,\\\"taskId\\\":919}\"}");
            ScheduleJob job = new ScheduleJob();
            job.setJobId("4hsohrrlo7n0");
            JSONObject jsonObject = JSONObject.parseObject(scheduleTaskShade.getExtraInfo());
            JSONObject info = jsonObject.getJSONObject("info");
            Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
            scriptJobStartTrigger.readyForTaskStartTrigger(actionParam, scheduleTaskShade, job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Mock extends BaseMock {

        @MockInvoke(targetClass = JobChainParamHandler.class)
        public JobChainParamHandleResult handle(String sql, ScheduleTaskShade taskShade, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
            JobChainParamHandleResult result = new JobChainParamHandleResult();
            result.setSql(sql);
            result.setTaskParams(taskShade.getTaskParams());
            return result;
        }

        @MockInvoke(targetClass = JobParamReplace.class)
        public String paramReplace(String sql,
                                   List<ScheduleTaskParamShade> paramList,
                                   String cycTime,
                                   Integer scheduleType) {
            return sql;
        }

        @MockInvoke(
                targetClass = ScheduleTaskRefShadeService.class,
                targetMethod = "isSupportRefJob"
        )
        public boolean isSupportRefJob(Integer taskType) {
            return true;
        }

        @MockInvoke(
                targetClass = ScheduleTaskRefShadeService.class,
                targetMethod = "getJobRefResourceFiles"
        )
        public List<JobResourceFile> getJobRefResourceFiles(Map<String, Object> actionParam,
                                                            ScheduleJob scheduleJob) {
            return new ArrayList<>();
        }

        @MockInvoke(
                targetClass = ScheduleTaskRefShadeService.class,
                targetMethod = "replaceRefFileContent"
        )
        public String replaceRefFileContent(String sqlText, List<JobResourceFile> jobResourceFiles) {
            return sqlText;
        }

        @MockInvoke(
                targetClass = ScheduleTaskRefShadeService.class,
                targetMethod = "processTaskParamForShipFiles"
        )
        public String processTaskParamForShipFiles(String taskParams,
                                                   List<JobResourceFile> jobRefResourceFiles) {
            return taskParams;
        }

        @MockInvoke(
                targetClass = PluginInfoManager.class,
                targetMethod = "buildTaskPluginInfo"
        )
        public JSONObject buildTaskPluginInfo(Long projectId, Integer appType, Integer taskType,
                                              Long dtUicTenantId, String engineTypeStr, Long dtUicUserId, Integer deployMode,
                                              Long resourceId, Map<Integer, String> componentVersionMap) {
            return new JSONObject();
        }

        @MockInvoke(
                targetClass = ComponentService.class,
                targetMethod = "buildHdfsTypeName"
        )
        public String buildHdfsTypeName(Long dtUicTenantId, Long clusterId) {
            return "";
        }

        @MockInvoke(
                targetClass = RdosWrapper.class,
                targetMethod = "getDataSourceCodeByDiceName"
        )
        public Integer getDataSourceCodeByDiceName(String typeName, EComponentType componentType) {
            return 1;
        }

        @MockInvoke(
                targetClass = ClientCache.class,
                targetMethod = "getHdfs"
        )
        public static IHdfsFile getHdfs(Integer dataSourceType) {
            return new IHdfsFile() {
                @Override
                public List<String> getContainerIdByFlinkJob(ISourceDTO source, String flinkJobId, String flinkJobManagerArchiveFsDir) {
                    return null;
                }

                @Override
                public boolean checkAndDeleteBySuperUser(ISourceDTO source, String remotePath) {
                    return false;
                }

                @Override
                public FileStatus getStatus(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public IDownloader getLogDownloader(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
                    return null;
                }

                @Override
                public List<String> getTaskManagerList(ISourceDTO iSourceDTO, String s, String s1) {
                    return null;
                }

                @Override
                public IDownloader getFileDownloader(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public boolean downloadFileFromHdfs(ISourceDTO iSourceDTO, String s, String s1) {
                    return false;
                }

                @Override
                public boolean uploadLocalFileToHdfs(ISourceDTO iSourceDTO, String s, String s1) {
                    return false;
                }

                @Override
                public boolean uploadInputStreamToHdfs(ISourceDTO iSourceDTO, byte[] bytes, String s) {
                    return false;
                }

                @Override
                public String uploadStringToHdfs(ISourceDTO iSourceDTO, String s, String s1) {
                    return "/dtscript/ns1";
                }

                @Override
                public boolean createDir(ISourceDTO iSourceDTO, String s, Short aShort) {
                    return false;
                }

                @Override
                public boolean isFileExist(ISourceDTO iSourceDTO, String s) {
                    return false;
                }

                @Override
                public boolean checkAndDelete(ISourceDTO iSourceDTO, String s) {
                    return false;
                }

                @Override
                public boolean delete(ISourceDTO iSourceDTO, String s, boolean b) {
                    return false;
                }

                @Override
                public long getDirSize(ISourceDTO iSourceDTO, String s) {
                    return 0;
                }

                @Override
                public boolean deleteFiles(ISourceDTO iSourceDTO, List<String> list) {
                    return false;
                }

                @Override
                public boolean isDirExist(ISourceDTO iSourceDTO, String s) {
                    return false;
                }

                @Override
                public boolean setPermission(ISourceDTO iSourceDTO, String s, String s1) {
                    return false;
                }

                @Override
                public boolean rename(ISourceDTO iSourceDTO, String s, String s1) {
                    return false;
                }

                @Override
                public boolean copyFile(ISourceDTO iSourceDTO, String s, String s1, boolean b) {
                    return false;
                }

                @Override
                public boolean copyDirector(ISourceDTO iSourceDTO, String s, String s1) {
                    return false;
                }

                @Override
                public boolean fileMerge(ISourceDTO iSourceDTO, String s, String s1, FileFormat fileFormat, Long aLong, Long aLong1) {
                    return false;
                }

                @Override
                public List<FileStatus> listStatus(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public List<String> listAllFilePath(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public List<FileStatus> listAllFiles(ISourceDTO iSourceDTO, String s, boolean b) {
                    return null;
                }

                @Override
                public Long listFileSize(ISourceDTO source, String remotePath, boolean isIterate) {
                    return null;
                }

                @Override
                public boolean copyToLocal(ISourceDTO iSourceDTO, String s, String s1) {
                    return false;
                }

                @Override
                public boolean copyFromLocal(ISourceDTO iSourceDTO, String s, String s1, boolean b) {
                    return false;
                }

                @Override
                public IDownloader getDownloaderByFormat(ISourceDTO iSourceDTO, String s, List<String> list, String s1, String s2) {
                    return null;
                }

                @Override
                public IDownloader getDownloaderByFormatWithType(ISourceDTO iSourceDTO, String s, List<ColumnMetaDTO> list, List<String> list1, Map<String, String> map, List<String> list2, String s1, String s2) {
                    return null;
                }

                @Override
                public IDownloader getDownloaderByFormatWithType(ISourceDTO iSourceDTO, String s, List<ColumnMetaDTO> list, List<String> list1, Map<String, String> map, List<String> list2, String s1, String s2, Boolean aBoolean) {
                    return null;
                }

                @Override
                public List<ColumnMetaDTO> getColumnList(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO, String s) {
                    return null;
                }

                @Override
                public int writeByPos(ISourceDTO iSourceDTO, HdfsWriterDTO hdfsWriterDTO) {
                    return 0;
                }

                @Override
                public int writeByName(ISourceDTO iSourceDTO, HdfsWriterDTO hdfsWriterDTO) {
                    return 0;
                }

                @Override
                public int getCsvUploadProgress(String tableName) {
                    return 0;
                }

                @Override
                public List<HDFSContentSummary> getContentSummary(ISourceDTO iSourceDTO, List<String> list) {
                    return null;
                }

                @Override
                public HDFSContentSummary getContentSummary(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public String getHdfsWithScript(ISourceDTO iSourceDTO, String s) {
                    return null;
                }

                @Override
                public List<String> getHdfsWithJob(ISourceDTO iSourceDTO, HdfsQueryDTO hdfsQueryDTO) {
                    return null;
                }

                @Override
                public List<FileFindResultDTO> listFiles(ISourceDTO iSourceDTO, List<FileFindDTO> list) {
                    return null;
                }
            };
        }

        @MockInvoke(
                targetClass = PluginInfoToSourceDTO.class,
                targetMethod = "getSourceDTO"
        )
        public static ISourceDTO getSourceDTO(String data, Long dtUicTenantId) {
            return new ISourceDTO() {
                @Override
                public String getUsername() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public Integer getSourceType() {
                    return null;
                }

                @Override
                public Long getTenantId() {
                    return null;
                }

                @Override
                public void setTenantId(Long aLong) {

                }
            };
        }


        @MockInvoke(
                targetClass = ScheduleTaskRefShadeService.class,
                targetMethod = "getResourceFilesFromTaskShade"
        )
        public List<JobResourceFile> getResourceFilesFromTaskShade(String taskExeArgs,
                                                                   ScheduleTaskShade taskShade) {
            JobResourceFile jobResourceFile = new JobResourceFile();

            return Lists.newArrayList(jobResourceFile);
        }

        @MockInvoke(
                targetClass = ScheduleJobResourceFileDao.class,
                targetMethod = "deleteByJobIdAndType"
        )
        public void deleteByJobIdAndType(String jobId, Integer type) {
        }

        @MockInvoke(
                targetClass = ScheduleJobResourceFileDao.class,
                targetMethod = "insert"
        )
        public Integer insert(ScheduleJobResourceFile scheduleJobResourceFile) {
            return null;
        }
    }
}
