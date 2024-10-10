package com.dtstack.engine.common.client;

import com.dtstack.engine.common.*;
import com.dtstack.engine.common.callback.CallBack;
import com.dtstack.engine.common.callback.ClassLoaderCallBackMethod;
import com.dtstack.engine.common.client.bean.*;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 代理IClient实现类的proxy
 * Date: 2017/12/19
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ClientProxy implements IClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProxy.class);

    private IClient targetClient;

    private ExecutorService executorService;

    private long timeout;

    private Integer minCorePoolSize;
    private Integer maxCorePoolSize;
    private Long keepAliveTime;

    public ClientProxy(IClient targetClient) {
        this.targetClient = targetClient;
        // 默认 5min 超时时间
        String timeOutProperty = System.getProperty(ConfigConstant.AKKA_WORKER_TIMEOUT,"300000");
        this.timeout = Long.parseLong(timeOutProperty);
        this.minCorePoolSize = Integer.parseInt(System.getProperty(ConfigConstant.WORKER_PROXY_MIN_POOL_SIZE, "1"));
        this.maxCorePoolSize = Integer.parseInt(System.getProperty(ConfigConstant.WORKER_PROXY_MAX_POOL_SIZE, "10"));
        // 默认 2h 线程存活时间
        this.keepAliveTime = Long.parseLong(System.getProperty(ConfigConstant.WORKER_PROXY_IDLE_KEEP_ALIVE_TIME, "7200000"));
        this.executorService = new ThreadPoolExecutor(minCorePoolSize, maxCorePoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new CustomThreadFactory(targetClient.getClass().getSimpleName() + "_" + this.getClass().getSimpleName()), new BlockCallerTimeoutPolicy(this.timeout));
    }

    @Override
    public void init(Properties prop) throws Exception {
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {
                        @Override
                        public String execute() throws Exception {
                            targetClient.init(prop);
                            return null;
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("init client error", e);
            // 初始化失败,关闭线程池
            executorService.shutdown();
            throw new RdosDefineException(e);
        }
    }

    @Override
    public void close(String pluginInfo) {
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            targetClient.close(pluginInfo);
                            return null;
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>() {

                        @Override
                        public JobResult execute() throws Exception {
                            return targetClient.submitJob(jobClient);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>() {

                        @Override
                        public JobResult execute() throws Exception {
                            return targetClient.cancelJob(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<RdosTaskStatus>() {

                        @Override
                        public RdosTaskStatus execute() throws Exception {
                            return targetClient.getJobStatus(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getJobMaster(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getMessageByHttp(String path) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getMessageByHttp(path);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getJobLog(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JudgeResult>() {

                        @Override
                        public JudgeResult execute() throws Exception {
                            return targetClient.judgeSlots(jobClient);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    return getJudgeResultWithException(e, e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return getJudgeResultWithException(e, e.getCause());
        }
    }

    private JudgeResult getJudgeResultWithException(Exception e, Throwable throwable) {
        if (throwable instanceof ClientArgumentException) {
            throw new ClientArgumentException(e);
        } else if (throwable instanceof LimitResourceException) {
            throw new LimitResourceException(e.getMessage());
        } else if (throwable instanceof RdosDefineException) {
            return JudgeResult.exception( "judgeSlots error" + ExceptionUtil.getErrorMessage(e));
        }
        throw new RdosDefineException(e);
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<List<String>>() {

                        @Override
                        public List<String> execute() throws Exception {
                            return targetClient.getContainerInfos(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getCheckpoints(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public ComponentTestResultDTO testConnect(String pluginInfo) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.testConnect(pluginInfo),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<List<Object>> executeQuery(String sql, String database) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.executeQuery(sql,database),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String uploadStringToHdfs(String bytes, String hdfsPath) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.uploadStringToHdfs(bytes,hdfsPath),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public ClusterResourceDTO getClusterResource() {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getClusterResource(),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getRollingLogBaseInfo(jobIdentifier), targetClient.getClass().getClassLoader(), true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public FlinkQueryResultDTO queryJobData(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.queryJobData(jobIdentifier), targetClient.getClass().getClassLoader(), true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public FlinkWebUrlResultDTO getWebMonitorUrl(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getWebMonitorUrl(jobIdentifier),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<ColumnDTO> getAllColumns(String tableName,String schemaName, String dbName) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getAllColumns(tableName,schemaName,dbName),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("getAllColumnsException,e:{}",ExceptionUtil.getErrorMessage(e));
            throw new RdosDefineException(e);
        }
    }

    @Override
    public CheckResultDTO grammarCheck(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.grammarCheck(jobClient), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String pluginInfo) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getDtScriptAgentLabel(pluginInfo), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<EngineJobCheckpointDTO> clearCheckpoint(List<EngineJobCheckpointDTO> checkpointPath) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.clearCheckpoint(checkpointPath), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public ApplicationInfoDTO retrieveJob(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.retrieveJob(jobIdentifier), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<ApplicationInfoDTO> listApplication(RdosTaskStatus status,String name,String applicationId) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.listApplication(status,name,applicationId), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getHdfsWithScript(String hdfsPath) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getHdfsWithScript(hdfsPath), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<String> getHdfsWithJob(HdfsQueryDTO hdfsQueryDTO) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getHdfsWithJob(hdfsQueryDTO), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<HdfsContentSummary> getContentSummary(List<String> hdfsDirPaths) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getContentSummary(hdfsDirPaths), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public Boolean deleteHdfsFile(String hdfsPath, boolean isRecursion) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.deleteHdfsFile(hdfsPath, isRecursion), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getJobMetricsAnalysis(JobIdentifier jobIdentifier, RdosTaskStatus status, Timestamp startTime, Timestamp endTime) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getJobMetricsAnalysis(jobIdentifier, status,startTime,endTime), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }


    @Override
    public Optional<CheckResultDTO> executeSql(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.executeSql(jobClient), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public Optional<List<FlinkTableResultDTO>> executeFlinkSQL(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.executeFlinkSQL(jobClient), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public SparkThriftServerDTO getThriftServerUrl(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getThriftServerUrl(jobIdentifier), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }
}
