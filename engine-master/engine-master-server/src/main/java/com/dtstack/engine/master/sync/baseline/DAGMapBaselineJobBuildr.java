package com.dtstack.engine.master.sync.baseline;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.api.enums.BaselineTaskTypeEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.dto.BaselineBuildDTO;
import com.dtstack.engine.master.dto.BaselineJobJobDTO;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.mapstruct.BaselineJobStruct;
import com.dtstack.engine.master.utils.JobExecuteOrderUtil;
import com.dtstack.engine.po.BaselineJobJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @Auther: dazhi
 * @Date: 2022/5/11 4:19 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class DAGMapBaselineJobBuildr extends AbstractStackBaselineJobBuilder {

    private final Logger LOGGER = LoggerFactory.getLogger(DAGMapBaselineJobBuildr.class);

    private final ScheduleJobService scheduleJobService;

    private final ScheduleJobJobDao scheduleJobJobDao;

    private final EnvironmentContext environmentContext;

    private final List<Set<BaselineJobJobDTO>> baselineJobJobs;

    private final BaselineJobStruct baselineJobStruct;

    public DAGMapBaselineJobBuildr(ApplicationContext applicationContext) {
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobJobDao = applicationContext.getBean(ScheduleJobJobDao.class);
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.baselineJobStruct = applicationContext.getBean(BaselineJobStruct.class);
        baselineJobJobs = Lists.newArrayList();
    }

    @Override
    public BaselineBuildDTO buildBaselineJob(String triggerDay, BaselineTaskDTO dto, Set<String> noExecCache, String cycTime) {
        BaselineBuildDTO baselineBuildDTO = new BaselineBuildDTO();
        List<BaselineJobJob> baselineJobJobList = Lists.newArrayList();
        try {
            Map<String, BaselineJobJobDTO> baselineJobJobCache = new HashMap<>();
            Map<String, Integer> baselineExecCache = new HashMap<>();
            List<AlarmChooseTaskDTO> taskVOS = dto.getTaskVOS();
            Integer appType = dto.getAppType();
            // 每一次批次最早执行的计划时间
            final AtomicReference<String> earliestCycTime = new AtomicReference<>();
            estimatedCompletionTime(triggerDay,taskVOS, appType,StringUtils.isNotBlank(cycTime)?
                    DateUtil.getDateStrTOFormat(cycTime,DateUtil.STANDARD_DATETIME_FORMAT,DateUtil.UN_STANDARD_DATETIME_FORMAT) : null,
                    (taskIds, jobs, sideMap, sonSideMap) -> {
                for (ScheduleJob job : jobs) {
                    BaselineJobJobDTO baselineJobJob = baselineJobJobCache.get(job.getJobKey());
                    // 计算预计运行时间
                    Integer estimatedExecTime = getEstimatedExecTime(job, baselineExecCache, noExecCache, sideMap);
                    LOGGER.info("jobId {} baselineId:{} estimatedExecTime is {}",job.getJobId(),dto.getId(),estimatedExecTime);
                    earliestCycTime.set(getEarliestCycTime(earliestCycTime,job));
                    Set<BaselineJobJobDTO> baselineJobJobSet = Sets.newHashSet();
                    if (baselineJobJob == null) {
                        baselineJobJob = new BaselineJobJobDTO();
                        baselineJobJob.setJobKey(job.getJobKey());
                        baselineJobJob.setSonJobKey(sonSideMap.get(job.getJobKey()));
                        baselineJobJob.setJobId(job.getJobId());
                        baselineJobJob.setTaskId(job.getTaskId());
                        baselineJobJob.setTaskAppType(job.getAppType());
                        baselineJobJob.setBaselineTaskType(getBaselineTaskType(job.getTaskId(), taskIds));
                        baselineJobJob.setEstimatedExecTime(estimatedExecTime);
                        baselineJobJob.setExpectStartTime(getExpectStartTime(job, sideMap, baselineJobJobCache,estimatedExecTime));
                        baselineJobJob.setExpectEndTime(getExpectEndTime(estimatedExecTime, baselineJobJob.getExpectStartTime()));
                        baselineJobJobCache.put(job.getJobKey(), baselineJobJob);
                        // 继续入栈
                        baselineJobJobSet.add(baselineJobJob);
                    }

                    if (CollectionUtils.isNotEmpty(baselineJobJobSet)) {
                        baselineJobJobs.add(baselineJobJobSet);
                    }
                }
            });

            if (StringUtils.isNotBlank(cycTime)) {
                baselineBuildDTO.setCycTime(cycTime);
            } else {
                baselineBuildDTO.setCycTime(earliestCycTime.get());
            }

            // 计算 预警时间，破线时间
            while (baselineJobJobs.size() > 0) {
                Set<BaselineJobJobDTO> baselineJobJobs = this.baselineJobJobs.remove(this.baselineJobJobs.size() - 1);

                for (BaselineJobJobDTO baselineJobJob : baselineJobJobs) {
                    Set<String> sonJobKeys = baselineJobJob.getSonJobKey();
                    Integer estimatedExecTime = getEstimatedExecTime(baselineJobJob.getEstimatedExecTime());
                    if (estimatedExecTime == null) {
                        baselineJobJob.setBrokenLineEndTime(null);
                        baselineJobJob.setBrokenLineStartTime(null);
                        baselineJobJob.setEarlyWarningEndTime(null);
                        baselineJobJob.setEarlyWarningStartTime(null);
                    } else if (CollectionUtils.isEmpty(sonJobKeys)) {
                        // 如果没有下游，说明是最底层的节点 所以 破线时间是 承诺时间，预计时间是 承诺时间-余量
                        Integer earlyWarnMargin = dto.getEarlyWarnMargin();
                        String replyTime = DateTime.parse(triggerDay,DateTimeFormat.forPattern(DateUtil.STANDARD_DATETIME_FORMAT))
                                .toString(DateUtil.DATE_FORMAT) + " " +dto.getReplyTime();
                        DateTime dateTime = DateTime.parse(replyTime, DateTimeFormat.forPattern(DateUtil.STANDARD_DATE_FORMAT));
                        long millis = dateTime.getMillis();
                        baselineJobJob.setBrokenLineEndTime(new Timestamp(millis));
                        baselineJobJob.setBrokenLineStartTime(new Timestamp(millis - estimatedExecTime));

                        if (earlyWarnMargin == null) {
                            earlyWarnMargin = 0;
                        }

                        baselineJobJob.setEarlyWarningEndTime(new Timestamp(millis - (earlyWarnMargin * 60 * 1000)));
                        baselineJobJob.setEarlyWarningStartTime(new Timestamp(millis - (earlyWarnMargin * 60 * 1000)
                                - estimatedExecTime));
                    } else {
                        // 如果有下游，则要通过下游推算出时间
                        List<BaselineJobJobDTO> dtos = Lists.newArrayList();
                        if (noExecCache.containsAll(sonJobKeys)) {
                            noExecCache.add(baselineJobJob.getJobKey());
                            continue;
                        }
                        for (String sonJobKey : sonJobKeys) {
                            BaselineJobJobDTO baselineJobJobDTO = baselineJobJobCache.get(sonJobKey);

                            if (baselineJobJobDTO != null) {
                                dtos.add(baselineJobJobDTO);
                            }

                            long brokenLineEndTime = dtos.stream().map(BaselineJobJobDTO::getBrokenLineStartTime)
                                    .filter(Objects::nonNull).mapToLong(Timestamp::getTime).max().orElse(0L);
                            long brokenLineStartTime = brokenLineEndTime - estimatedExecTime;


                            long earlWarningEndTime = dtos.stream().map(BaselineJobJobDTO::getEarlyWarningStartTime)
                                    .filter(Objects::nonNull).mapToLong(Timestamp::getTime).max().orElse(0L);
                            long earlWarningStartTime = earlWarningEndTime - estimatedExecTime;

                            baselineJobJob.setBrokenLineEndTime(new Timestamp(brokenLineEndTime));
                            baselineJobJob.setBrokenLineStartTime(new Timestamp(brokenLineStartTime));
                            baselineJobJob.setEarlyWarningStartTime(new Timestamp(earlWarningStartTime));
                            baselineJobJob.setEarlyWarningEndTime(new Timestamp(earlWarningEndTime));
                        }
                    }
                    baselineJobJobList.add(baselineJobStruct.toBaselineJobJob(baselineJobJob));
                }
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
        baselineBuildDTO.setBaselineJobJobs(baselineJobJobList);
        return baselineBuildDTO;
    }

    private String getEarliestCycTime(AtomicReference<String> earliestCycTime,ScheduleJob job) {
        String cycTime = earliestCycTime.get();
        if (StringUtils.isBlank(cycTime)) {
            return job.getCycTime();
        } else {
            long min = 0;
            long jobCyc = 0;
            try {
                min = DateUtil.stringToLong(cycTime, DateUtil.UN_STANDARD_DATETIME_FORMAT);
                jobCyc = DateUtil.stringToLong(job.getCycTime(), DateUtil.UN_STANDARD_DATETIME_FORMAT);
            } catch (ParseException e) {
                LOGGER.error("",e);
                return cycTime;
            }

            if (jobCyc < min) {
                return job.getCycTime();
            }
        }

        return cycTime;
    }

    private void estimatedCompletionTime(String triggerDay,
                                         List<AlarmChooseTaskDTO> taskVOS,
                                         Integer appType,
                                         String cycTime,
                                         EstimatedCompletionCallBack estimatedCompletionCallBack) throws ParseException {
        AtomicInteger count = new AtomicInteger();
        String timeStrWithoutSymbol = DateUtil.getTimeStrWithoutSymbol(triggerDay);
        Long start = JobExecuteOrderUtil.buildJobExecuteOrder(timeStrWithoutSymbol, count);
        Long end = JobExecuteOrderUtil.buildJobExecuteOrder(
                DateTime.parse(timeStrWithoutSymbol,
                                DateTimeFormat.forPattern(DateUtil.UN_STANDARD_DATETIME_FORMAT))
                        .plusDays(1).toString(DateUtil.UN_STANDARD_DATETIME_FORMAT)
                , count);

        List<Long> taskIds = taskVOS.stream().map(AlarmChooseTaskDTO::getTaskId).collect(Collectors.toList());

        // 第一步 taskVOS 今天生成的实例
        List<ScheduleJob> scheduleJobs = scheduleJobService.selectJobByTaskIdAndJobExecuteOrder(start, end, taskIds, appType,cycTime);
        // 开始查询有效上游
        Set<String> jobKeys = scheduleJobs.stream().map(ScheduleJob::getJobKey).collect(Collectors.toSet());

        // 出度集合
        Map<String, Set<String>> sideMap = new HashMap<>();
        Map<String, Set<String>> sonSideMap = new HashMap<>();
        while (CollectionUtils.isNotEmpty(jobKeys)) {
            if (!push(jobKeys)) {
                // 没进入栈中,说明有可能成环了所以直接退出循环
                break;
            }

            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKeys(jobKeys, RelyTypeEnum.NORMAL.getType());
            jobKeys.clear();
            scheduleJobJobs = filter(scheduleJobJobs);
            if (CollectionUtils.isEmpty(scheduleJobJobs)) {
                break;
            }

            for (ScheduleJobJob scheduleJobJob : scheduleJobJobs) {
                jobKeys.add(scheduleJobJob.getParentJobKey());
                // 设置边 sideMap
                setSide(sideMap, scheduleJobJob.getJobKey(), scheduleJobJob.getParentJobKey());

                // 设置边 sonSideMap
                setSide(sonSideMap, scheduleJobJob.getParentJobKey(), scheduleJobJob.getJobKey());
            }
        }

        // 出栈，每次输出一层输出置 callback 用于计算
        Set<String> popJobKeys = pop();
        while (CollectionUtils.isNotEmpty(popJobKeys)) {
            List<String> popJobKeyList = Lists.newArrayList(popJobKeys);
            List<ScheduleJob> jobs = scheduleJobService.listJobByJobKeys(popJobKeyList);
            estimatedCompletionCallBack.callback(taskIds,jobs,sideMap,sonSideMap);
            popJobKeys = pop();
        }
    }

    private List<ScheduleJobJob> filter( List<ScheduleJobJob> scheduleJobJobs) {
        List<String> parentJobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getParentJobKey).collect(Collectors.toList());
        List<String> jobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
        List<String> keys = Lists.newArrayList(parentJobKeys);
        keys.addAll(jobKeys);
        List<ScheduleJob> scheduleJobs = scheduleJobService.listJobByJobKeys(keys);
        Map<String, ScheduleJob> jobMap = scheduleJobs.stream().collect(Collectors.toMap(ScheduleJob::getJobKey, g -> (g)));
        List<ScheduleJobJob> returnJobJob = Lists.newArrayList();

        for (ScheduleJobJob scheduleJobJob : scheduleJobJobs) {
            ScheduleJob scheduleJob = jobMap.get(scheduleJobJob.getJobKey());

            if (scheduleJob != null) {
                String taskKey = scheduleJob.getTaskId() + "" +scheduleJob.getAppType();
                String parentJobKey = scheduleJobJob.getParentJobKey();

                ScheduleJob parentJob = jobMap.get(parentJobKey);
                if (StringUtils.isNotBlank(taskKey) && parentJob != null) {
                    String parentTaskKey = parentJob.getTaskId() + "" +parentJob.getAppType();

                    if (!taskKey.equals(parentTaskKey)) {
                        returnJobJob.add(scheduleJobJob);
                    }
                }
            }
        }

        return returnJobJob;
    }


    private Integer getEstimatedExecTime(Integer estimatedExecTime) {
        return estimatedExecTime;
    }

    private void setSide(Map<String, Set<String>> sideMap, String key, String value) {
        Set<String> sideSet = sideMap.get(key);

        if (CollectionUtils.isEmpty(sideSet)) {
            sideSet = new HashSet<>();
            sideSet.add(value);
        } else {
            sideSet.add(value);
        }

        sideMap.put(key,sideSet);
    }

    private Timestamp getExpectEndTime(Integer estimatedExecTime, Timestamp expectStartTime) {
        if (expectStartTime == null || estimatedExecTime == null) {
            return null;
        }

        return new Timestamp(expectStartTime.getTime()+estimatedExecTime);
    }

    /**
     * 计算预计运行时长
     * 计算公式：每个任务的预计运行时长 = 任务历史运行成功的平均运行时长
     * 任务所有历史运行实例的成功运行次数不足15次时取全部成功次数（包含补数据和周期实例的成功运行时长），超过15次取最近15次
     *
     * @param job               当前实例
     * @param baselineExecCache 基线实例缓存
     * @return 计算预计运行时长
     */
    private Integer getEstimatedExecTime(ScheduleJob job,
                                         Map<String, Integer> baselineExecCache,
                                         Set<String> noExecCache,
                                         Map<String, Set<String>> side) {
        Long taskId = job.getTaskId();
        Integer appType = job.getAppType();
        String jobKey = job.getJobKey();
        Set<String> parentKeys = side.get(job.getJobKey());

        // 如果parentKeys的集合是noExecCache的子集，则不需要在继续计算预计运行时长
        if (CollectionUtils.isNotEmpty(parentKeys)
                && CollectionUtils.isNotEmpty(noExecCache)
                && noExecCache.containsAll(parentKeys)) {
            noExecCache.add(jobKey);
            return null;
        }

        String taskKey = taskId + "_" + appType;
        Integer estimatedExecTime = baselineExecCache.get(taskKey);

        if (estimatedExecTime == null) {
            // 查询 任务前15执行成功的平均时长
            List<Integer> execTimes = scheduleJobService
                    .selectExecTimeByTaskIdAndAppType(job.getTaskId(), appType, RdosTaskStatus.FINISHED.getStatus(),
                            environmentContext.getMaxBaselineJobNum());

            if (CollectionUtils.isNotEmpty(noExecCache) && CollectionUtils.isEmpty(execTimes)) {
                noExecCache.add(jobKey);
                return null;
            }
            int sum = execTimes.stream().mapToInt(t -> t).sum();
            if (sum > 0) {
                estimatedExecTime = sum / execTimes.size();
                baselineExecCache.put(taskKey, estimatedExecTime);
                return estimatedExecTime*1000;
            }

            if (sum == 0) {
                return 0;
            }
        }
        return estimatedExecTime;
    }

    private Timestamp getExpectStartTime(ScheduleJob job,
                                         Map<String, Set<String>> sideMap,
                                         Map<String, BaselineJobJobDTO> baselineJobJobCache,
                                         Integer estimatedExecTime) throws ParseException {
        if (estimatedExecTime == null) {
            return null;
        }

        String jobKey = job.getJobKey();
        Set<String> parentCacheKeys = sideMap.get(jobKey);

        String cycTime = job.getCycTime();
        long max = DateUtil.stringToLong(cycTime, DateUtil.UN_STANDARD_DATETIME_FORMAT);
        if (CollectionUtils.isNotEmpty(parentCacheKeys)) {

            for (String parentCacheKey : parentCacheKeys) {
                BaselineJobJobDTO baselineJobJob = baselineJobJobCache.get(parentCacheKey);

                if (baselineJobJob != null) {
                    Timestamp startTime = baselineJobJob.getExpectEndTime();
                    long expectEndTime = startTime.getTime();
                    if (max < expectEndTime) {
                        max = expectEndTime;
                    }
                }
            }
        }
        return new Timestamp(max);
    }

    private Integer getBaselineTaskType(Long taskId, List<Long> taskIds) {
        if (taskIds.contains(taskId)) {
            return BaselineTaskTypeEnum.CHOOSE_TASK.getCode();
        }
        return BaselineTaskTypeEnum.CHAIN_TASK.getCode();
    }

    public Map<String, String> getBatchEstimatedFinish(String triggerDay,List<AlarmChooseTaskDTO> alarmChooseTaskDTOList,Integer appType, Set<String> cycTimeSet) {
        Map<String, String> batchMaps = Maps.newHashMap();
        Map<String, Integer> baselineExecCache = new HashMap<>();
        Map<String, BaselineJobJobDTO> baselineJobJobCache = new HashMap<>();
        for (String cycTime : cycTimeSet) {
            try {
                String timeStrWithoutSymbol = DateUtil.getTimeStrWithoutSymbol(cycTime);
                estimatedCompletionTime(triggerDay,alarmChooseTaskDTOList,appType,timeStrWithoutSymbol,(taskIds, jobs, sideMap, sonSideMap)->{
                    for (ScheduleJob job : jobs) {
                        // 不是目标任务计算预计运行时间
                        Integer estimatedExecTime = getEstimatedExecTime(job, baselineExecCache,null,sideMap);
                        // 预计开始时间
                        Timestamp expectStartTime = getExpectStartTime(job, sideMap, baselineJobJobCache, estimatedExecTime);
                        // 预计结束时间
                        Timestamp expectEndTime = getExpectEndTime(estimatedExecTime, expectStartTime);

                        if (taskIds.contains(job.getTaskId())) {
                            batchMaps.put(job.getCycTime(),expectEndTime!=null ? new DateTime(expectEndTime.getTime()).toString(DateUtil.STANDARD_FORMAT) : "-(历史数据不足)");
                        } else {
                            BaselineJobJobDTO dto = new BaselineJobJobDTO();
                            dto.setExpectEndTime(expectEndTime);
                            baselineJobJobCache.put(job.getJobKey(),dto);
                        }
                    }
                });
            } catch (ParseException e) {
                batchMaps.put(cycTime,null);
                LOGGER.error("",e);
            }
        }
        return batchMaps;
    }


    interface EstimatedCompletionCallBack {
        void callback(List<Long> taskIds,List<ScheduleJob> jobs,Map<String, Set<String>> sideMap,Map<String, Set<String>> sonSideMap) throws ParseException;
    }
}
