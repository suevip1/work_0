package com.dtstack.engine.master.resource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationStatus;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.OpenStatusEnum;
import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.AlertAlarmDao;
import com.dtstack.engine.dao.AlertAlarmRuleDao;
import com.dtstack.engine.dao.AlertRuleDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.listener.AlterEvent;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.dtstack.engine.master.strategy.ScanningAlterStrategy;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import com.dtstack.engine.po.AlertRule;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.google.common.collect.Lists;
import io.prometheus.client.exporter.PushGateway;
import joptsimple.internal.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * job resource monitor
 *
 * @author ：wangchuan
 * date：Created in 11:32 2023/1/17
 * company: www.dtstack.com
 */
@Component
public class JobResourceMonitor implements InitializingBean, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResourceMonitor.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private DataSourceXOperator dataSourceXOperator;

    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    protected AlertAlarmRuleDao alertAlarmRuleDao;

    @Autowired
    protected AlertAlarmDao alertAlarmDao;

    @Autowired
    protected AlertRuleDao alertRuleDao;

    @Autowired
    protected AlterEvent alterEvent;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 任务资源监控相关信息缓存
     */
    private final Map<String, JobResourceMonitorContext> jobMonitorCache = new ConcurrentHashMap<>();

    /**
     * pushGateway 信息缓存, 集群名称 --> pushGateway 相关信息
     */
    private final Map<String, PushGatewayInfo> pushGatewayInfoCache = new ConcurrentHashMap<>();

    /**
     * group key
     */
    private final Map<String, String> groupKey = new HashMap<>();

    /**
     * 本机 host
     */
    private final String host = AddressUtil.getOneIp();

    /**
     * 指标收集线程池
     */
    private ExecutorService jobMetricCollect;

    /**
     * 指标推送到 pushGateway 的 job 名称
     */
    private static final String JOB_NAME = "dag_schedule_job_resource_monitor";

    /**
     * 任务实例cpu核数redis存储key前缀
     */
    public static final String CORE_REDIS_PRE = "dag_core";

    /**
     * 任务实例内存使用核数redis存储key前缀
     */
    public static final String MEM_REDIS_PRE = "dag_mem";


    /**
     * 周期运行刷新数据到 pushGateway
     */
    @Override
    public void run() {
        Set<String> jobIds = jobMonitorCache.keySet();
        if (CollectionUtils.isEmpty(jobIds)) {
            return;
        }
        LOGGER.info("job resource monitor start, num: {}", jobIds.size());
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(jobIds.size());
        for (String jobId : jobIds) {
            try {
                JobResourceMonitorContext monitorDTO = jobMonitorCache.get(jobId);
                if (Objects.isNull(monitorDTO)) {
                    LOGGER.info("jobId: {} is not found from monitor cache, skip resource monitor", jobId);
                    continue;
                }
                ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
                if (Objects.isNull(scheduleJob)) {
                    LOGGER.info("jobId: {} is not found from schedule job, skip resource monitor", jobId);
                    continue;
                }
                if (RdosTaskStatus.isStopped(scheduleJob.getStatus())) {
                    // 保存 app id
                    updateTaskEngineJobId(monitorDTO.getJobClient());
                    resetResourceMetric(jobId);
                    // 任务结束则置为 100
                    resetProgressMetric(jobId, 100.0);
                    LOGGER.info("jobId: {} is stopped, skip resource monitor and remove from cache", jobId);
                    updateCoreAndMem(monitorDTO);
                    removeTaskMonitor(jobId);
                    continue;
                }
                if (!RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
                    resetResourceMetric(jobId);
                    // 未运行则设置 job 进度为 0
                    resetProgressMetric(jobId, 0.0);
                    LOGGER.warn("jobId: {} is not running, skip resource monitor", jobId);
                    continue;
                }
                Future<?> execResult = jobMetricCollect.submit(() -> collectMetric(monitorDTO));
                try {
                    execResult.get(environmentContext.getJobResourceMonitorTimeout(), TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOGGER.error("jobId: {} get task resource error", jobId, e);
                } finally {
                    if (!execResult.isDone()) {
                        execResult.cancel(true);
                        LOGGER.warn("jobId: {} task resource monitor not done and cancel it.", jobId);
                    }
                }
            } catch (Throwable e) {
                LOGGER.error("jobId: {} resource monitor task error", jobId, e);
            } finally {
                latch.countDown();
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("wait count down latch release error", e);
        }
        // 添加当天的时间作为 group key, 便于清理旧指标
        String timeNow = DateUtil.getFormattedDate(System.currentTimeMillis(), "yyyy-MM-dd");
        groupKey.put("push_time", timeNow);
        // 开始推送指标
        Collection<PushGatewayInfo> gatewayInfos = pushGatewayInfoCache.values();
        for (PushGatewayInfo gatewayInfo : gatewayInfos) {
            try {
                PushGateway pushGateway = gatewayInfo.getPushGateway();
                pushGateway.push(gatewayInfo.getCollectorRegistry(), JOB_NAME, groupKey);
            } catch (Throwable e) {
                LOGGER.error("push metric to pushGateway error, clusterName: {}, address: {}", gatewayInfo.getClusterName(), gatewayInfo.getPmAddress(), e);
            }
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("job resource monitor end, spend time : {} ms", endTime - startTime);
    }

    private void updateCoreAndMem(JobResourceMonitorContext context) {
        int memNum = context.getMemNum();
        int coreNum = context.getCoreNum();
        Integer runNum = context.getRunNum();
        JobClient jobClient = context.getJobClient();
        scheduleJobExpandDao.updateJobCoreAndMem(jobClient.getTaskId(), coreNum, memNum, runNum);
        //统计7天累计 放入redis
        Integer appType = jobClient.getAppType();
        Long projectId = jobClient.getProjectId();
        String time = DateTime.now().toString(DateUtil.DATE_FORMAT);
        refreshTotalValue(time, MEM_REDIS_PRE, appType, projectId, memNum);
        refreshTotalValue(time, CORE_REDIS_PRE, appType, projectId, coreNum);
    }

    public Integer refreshTotalValue(String time, String keyPrefix, Integer appType, Long projectId, Integer incrementVal) {
        String key = Strings.join(new String[]{keyPrefix, time, String.valueOf(appType), String.valueOf(projectId)}, "-");
        String value = redisTemplate.opsForValue().get(key);
        int cacheValue = StringUtils.isBlank(value) ? 0 : Integer.parseInt(value);
        if (incrementVal <= 0) {
            return cacheValue;
        }
        redisTemplate.opsForValue().set(key, String.valueOf(cacheValue + incrementVal));
        redisTemplate.expire(key, Duration.of(7, ChronoUnit.DAYS));
        return cacheValue;
    }

    /**
     * 保存任务 engineJobId, 只保存最后一次非运行中状态的 applicationId
     *
     * @param jobClient 任务配置信息
     */
    private void updateTaskEngineJobId(JobClient jobClient) {
        List<YarnApplicationInfoDTO> applicationInfoDTOS = dataSourceXOperator
                .listApplicationByTag(jobClient, jobClient.getTaskId())
                .stream()
                .filter(Objects::nonNull)
                .filter(info -> !YarnApplicationStatus.RUNNING.equals(info.getStatus()))
                .sorted(Comparator.comparingLong(app -> app.getStartTime().getTime()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(applicationInfoDTOS)) {
            return;
        }
        YarnApplicationInfoDTO lastYarnApplication = applicationInfoDTOS.get(applicationInfoDTOS.size() - 1);
        scheduleJobDao.updateJobAppId(jobClient.getTaskId(), lastYarnApplication.getApplicationId(), lastYarnApplication.getApplicationId());
        scheduleJobExpandDao.updateJobSubmitSuccess(jobClient.getTaskId(), lastYarnApplication.getApplicationId(), lastYarnApplication.getApplicationId());
        LOGGER.info("update the applicationId of jobId is [{}] to [{}]", jobClient.getTaskId(), lastYarnApplication.getApplicationId());
    }

    /**
     * 获取资源使用情况, 目前仅支持 hive sql 任务
     *
     * @param resourceMonitorDTO 任务资源监控信息
     */
    private void collectMetric(JobResourceMonitorContext resourceMonitorDTO) {
        JobClient jobClient = resourceMonitorDTO.getJobClient();
        List<YarnApplicationInfoDTO> applicationInfoDTOS = dataSourceXOperator
                .listApplicationByTag(jobClient, jobClient.getTaskId())
                .stream()
                .filter(Objects::nonNull)
                .filter(info -> YarnApplicationStatus.RUNNING.equals(info.getStatus()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(applicationInfoDTOS)) {
            resetResourceMetric(jobClient.getTaskId());
            LOGGER.info("jobId: {} get running application from yarn is empty", jobClient.getTaskId());
            return;
        }

        int core = applicationInfoDTOS.stream().mapToInt(info -> Objects.isNull(info.getCore()) ? 0 : info.getCore()).sum();
        int memory = applicationInfoDTOS.stream().mapToInt(info -> Objects.isNull(info.getMemory()) ? 0 : info.getMemory()).sum();
        double progress = applicationInfoDTOS.stream().mapToDouble(info -> Objects.isNull(info.getProgress()) ? 0 : info.getProgress()).average().orElse(0);
        resourceMonitorDTO.refreshCoreAndMem(core,memory);
        // 处理资源使用超限告警
        handleResourceOverLimit(core, memory, jobClient.getTaskId());
        resourceMonitorDTO.addMetric(GaugeType.JOB_RESOURCE_CORE, core);
        resourceMonitorDTO.addMetric(GaugeType.JOB_RESOURCE_MEMORY, memory);

        JobIdentifier jobIdentifier = new JobIdentifier(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId()
                , jobClient.getTenantId(), jobClient.getEngineType(), null, jobClient.getUserId(), jobClient.getResourceId(), null, jobClient.getComponentVersion());

        // 获取 sql 执行信息
        JobExecInfo jobExecInfo = operatorDistributor
                .getOperator(jobClient.getClientType(), jobClient.getEngineType())
                .getJobExecInfo(jobIdentifier, false, false);
        if (Objects.nonNull(jobExecInfo) && Objects.nonNull(jobExecInfo.getCurrentExecSqlNum()) && Objects.nonNull(jobExecInfo.getSqlNum())) {
            // hive sql 执行进度结果例如: 25.5, 保留一位有效数字, 计算方式为 (currentExecSql - 1 + currentRunningProgress / 100) * 100 / sqlNum
            double allProgress = ((jobExecInfo.getCurrentExecSqlNum() - 1 + progress / 100) * 100) / jobExecInfo.getSqlNum();
            resourceMonitorDTO.addMetric(GaugeType.JOB_PROGRESS_REAL_PERCENTAGE, allProgress);
        } else {
            LOGGER.warn("compute job progress error, jobId: {}, jobExecInfo: {}", jobClient.getTaskId(), jobExecInfo);
        }
    }

    /**
     * 判断是否配置资源使用超限告警, 如果触发则发送告警
     *
     * @param core   当前 cpu 使用
     * @param memory 当前内存使用
     * @param jobId  任务实例 id
     */
    private void handleResourceOverLimit(int core, int memory, String jobId) {
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
            if (Objects.isNull(scheduleJob)) {
                return;
            }
            // 告警只对周期调度、开启了补数据告警下的补数据任务、手动任务生效
            if (!EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType()) &&
                            !EScheduleType.MANUAL.getType().equals(scheduleJob.getType()) &&
                            !EScheduleType.FILL_DATA.getType().equals(scheduleJob.getType())) {
                return;
            }
            // 告警 id 集合
            List<Long> alertIds = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(
                    AlarmRuleBusinessTypeEnum.TASK.getCode(), Lists.newArrayList(scheduleJob.getTaskId()));
            if (CollectionUtils.isEmpty(alertIds)) {
                return;
            }
            List<AlertAlarm> alertAlarmList = alertAlarmDao.selectByIds(alertIds, scheduleJob.getAppType(), null);
            if (CollectionUtils.isEmpty(alertAlarmList)) {
                return;
            }
            List<Long> openAlertIds = alertAlarmList.stream()
                    .filter(alertAlarm -> OpenStatusEnum.OPEN.getCode().equals(alertAlarm.getOpenStatus()))
                    .map(AlertAlarm::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(openAlertIds)) {
                return;
            }
            List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(openAlertIds, AlarmRuleBusinessTypeEnum.RULE.getCode());
            // 告警 id --> 所有的告警配置
            Map<Long, List<AlertAlarmRule>> alertRuleMap = alertAlarmRules.stream()
                    .collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId));
            for (Map.Entry<Long, List<AlertAlarmRule>> entry : alertRuleMap.entrySet()) {
                // 如果是补数据调度且未配置告警则跳过
                Supplier<Boolean> isFillDataNotAlert = () -> {
                    if (!EScheduleType.FILL_DATA.getType().equals(scheduleJob.getType())) {
                        return false;
                    }
                    List<AlertAlarmRule> fillDataRule = alertAlarmRuleDao.selectByAlarmIds(Lists.newArrayList(entry.getKey()), AlarmRuleBusinessTypeEnum.FILL_DATA_ALERT.getCode());
                    return CollectionUtils.isEmpty(fillDataRule);
                };
                if (BooleanUtils.isTrue(isFillDataNotAlert.get())) {
                    continue;
                }
                Long alertAlarmId = entry.getKey();
                // 该告警配置的所有告警规则(就是告警触发条件)
                List<Long> ruleIds = entry.getValue().stream()
                        .filter(alertAlarmRule -> AlarmRuleBusinessTypeEnum.RULE.getCode().equals(alertAlarmRule.getBusinessType()))
                        .map(AlertAlarmRule::getBusinessId).collect(Collectors.toList());
                // 判断有没有资源超限告警
                AlertRule alertRule = alertRuleDao.selectByIds(ruleIds)
                        .stream()
                        .filter(rule -> StringUtils.equals(ScanningAlterStrategy.RESOURCE_OVER_LIMIT, rule.getKey()))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(alertRule)) {
                    continue;
                }
                AlertAlarm alertAlarm = alertAlarmDao.selectByPrimaryKey(alertAlarmId);
                if (Objects.isNull(alertAlarm)) {
                    continue;
                }
                String extraParams = alertAlarm.getExtraParams();
                if (StringUtils.isBlank(extraParams)) {
                    LOGGER.info("jobId: {}, alertAlarm id: {}, extraParams is blank.", jobId, alertAlarmId);
                    continue;
                }
                JSONObject extraParamsJson = JSONObject.parseObject(extraParams);
                // 资源限制
                Integer limitCore = extraParamsJson.getInteger("limitCore");
                Integer limitMemory = extraParamsJson.getInteger("limitMemory");
                // 判断资源是否超限
                if (Objects.isNull(limitCore) || Objects.isNull(limitMemory)) {
                    LOGGER.warn("jobId: {}, alertAlarm id: {}, limitCore or limitMemory is empty", jobId, alertAlarmId);
                    continue;
                }
                if (core > limitCore || memory > limitMemory) {
                    // 超限
                    LOGGER.info("jobId: {}, alertAlarm id: {}, resource over limit, limitCore: {}, limitMemory: {}, currentCore: {}, currentMemory: {}",
                            jobId, alertAlarmId, limitCore, limitMemory, core, memory);
                    // 构建 eventContext
                    AlterEventContext context = new AlterEventContext();
                    context.setKey(AlterKey.resource_over_limit);
                    context.setStatus(scheduleJob.getStatus());
                    context.setJobId(scheduleJob.getJobId());
                    context.setTaskId(scheduleJob.getTaskId());
                    context.setAppType(scheduleJob.getAppType());
                    context.setAlertAlarmId(alertAlarmId);
                    context.setResourceUsage(String.format("%sC%sM(限制%sC%sM)", core, memory, limitCore, limitMemory));
                    ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
                    if (scheduleTaskShade != null) {
                        context.setTaskName(scheduleTaskShade.getName());
                        context.setTaskType(scheduleTaskShade.getTaskType());
                        context.setOwnerUserId(scheduleTaskShade.getOwnerUserId());
                    }
                    alterEvent.event(context);
                }
            }
        } catch (Exception e) {
            LOGGER.error("jobId: {}, judge job resource over limit error", jobId, e);
        }
    }

    /**
     * 添加任务资源监控到缓存中
     *
     * @param jobClient 人物信息
     */
    public void addTaskMonitor(JobClient jobClient) {
        if (!isSupportResourceMonitor(jobClient)) {
            return;
        }
        String hiveConf = clusterService.getConfigByKey(jobClient.getTenantId(), EComponentType.HIVE_SERVER.getConfName(), false, null, false);
        if (StringUtils.isBlank(hiveConf)) {
            LOGGER.warn("jobId:{}, hive conf is empty", jobClient.getTaskId());
            return;
        }
        JSONObject hiveInfo = JSONObject.parseObject(hiveConf);
        Boolean isMonitor = hiveInfo.getBoolean(GlobalConst.HIVE_MONITOR_RESOURCE_ENABLE);
        if (BooleanUtils.isNotTrue(isMonitor)) {
            LOGGER.info("jobId:{}, hive monitor resources is not enable", jobClient.getTaskId());
            return;
        }
        String pmHost = hiveInfo.getString(GlobalConst.PUSH_GATEWAY_HOST);
        String pmPort = hiveInfo.getString(GlobalConst.PUSH_GATEWAY_PORT);
        if (StringUtils.isBlank(pmHost) || StringUtils.isBlank(pmPort)) {
            LOGGER.info("jobId:{}, hive prometheus config is empty", jobClient.getTaskId());
            return;
        }

        Cluster cluster = clusterService.getCluster(jobClient.getTenantId());
        String pmAddress = String.format("%s:%s", pmHost, pmPort);

        if (jobMonitorCache.containsKey(jobClient.getTaskId())) {
            LOGGER.info("jobId:{} unable add to jobMonitorCache, because jobId already exist.", jobClient.getTaskId());
            return;
        }

        // 添加缓存, 并判断 pushGateway 地址有没有变更, 如果发生变更则需要重新初始化 pushGateway 客户端
        PushGatewayInfo pushGatewayInfo = pushGatewayInfoCache.computeIfAbsent(cluster.getClusterName(), key -> new PushGatewayInfo(key, pmAddress));
        pushGatewayInfo.refresh(pmAddress);
        ScheduleJobExpand jobExpand = scheduleJobExpandDao.getExpandByJobId(jobClient.getTaskId());
        jobMonitorCache.put(jobClient.getTaskId(), new JobResourceMonitorContext(jobClient, host, pushGatewayInfo, jobExpand.getRunNum()));
        LOGGER.info("jobId:{} add to jobMonitorCache success, cache size: {}", jobClient.getTaskId(), jobMonitorCache.size());
    }

    /**
     * 判断当前引擎类型是否支持资源监控
     *
     * @param jobClient 任务信息
     * @return 是否支持
     */
    private boolean isSupportResourceMonitor(JobClient jobClient) {
        // 目前仅支持 hive 任务
        return StringUtils.isNotBlank(jobClient.getEngineType()) && EngineType.Hive.equals(EngineType.getEngineType(jobClient.getEngineType()));
    }


    /**
     * 移除任务资源监控信息
     *
     * @param jobId 任务 id
     */
    private void removeTaskMonitor(String jobId) {
        JobResourceMonitorContext jobResourceMonitorContext = jobMonitorCache.get(jobId);
        if (Objects.isNull(jobResourceMonitorContext)) {
            return;
        }
        jobMonitorCache.remove(jobId);
        LOGGER.info("jobId:{} remove to jobMonitorCache success, cache size: {}", jobId, jobMonitorCache.size());
    }

    /**
     * 资源使用情况重置为 0
     *
     * @param jobId 任务 id
     */
    private void resetResourceMetric(String jobId) {
        JobResourceMonitorContext monitorContext = jobMonitorCache.get(jobId);
        if (Objects.nonNull(monitorContext)) {
            monitorContext.addMetric(GaugeType.JOB_RESOURCE_CORE, 0);
            monitorContext.addMetric(GaugeType.JOB_RESOURCE_MEMORY, 0);
        }
    }

    /**
     * job 进度重置为 0
     *
     * @param jobId 任务 id
     */
    private void resetProgressMetric(String jobId, double val) {
        JobResourceMonitorContext monitorContext = jobMonitorCache.get(jobId);
        if (Objects.nonNull(monitorContext)) {
            monitorContext.addMetric(GaugeType.JOB_PROGRESS_REAL_PERCENTAGE, val);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jobMetricCollect = new ThreadPoolExecutor(environmentContext.getJobResourceMonitorPoolCoreSize(),
                environmentContext.getJobResourceMonitorPoolMaxSize(), 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new CustomThreadFactory("job-resource-monitor-collect"), new BlockCallerPolicy());
        // 指标推送定时调度线程池
        ScheduledExecutorService monitorSubmitExecutor = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("job-resource-monitor-submit"));
        monitorSubmitExecutor.scheduleAtFixedRate(this, 10, environmentContext.getJobResourceMonitorPeriod(), TimeUnit.SECONDS);
        groupKey.put("instance", host);
    }
}
