package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTask;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.vo.FindParentJobVO;
import com.dtstack.engine.api.vo.ParentJobStatusVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.job.*;
import com.dtstack.engine.api.vo.task.TaskViewElementVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BaselineJobJobDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dto.ScheduleJobJobTaskDTO;
import com.dtstack.engine.master.dto.ScheduleJobJobDTO;
import com.dtstack.engine.master.enums.OperateTypeEnum;
import com.dtstack.engine.master.mapstruct.ScheduleJobStruct;
import com.dtstack.engine.master.utils.JobKeyUtils;
import com.dtstack.engine.master.utils.TaskUtils;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.po.BaselineJobJob;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleJobJobService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobJobService.class);

    private static final String WORKFLOW_PARENT = "0";

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobService batchJobService;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private EnvironmentContext context;

    @Autowired
    private ScheduleTaskShadeService taskShadeService;

    @Autowired
    private WorkSpaceProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleJobOperateService scheduleJobOperateService;

    @Autowired
    private ScheduleJobStruct scheduleJobStruct;

    @Autowired
    private BaselineJobService baselineJobService;

    @Autowired
    private BaselineJobJobDao baselineJobJobDao;

    /**
     * @author newman
     * @Description
     * @Date 2021/1/5 4:09 下午
     * @param jobId: 任务id
     * @param level: 展开层数
     * @return: com.dtstack.engine.master.vo.ScheduleJobVO
     **/
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringNew( Long jobId,
                                                                        Integer level) throws Exception {

        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        level = checkLevel(level);
        if (job.getFlowJobId() != null && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            //如果是工作流子节点则返回整个工作流子节点依赖关系
            try {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
                if(null == flowJob){
                    return null;
                }
                //工作流下全部实例
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO = displayOffSpringForFlowWorkNew(flowJob);
                if(null!=subJobVO) {
                    subJobVO.setProjectId(flowJob.getProjectId());
                }
                return subJobVO;
            } catch (Exception e) {
                LOGGER.error("get flow work subJob error", e);
            }
        }
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobsNew(job.getJobKey(), level, true);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, true);
        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);
        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<String, ScheduleTaskShade> idTaskMap = new HashMap<>();
        //获取keyJobMap和taskId-TaskShade map组
        getRelationData(allJobKeys, keyJobMap, idTaskMap);
        com.dtstack.engine.master.vo.ScheduleJobVO offSpring = getOffSpring(root, keyJobMap, idTaskMap, false);
        packRootInfo(job,offSpring);
        return offSpring;
    }

    private void packRootInfo(ScheduleJob job, com.dtstack.engine.master.vo.ScheduleJobVO vo) {
        List<ScheduleJobJob> jobjobs = scheduleJobJobDao.listByJobKeys(Lists.newArrayList(job.getJobKey()),null);

        if (CollectionUtils.isNotEmpty(jobjobs)) {
            List<String> jobKeys = jobjobs.stream().map(ScheduleJobJob::getParentJobKey).collect(Collectors.toList());
            List<ScheduleJob> jobs = scheduleJobDao.listJobByJobKeys(jobKeys);

            if (CollectionUtils.isNotEmpty(jobs)) {
                // 有上游任务
                vo.setRootNode(Boolean.FALSE);
            } else {
                vo.setRootNode(Boolean.TRUE);
            }
        } else {
            vo.setRootNode(Boolean.TRUE);
        }
    }

    /**
     * @author newman
     * @Description 校验level值
     * @Date 2021/1/5 4:29 下午
     * @param level:
     * @return: void
     **/
    private Integer checkLevel(Integer level) {
        if (level == null || level < 1) {
            //控制展开层数
            level = 1;
        }
        Integer jobLevel = context.getJobJobLevel();
        if(level !=null && level > jobLevel){
            level = jobLevel;
        }
        return level;
    }

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * @author toutian
     */
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpring( Long jobId,
                                                                        Integer level) throws Exception {
        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        if (level == null || level < 1) {
            level = 1;
        }
        if (job.getFlowJobId() != null && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            //如果是工作流子节点则返回整个工作流子节点依赖关系
            try {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
                //工作流下全部实例,层级level使用int最大值，这个改成最大10层，否则可能会oom
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO = displayOffSpringForFlowWork(flowJob);
                if(null != subJobVO) {
                    subJobVO.setProjectId(flowJob.getProjectId());
                }
                return subJobVO;
            } catch (Exception e) {
                LOGGER.error("get flow work subJob error", e);
            }
        }
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(job.getJobKey(), level, true, null);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, true);
        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);
        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<String, ScheduleTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);
        //flowJobId不为0时表示是工作流中的子任务
        boolean isSubTask = !StringUtils.equals("0", job.getFlowJobId());

        return getOffSpring(root, keyJobMap, idTaskMap, isSubTask);
    }

    private void getRelationData(Set<String> allJobKeys, Map<String, ScheduleJob> keyJobMap,
                                 Map<String, ScheduleTaskShade> idTaskMap) throws Exception {
        List<ScheduleJob> jobs = scheduleJobDao.listJobByJobKeys(allJobKeys);
        if(CollectionUtils.isEmpty(jobs)){
            return;
        }
        Map<Integer, List<Long>> mapTaskId = Maps.newHashMap();
        for (ScheduleJob scheduleJob : jobs) {
            keyJobMap.put(scheduleJob.getJobKey(), scheduleJob);

            List<Long> taskIds = mapTaskId.get(scheduleJob.getAppType());

            if (CollectionUtils.isEmpty(taskIds)) {
                taskIds = Lists.newArrayList();
            }

            taskIds.add(scheduleJob.getTaskId());
            mapTaskId.put(scheduleJob.getAppType(),taskIds);
        }
        List<ScheduleTaskShade> taskAllShades = Lists.newArrayList();

        for (Map.Entry<Integer, List<Long>> entry : mapTaskId.entrySet()) {
            Integer appType = entry.getKey();
            List<Long> taskIds = entry.getValue();
            List<ScheduleTaskShade> tasks = batchTaskShadeService.getSimpleTaskRangeAllByIds(taskIds, appType);

            if (CollectionUtils.isNotEmpty(tasks)) {
                taskAllShades.addAll(tasks);
            }
        }
        taskAllShades.forEach(item -> idTaskMap.put(item.getTaskId()+"-"+item.getAppType(), item));
    }

    private ScheduleJobJobDTO getTree(ScheduleJobJobDTO root, Map<Integer, List<ScheduleJobJob>> result, int level, boolean isChild) {
        if (level <= result.size()) {
            List<ScheduleJobJob> currentLevel = result.get(level);
            List<ScheduleJobJobDTO> children = new ArrayList<>();
            for (ScheduleJobJob jobJob : currentLevel) {
                if (isChild) {
                    if (jobJob.getParentJobKey().equals(root.getJobKey())) {
                        ScheduleJobJobDTO child = new ScheduleJobJobDTO();
                        child.setJobKey(jobJob.getJobKey());
                        child.setRelyType(jobJob.getRelyType());
                        children.add(child);
                    }
                } else {
                    if (jobJob.getJobKey().equals(root.getJobKey())) {
                        ScheduleJobJobDTO parent = new ScheduleJobJobDTO();
                        parent.setJobKey(jobJob.getParentJobKey());
                        parent.setRelyType(jobJob.getRelyType());
                        children.add(parent);
                    }
                }
            }
            int nextLevel = ++level;
            for (ScheduleJobJobDTO child : children) {
                getTree(child, result, nextLevel, isChild);
            }
            root.setChildren(children);
        }
        return root;
    }

    /**
     * @Desc 各层任务依赖关系
     * @param rootKey
     * @param level 防止一直递归
     * @param getChild
     * @return
     */
    private Map<Integer, List<ScheduleJobJob>> getSpecifiedLevelJobJobsNew(String rootKey, int level, boolean getChild) {

        Map<Integer, List<ScheduleJobJob>> result = new HashMap<>(16);
        List<String> jobKeys = new ArrayList<>();
        Map<String,Set<String>> jobKeyRelations = Maps.newHashMap();
        jobKeyRelations.put(rootKey,Sets.newHashSet(rootKey));
        jobKeys.add(rootKey);
        int jobLoop = 1;
        for (int leveCount = level; leveCount > 0; leveCount--) {
            List<ScheduleJobJobTaskDTO> jobJobs;
            if (getChild) {
                //向下获取
                jobJobs = scheduleJobJobDao.listByParentJobKeysWithOutSelfTask(jobKeys);
            } else {
                //向上获取
                jobJobs = scheduleJobJobDao.listByJobKeysWithOutSelfTask(jobKeys);
            }
            List<String> finalJobKeys = jobKeys;
            //过滤掉不满足条件的jobKey
            jobJobs = filterJobJobs(getChild, jobJobs, finalJobKeys);
            if (CollectionUtils.isEmpty(jobJobs)) {
                return result;
            }
            //获取jobKeys
            jobKeys = getJobKeys(getChild, jobJobs);
            //校验是否成环
            if(checkIsLoop(getChild,jobJobs, jobKeyRelations)){
                return result;
            }
            List<ScheduleJobJob> jobJobList = jobJobs.stream().map(ScheduleJobJobTaskDTO::toJobJob).collect(Collectors.toList());
            LOGGER.info("count info --- rootKey:{} jobJobList size:{} jobLoop:{}", rootKey, jobJobList.size(), jobLoop);
            result.put(jobLoop, jobJobList);
            jobLoop++;
        }
        return result;
    }

    /**
     * @param jobJobs:
     * @param jobKeyRelations:
     * @author newman
     * @Description 检测工作实例是否成环
     * @Date 2021/1/6 2:39 下午
     * @return: Boolean 是否成环
     **/
    private Boolean checkIsLoop(Boolean getChild, List<ScheduleJobJobTaskDTO> jobJobs, Map<String,Set<String>> jobKeyRelations) {
        if (CollectionUtils.isEmpty(jobJobs)) {
            return Boolean.FALSE;
        }

        Map<String, List<ScheduleJobJobTaskDTO>> jobKeyMap;
        if (getChild) {
            // 向下判断
            jobKeyMap = jobJobs.stream().collect(Collectors.groupingBy(ScheduleJobJobTaskDTO::getParentJobKey));
        } else {
            // 向上判断
            jobKeyMap = jobJobs.stream().collect(Collectors.groupingBy(ScheduleJobJobTaskDTO::getJobKey));
        }

        if (isRing(getChild,jobKeyRelations, jobKeyMap)) {
            LOGGER.error("jobKeyRelations:{}", JSON.toJSONString(jobKeyRelations));
            return Boolean.TRUE;
        }
        return false;
    }

    private boolean isRing(Boolean getChild,Map<String, Set<String>> jobKeyRelations, Map<String, List<ScheduleJobJobTaskDTO>> jobKeyMap) {
        Map<String, Set<String>> temporaryMap = Maps.newHashMap(jobKeyRelations);
        for (Map.Entry<String, Set<String>> ketSetEntry : temporaryMap.entrySet()) {
            String key = ketSetEntry.getKey();
            // 找到每条链表的子节点
            List<ScheduleJobJobTaskDTO> scheduleJobJobTaskDTOS = jobKeyMap.get(key);

            if (CollectionUtils.isNotEmpty(scheduleJobJobTaskDTOS)) {
                Set<String> keys = ketSetEntry.getValue();

                for (ScheduleJobJobTaskDTO scheduleJobJobTaskDTO : scheduleJobJobTaskDTOS) {
                    HashSet<String> newKeys = Sets.newHashSet(keys);
                    if (getChild) {
                        // 向下判断
                        if (!newKeys.add(scheduleJobJobTaskDTO.getJobKey())) {
                            // 添加失败 说明成环
                            return true;
                        } else {
                            // 添加成功
                            jobKeyRelations.put(scheduleJobJobTaskDTO.getJobKey(), newKeys);
                        }
                    } else {
                        // 向上判断
                        if (!newKeys.add(scheduleJobJobTaskDTO.getParentJobKey())) {
                            // 添加失败 说明成环
                            return true;
                        } else {
                            // 添加成功
                            jobKeyRelations.put(scheduleJobJobTaskDTO.getParentJobKey(), newKeys);
                        }
                    }

                }
            }
        }
        return false;
    }


    private List<String> getJobKeys(boolean getChild, List<ScheduleJobJobTaskDTO> jobJobs) {

        List<String> jobKeys = new ArrayList<>();
        for (ScheduleJobJobTaskDTO jobJob : jobJobs) {
            if (getChild) {
                jobKeys.add(jobJob.getJobKey());
            } else {
                jobKeys.add(jobJob.getParentJobKey());
            }
        }
        return jobKeys;
    }

    private List<ScheduleJobJobTaskDTO> filterJobJobs(boolean getChild, List<ScheduleJobJobTaskDTO> jobJobs, List<String> finalJobKeys) {
        jobJobs = jobJobs.stream().filter(jobjob -> {
            String temp;
            if (!getChild) {
                temp = jobjob.getJobKey();
            } else {
                temp = jobjob.getParentJobKey();
            }
            return finalJobKeys.contains(temp);
        }).collect(Collectors.toList());
        return jobJobs;
    }

    /**
     * @param rootKey
     * @param level 这里设置为10，防止一直递归
     * @param getChild
     * @param parentFlowJobJobKey 若root节点为工作流里的子节点，则为工作流父节点的jobKey值，否则为NULL
     *                            在业务上不需要展示从工作流子节点到父节点的关系
     * @return
     */
    private Map<Integer, List<ScheduleJobJob>> getSpecifiedLevelJobJobs(String rootKey, int level, boolean getChild, String parentFlowJobJobKey) {

        Map<Integer, List<ScheduleJobJob>> result = new HashMap<>(16);
        List<String> jobKeys = new ArrayList<>();

        jobKeys.add(rootKey);
        int jobLoop = 1;
        for (int leveCount = level; leveCount > 0; leveCount--) {
            List<ScheduleJobJobTaskDTO> jobJobs;
            if (getChild) {
                jobJobs = scheduleJobJobDao.listByParentJobKeysWithOutSelfTask(jobKeys);
            } else {
                jobJobs = scheduleJobJobDao.listByJobKeysWithOutSelfTask(jobKeys);
            }
            List<String> finalJobKeys = jobKeys;
            //过滤掉不满足条件的jobJob
            jobJobs = filterJobJobs(getChild, jobJobs, finalJobKeys);
            if (CollectionUtils.isEmpty(jobJobs)) {
                return result;
            }
            //重新给jobKeys赋值
            jobKeys = getJobKeys(getChild, jobJobs);
            List<ScheduleJobJob> jobJobList = jobJobs.stream().map(ScheduleJobJobTaskDTO::toJobJob).collect(Collectors.toList());
            LOGGER.info("count info --- rootKey:{} jobJobList size:{} jobLoop:{}", rootKey, jobJobList.size(), jobLoop);
            result.put(jobLoop, jobJobList);
            jobLoop++;
        }
        return result;
    }

    /**
     * @author newman
     * @Description 展开工作流子任务
     * @Date 2021/1/5 4:40 下午
     * @param flowJob:
     * @return: com.dtstack.engine.master.vo.ScheduleJobVO
     **/
    private com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringForFlowWorkNew(ScheduleJob flowJob) throws Exception {
        com.dtstack.engine.master.vo.ScheduleJobVO vo = null;
        // 递归获取level层的子节点
        Integer level = context.getWorkFlowLevel();
        //获取工作流子节点各层任务依赖关系
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobsNew(flowJob.getJobKey(), level, true);
        List<ScheduleJobJob> firstLevel = result.get(1);
        if (CollectionUtils.isNotEmpty(firstLevel)) {
            Set<String> allJobKeys = new HashSet<>();
            getAllJobKeys(result, allJobKeys);
            Map<String, ScheduleJob> keyJobMap = new HashMap<>(16);
            Map<String, ScheduleTaskShade> idTaskMap = new HashMap<>(16);
            //获取keyJobMap和taskId-TaskShade map组
            getRelationData(allJobKeys, keyJobMap, idTaskMap);
            ScheduleJobJob beginJobJob = null;
            for (ScheduleJobJob jobjob : firstLevel) {
                ScheduleJob subJob = keyJobMap.get(jobjob.getJobKey());
                //找出工作流的起始节点
                if (StringUtils.equals(subJob.getFlowJobId(), flowJob.getJobId())) {
                    beginJobJob = jobjob;
                    break;
                }
            }
            if (beginJobJob == null) {
                LOGGER.error("displayOffSpringForFlowWork end with no subTasks with flowJobKey [{}]", flowJob.getJobKey());
                return null;
            }
            ScheduleJobJobDTO root = new ScheduleJobJobDTO();
            root.setJobKey(beginJobJob.getJobKey());
            //第一层作为root
            getTree(root, result, 2, true);
            vo = getOffSpring(root, keyJobMap, idTaskMap, true);
        }
        return vo;
    }



    private com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringForFlowWork(ScheduleJob flowJob) throws Exception {
        com.dtstack.engine.master.vo.ScheduleJobVO vo = null;
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(flowJob.getJobKey(), environmentContext.getMaxDeepShow(), true, null);
        List<ScheduleJobJob> firstLevel = result.get(1);
        if (CollectionUtils.isNotEmpty(firstLevel)) {
            Set<String> allJobKeys = new HashSet<>();
            getAllJobKeys(result, allJobKeys);

            Map<String, ScheduleJob> keyJobMap = new HashMap<>(16);
            Map<String, ScheduleTaskShade> idTaskMap = new HashMap<>(16);
            getRelationData(allJobKeys, keyJobMap, idTaskMap);

            ScheduleJobJob beginJobJob = null;
            for (ScheduleJobJob jobjob : firstLevel) {
                ScheduleJob subJob = keyJobMap.get(jobjob.getJobKey());
                //找出工作流的起始节点
                if (StringUtils.equals(subJob.getFlowJobId(), flowJob.getJobId())) {
                    beginJobJob = jobjob;
                    break;
                }
            }
            if (beginJobJob == null) {
                LOGGER.error("displayOffSpringForFlowWork end with no subTasks with flowJobKey [{}]", flowJob.getJobKey());
                return null;
            }
            ScheduleJobJobDTO root = new ScheduleJobJobDTO();
            root.setJobKey(beginJobJob.getJobKey());
            //第一层作为root
            getTree(root, result, 2, true);
            vo = getOffSpring(root, keyJobMap, idTaskMap, true);
        }
        return vo;
    }

    public com.dtstack.engine.master.vo.ScheduleJobVO getOffSpring(
            ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap, Map<String, ScheduleTaskShade> idTaskMap, boolean isSubTask) {

        ScheduleJob job = keyJobMap.get(root.getJobKey());
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        vo.setProjectId(job.getProjectId());
        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(vo.getStatus())) {
            vo.setStatus(RdosTaskStatus.RUNNING.getStatus());
        }

        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId()+"-"+job.getAppType());
        if (batchTaskShade == null) {
            return null;
        }

        setProject(vo,batchTaskShade);
        // 查询是否有绑定任务
        List<ScheduleTaskShade> taskShades = taskShadeService.findChildTaskRuleByTaskId(batchTaskShade.getTaskId(), batchTaskShade.getAppType());
        if (CollectionUtils.isNotEmpty(taskShades)) {
            // 绑定了规则任务
            vo.setExistsOnRule(Boolean.TRUE);
        } else {
            vo.setExistsOnRule(Boolean.FALSE);
        }

        //展示非工作流中的任务节点时，过滤掉工作流中的节点
        if (!isSubTask) {
            if (!vo.getFlowJobId().equals("0")) {
                return null;
            }
        }
        vo.setBatchTask(getTaskVo(batchTaskShade));
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            //移除不符合条件的jobjob
            filterJobJob(root, job, batchTaskShade);
            List<ScheduleJobVO> subJobVOs = new ArrayList<>(root.getChildren().size());
            List<ScheduleJobVO> taskRuleJobVOs = new ArrayList<>(root.getChildren().size());
            for (ScheduleJobJobDTO jobJobDTO : root.getChildren()) {
                com.dtstack.engine.master.vo.ScheduleJobVO subVO = getOffSpring(jobJobDTO, keyJobMap, idTaskMap, isSubTask);
                if (subVO != null) {
                    subVO.setRelyType(jobJobDTO.getRelyType());
                    if (TaskRuleEnum.NO_RULE.getCode().equals(subVO.getTaskRule())) {
                        subJobVOs.add(subVO);
                    } else {
                        taskRuleJobVOs.add(subVO);
                    }
                }
            }
            vo.setJobVOS(subJobVOs);
            vo.setTaskRuleJobVOS(taskRuleJobVOs);
        }
        return vo;
    }

    private void setProject(com.dtstack.engine.master.vo.ScheduleJobVO vo, ScheduleTaskShade batchTaskShade) {
        AuthProjectVO authProjectVO = projectService.finProject(batchTaskShade.getProjectId(), batchTaskShade.getAppType());
        if (authProjectVO != null) {
            vo.setProjectName(authProjectVO.getProjectName());
            vo.setProjectAlias(authProjectVO.getProjectAlias());
        }
    }


    private void filterJobJob(ScheduleJobJobDTO root, ScheduleJob job, ScheduleTaskShade batchTaskShade) {
        Iterator<ScheduleJobJobDTO> it = root.getChildren().iterator();
        while (it.hasNext()) {
            ScheduleJobJobDTO jobJob = it.next();
            // 4.0 getTaskIdFromJobKey 获取的是task_shade 的id
            if (batchTaskShade.getId().equals(batchJobService.getTaskShadeIdFromJobKey(jobJob.getJobKey()))) {
                //移除本身
                it.remove();
                continue;
            }
            String jobDayStr = batchJobService.getJobTriggerTimeFromJobKey(job.getJobKey());
            String jobJobDayStr = batchJobService.getJobTriggerTimeFromJobKey(jobJob.getJobKey());
            if (!jobDayStr.equals(jobJobDayStr)) {
                //如果执行时间不是同一天，移除
                it.remove();
            }
        }
    }


    private ScheduleTaskVO getTaskVo(ScheduleTaskShade batchTaskShade) {
        ScheduleTaskVO scheduleTaskVO = new ScheduleTaskVO(batchTaskShade, true);
        userService.fillUser(Lists.newArrayList(scheduleTaskVO));
        return scheduleTaskVO;
    }

    /**
     * 为工作流节点展开子节点
     */
    public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringWorkFlow( Long jobId, Integer appType) throws Exception {

        ScheduleJob job = batchJobService.getJobById(jobId);
        if(null == job){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        ScheduleTaskShade batchTaskShade = batchTaskShadeService.getBatchTaskById(job.getTaskId(),appType);
        if(null == batchTaskShade){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        vo.setBatchTask(new ScheduleTaskVO(batchTaskShade, true));
        if (batchTaskShade.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() || batchTaskShade.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
            try {
                com.dtstack.engine.master.vo.ScheduleJobVO subJobVO;
                //是否使用优化后的接口
                if(context.getUseOptimize()) {
                    subJobVO  = this.displayOffSpringForFlowWorkNew(job);
                }else{
                    subJobVO  = this.displayOffSpringForFlowWork(job);
                }
                vo.setSubNodes(subJobVO);
            } catch (Exception e) {
                LOGGER.error("get flow work subJob error", e);
            }
        }
        return vo;
    }

    private void getAllJobKeys(Map<Integer, List<ScheduleJobJob>> result, Set<String> jobKeys) {
        result.forEach((key, value) -> {
            for (ScheduleJobJob jobJob : value) {
                jobKeys.add(jobJob.getJobKey());
                jobKeys.add(jobJob.getParentJobKey());
            }
        });
    }

    /**
     * @author newman
     * @Description 向上展开工作实例，优化后接口
     * @Date 2021/1/6 7:22 下午
     * @param jobId:
     * @param level:
     * @return: com.dtstack.engine.master.vo.ScheduleJobVO
     **/
    public com.dtstack.engine.master.vo.ScheduleJobVO displayForefathersNew( Long jobId,  Integer level) throws Exception {

        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        checkLevel(level);
        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobsNew(job.getJobKey(), level, false);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, false);
        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);
        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<String, ScheduleTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);
        return getForefathers(root, keyJobMap, idTaskMap);
    }

    public com.dtstack.engine.master.vo.ScheduleJobVO displayForefathers( Long jobId,  Integer level) throws Exception {

        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        if (level == null || level < 1) {
            level = 1;
        }
        //如果是工作流子节点，则获取父节点的jobKey以便不展示工作流父节点
        String parentFlowJobJobKey = null;
        if (StringUtils.isNotEmpty(job.getFlowJobId()) && !job.getFlowJobId().equals(WORKFLOW_PARENT)) {
            ScheduleJob parentFlowJob = scheduleJobDao.getByJobId(job.getFlowJobId(), Deleted.NORMAL.getStatus());
            parentFlowJobJobKey = parentFlowJob.getJobKey();
        }

        // 递归获取level层的子节点
        Map<Integer, List<ScheduleJobJob>> result = getSpecifiedLevelJobJobs(job.getJobKey(), level, false, parentFlowJobJobKey);
        ScheduleJobJobDTO root = new ScheduleJobJobDTO();
        root.setJobKey(job.getJobKey());
        getTree(root, result, 1, false);

        Set<String> allJobKeys = new HashSet<>();
        allJobKeys.add(job.getJobKey());
        getAllJobKeys(result, allJobKeys);

        Map<String, ScheduleJob> keyJobMap = new HashMap<>();
        Map<String, ScheduleTaskShade> idTaskMap = new HashMap<>();
        getRelationData(allJobKeys, keyJobMap, idTaskMap);

        return getForefathersNew(root, keyJobMap, idTaskMap);
    }

    private com.dtstack.engine.master.vo.ScheduleJobVO getForefathersNew(ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap,
                                                                      Map<String, ScheduleTaskShade> idTaskMap) {
        ScheduleJob job = keyJobMap.get(root.getJobKey());
        if (job == null) {
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId()+"-"+job.getAppType());
        vo.setBatchTask(getTaskVo(batchTaskShade));
        if (StringUtils.isBlank(job.getJobKey())) {
            return vo;
        }
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            //移除自依赖
            root.getChildren().removeIf(jobJobDTO ->
                    batchJobService.getTaskShadeIdFromJobKey(jobJobDTO.getJobKey()).equals(batchTaskShade.getId()));
            if(CollectionUtils.isEmpty(root.getChildren())){
                return vo;
            }
            List<ScheduleJobVO> fatherVOs = new ArrayList<>();
            List<ScheduleJobVO> taskRuleJobVOs = new ArrayList<>();
            for (ScheduleJobJobDTO jobJobDTO : root.getChildren()) {
                com.dtstack.engine.master.vo.ScheduleJobVO item = this.getForefathersNew(jobJobDTO, keyJobMap, idTaskMap);
                if (item != null) {
                    if (TaskRuleEnum.NO_RULE.getCode().equals(item.getTaskRule())) {
                        fatherVOs.add(item);
                    } else {
                        taskRuleJobVOs.add(item);
                    }
                }
            }
            vo.setJobVOS(fatherVOs);
            vo.setTaskRuleJobVOS(taskRuleJobVOs);
        }
        return vo;
    }

    private com.dtstack.engine.master.vo.ScheduleJobVO getForefathers(ScheduleJobJobDTO root, Map<String, ScheduleJob> keyJobMap,
                                                                      Map<String, ScheduleTaskShade> idTaskMap) {
        ScheduleJob job = keyJobMap.get(root.getJobKey());
        if (job == null) {
            return null;
        }
        com.dtstack.engine.master.vo.ScheduleJobVO vo = new com.dtstack.engine.master.vo.ScheduleJobVO(job);
        ScheduleTaskShade batchTaskShade = idTaskMap.get(job.getTaskId() + "-" + job.getAppType());
        vo.setBatchTask(getTaskVo(batchTaskShade));
        if (StringUtils.isBlank(job.getJobKey())) {
            return vo;
        }

        List<ScheduleTaskShade> taskShades = taskShadeService.findChildTaskRuleByTaskId(batchTaskShade.getTaskId(), batchTaskShade.getAppType());
        if (CollectionUtils.isNotEmpty(taskShades)) {
            // 绑定了规则任务
            vo.setExistsOnRule(Boolean.TRUE);
        } else {
            vo.setExistsOnRule(Boolean.FALSE);
        }

        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(vo.getStatus())) {
            vo.setStatus(RdosTaskStatus.RUNNING.getStatus());
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            root.getChildren().removeIf(jobJobDTO ->
                    batchJobService.getTaskShadeIdFromJobKey(jobJobDTO.getJobKey()).equals(batchTaskShade.getId()));
//            root.getChildren().removeIf(jobJobDTO -> job.getTaskId().equals(batchJobService.getTaskIdFromJobKey(jobJobDTO.getJobKey())));
            if(CollectionUtils.isEmpty(root.getChildren())){
                return vo;
            }
            List<ScheduleJobVO> fatherVOs = new ArrayList<>();
            List<ScheduleJobVO> taskRuleJobVOs = new ArrayList<>();

            for (ScheduleJobJobDTO jobJobDTO : root.getChildren()) {
                com.dtstack.engine.master.vo.ScheduleJobVO item = this.getForefathers(jobJobDTO, keyJobMap, idTaskMap);
                if (item != null) {
                    item.setRelyType(jobJobDTO.getRelyType());
                    if (TaskRuleEnum.NO_RULE.getCode().equals(item.getTaskRule())) {
                        fatherVOs.add(item);
                    } else {
                        taskRuleJobVOs.add(item);
                    }
                }
            }
            vo.setJobVOS(fatherVOs);
            vo.setTaskRuleJobVOS(taskRuleJobVOs);
        }
        return vo;
    }

    public List<ScheduleJobJob> getJobChild(String parentJobKey) {
        return scheduleJobJobDao.listByParentJobKey(parentJobKey,RelyTypeEnum.NORMAL.getType());
    }

    public List<ScheduleJobJob> getJobChild(Set<String> parentJobKey) {
        return scheduleJobJobDao.listByParentJobKeys(parentJobKey);
    }

    public int batchInsert(List<ScheduleJobJob> scheduleJobJobs) {
        return scheduleJobJobDao.batchInsert(scheduleJobJobs);
    }

    public ParentNodesVO parentNode(String jobId, String taskName) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());
        ParentNodesVO vo = new ParentNodesVO();
        if (scheduleJob != null) {
            vo.setJobId(scheduleJob.getJobId());
            vo.setJobKey(scheduleJob.getJobKey());
            vo.setName(taskShadeService.getTaskNameByJobKey(scheduleJob.getJobKey(), scheduleJob.getAppType()));

            List<ParentJobVO> parentJobVOS = Lists.newArrayList();
            // 查询上游关系
            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey(), null);
            if (CollectionUtils.isNotEmpty(scheduleJobJobs)) {
                Map<String, List<ScheduleJobJob>> parentJobKeyMaps = scheduleJobJobs.stream().collect(Collectors.groupingBy(ScheduleJobJob::getParentJobKey));
                Set<String> parentJobKeys = parentJobKeyMaps.keySet();
                List<Long> ids = TaskUtils.dissectJobKeys(parentJobKeys);
                // 查询父实例
                List<ScheduleJob> parentJobs = scheduleJobDao.listJobByJobKeys(parentJobKeys);
                Map<Long, List<ScheduleJob>> parentJobMaps = parentJobs.stream().collect(Collectors.groupingBy(job -> TaskUtils.dissectJobKey(job.getJobKey())));
                // 查询父任务
                List<ScheduleTaskShade> taskShades = taskShadeService.listByIdsAndTaskName(ids,taskName);
                for (ScheduleTaskShade taskShade : taskShades) {
                    List<ScheduleJob> jobs = parentJobMaps.get(taskShade.getId());
                    if (CollectionUtils.isNotEmpty(jobs)) {
                        for (ScheduleJob job : jobs) {
                            List<ScheduleJobJob> jobJobs = parentJobKeyMaps.get(job.getJobKey());
                            if (CollectionUtils.isNotEmpty(jobJobs)) {
                                ParentJobVO parentJobVO = new ParentJobVO();
                                parentJobVO.setName(taskShade.getName());
                                parentJobVO.setTaskType(taskShade.getTaskType());
                                parentJobVO.setAppType(taskShade.getAppType());
                                parentJobVO.setStatus(job.getStatus());
                                parentJobVO.setCycTime(job.getCycTime());
                                parentJobVO.setJobId(job.getJobId());
                                parentJobVO.setJobKey(job.getJobKey());
                                parentJobVO.setRelyType(jobJobs.get(0).getRelyType());
                                parentJobVOS.add(parentJobVO);
                            }
                        }
                    }
                }
            }
            vo.setParentJobVOS(parentJobVOS);
        }
        return vo;
    }

    public RelyResultVO removeRely(String relyJobKey, List<String> relyJobParentKeys, Long operateId) {
        RelyResultVO vo = new RelyResultVO();
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getByJobKey(relyJobKey);
            if (scheduleJob == null) {
                vo.setSuccess(Boolean.FALSE);
                vo.setMsg("去依赖异常");
                return vo;
            }

            List<String> taskName = taskShadeService.getTaskNameByJobKeys(relyJobParentKeys);
            if (CollectionUtils.isNotEmpty(taskName)) {
                scheduleJobOperateService.addScheduleJobOperate(scheduleJob.getJobId(), OperateTypeEnum.RELY.getCode(),String.format(OperateTypeEnum.RELY.getMsg(),Joiner.on(",").join(taskName)),operateId);
            }

            if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(scheduleJob.getStatus()) &&
                    !RdosTaskStatus.PARENTFAILED.getStatus().equals(scheduleJob.getStatus())) {
                ScheduleTaskShade task = taskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
                vo.setSuccess(Boolean.FALSE);
                String msg = TaskConstant.RELY_TASK_STATUS_ERROR;
                vo.setMsg(String.format(msg,task.getName(),RdosTaskStatus.getCode(scheduleJob.getStatus())));
                return vo;
            }

            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(relyJobKey, RelyTypeEnum.NORMAL.getType());

            // 过滤已经去依赖的上游
            scheduleJobJobs = scheduleJobJobs.stream().filter(scheduleJobJob ->
                            relyJobParentKeys.contains(scheduleJobJob.getParentJobKey()))
                    .collect(Collectors.toList());

            // 过滤出状态不符合去依赖的实例
            List<String> parentJobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getParentJobKey)
                    .collect(Collectors.toList());
            List<String> isFinishedParentKeys = Lists.newArrayList();
            List<String> noFinishedParentKeys = filterJob(vo, parentJobKeys,isFinishedParentKeys);

            if (CollectionUtils.isNotEmpty(noFinishedParentKeys)) {
                scheduleJobJobDao.removeRely(relyJobKey, noFinishedParentKeys, RelyTypeEnum.TO_RELY_ON.getType(), RelyTypeEnum.NORMAL.getType());
                handlerBaseline(scheduleJob);
            }

            vo.setSuccess(Boolean.TRUE);
            vo.setRelySuccessJob(noFinishedParentKeys);
            vo.setRelyFailJob(isFinishedParentKeys);
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            vo.setSuccess(Boolean.FALSE);
            vo.setMsg("去依赖异常");
            return vo;
        }
    }

    private void handlerBaseline(ScheduleJob scheduleJob) {
        List<BaselineJobJob> baselineJobJobList = baselineJobJobDao.selectBaselineByJobId(scheduleJob.getJobId());

        if (CollectionUtils.isNotEmpty(baselineJobJobList)) {
            List<Long> baselineJobIds = baselineJobJobList.stream().map(BaselineJobJob::getBaselineJobId).collect(Collectors.toList());
            baselineJobService.againCreateBaseJob(new HashSet<Long>(baselineJobIds));
        }
    }

    private List<String> filterJob(RelyResultVO vo, List<String> parentJobKeys,List<String> isFinishedParentKeys) {
        List<String> noFinishedParentKeys = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(parentJobKeys)) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(parentJobKeys);
            for (ScheduleJob job : scheduleJobs) {
                if (RdosTaskStatus.FINISHED.getStatus().equals(job.getStatus())) {
                    isFinishedParentKeys.add(job.getJobKey());
                } else {
                    noFinishedParentKeys.add(job.getJobKey());
                }
            }

            if (CollectionUtils.isNotEmpty(isFinishedParentKeys)) {
                List<Long> ids = TaskUtils.dissectJobKeys(isFinishedParentKeys);
                List<ScheduleTaskShade> taskShades = taskShadeService.listByIdsAndTaskName(ids, null);
                StringBuilder msg = new StringBuilder("以下任务已运行成功，无须去依赖：");
                for (ScheduleTaskShade taskShade : taskShades) {
                    msg.append(taskShade.getName()).append(",");
                }
                vo.setMsg(msg.toString());
            }
        }
        return noFinishedParentKeys;
    }
    public FindParentJobVO findParentJob(String jobId) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());

        if (scheduleJob == null) {
            throw new RdosDefineException("job does not exist");
        }
        FindParentJobVO vo = new FindParentJobVO();
        vo.setJobId(jobId);
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey(), RelyTypeEnum.NORMAL.getType());

        if (CollectionUtils.isEmpty(scheduleJobJobs)) {
            return vo;
        }

        List<String> jobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getParentJobKey).collect(Collectors.toList());
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(jobKeys);
        List<ParentJobStatusVO> parentJobVOS = Lists.newArrayList();

        for (ScheduleJob job : scheduleJobs) {
            ParentJobStatusVO parentJobVO = new ParentJobStatusVO();
            parentJobVO.setJobId(job.getJobId());
            parentJobVO.setStatus(job.getStatus());
            parentJobVOS.add(parentJobVO);
        }
        vo.setParentJobVOS(parentJobVOS);
        return vo;
    }

    public JobViewVO view(List<String> jobIds, Integer level, Integer directType, boolean showSelf) {
        JobViewVO jobViewVO = new JobViewVO();
        if (CollectionUtils.isEmpty(jobIds)) {
            throw new RdosDefineException("查询视图必须jobId");
        }

        if (level == null || level < 1) {
            level = 1;
        }

        if( level > context.getJobJobLevel()){
            level = context.getJobJobLevel();
        }

        if (directType == null) {
            directType = DisplayDirect.FATHER.getType();
        }

        List<JobViewElementVO> jobViewElementVOS = Lists.newArrayList();
        List<JobViewSideVO> jobViewSideVOS = Lists.newArrayList();

        List<ScheduleJob> scheduleJobs = scheduleJobDao.listByJobIdList(jobIds, null);
        jobViewElementVOS.addAll(buildJobViewElementVO(scheduleJobs));


        while (level > 0 && CollectionUtils.isNotEmpty(scheduleJobs)) {
            List<ScheduleJobJob> scheduleJobJobs = null;
            List<String> jobKeys = scheduleJobs.stream().filter(scheduleJob -> "0".equals(scheduleJob.getFlowJobId())).map(ScheduleJob::getJobKey).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(jobKeys)){
                break;
            }
            List<String> nextJobKeys = null;
            if (DisplayDirect.FATHER.getType().equals(directType)) {
                scheduleJobJobs = scheduleJobJobDao.listByJobKeys(jobKeys, null);
                if(!showSelf){
                    scheduleJobJobs = filterSelf(scheduleJobJobs);
                }
                //过滤掉不满足条件的jobKey
                nextJobKeys = scheduleJobJobs.stream()
                        .map(ScheduleJobJob::getParentJobKey)
                        .collect(Collectors.toList());

            } else if (DisplayDirect.CHILD.getType().equals(directType)) {
                scheduleJobJobs = scheduleJobJobDao.listByParentJobKeys(jobKeys);
                if(!showSelf){
                    scheduleJobJobs = filterSelf(scheduleJobJobs);
                }
                nextJobKeys = scheduleJobJobs.stream()
                        .map(ScheduleJobJob::getJobKey)
                        .collect(Collectors.toList());
            }

            if (CollectionUtils.isEmpty(scheduleJobJobs)) {
                break;
            }

            jobViewSideVOS.addAll(scheduleJobStruct.toJobViewSideVO(scheduleJobJobs));
            scheduleJobs = scheduleJobDao.listJobByJobKeys(nextJobKeys);
            jobViewSideVOS.addAll(scheduleJobStruct.toJobViewSideVO(scheduleJobJobs));

            // 过滤工作流子节点和规则任务和自依赖任务
            scheduleJobs = scheduleJobs.stream().filter(scheduleJob ->
                    "0".equals(scheduleJob.getFlowJobId()) &&
                    TaskRuleEnum.NO_RULE.getCode().equals(scheduleJob.getTaskRule())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(scheduleJobs)) {
                jobViewElementVOS.addAll(buildJobViewElementVO(scheduleJobs));
            }

            level--;
        }

        for (JobViewElementVO jobViewElementVO : jobViewElementVOS) {
            List<ScheduleTaskShade> taskShades = taskShadeService.findChildTaskRuleByTaskId(jobViewElementVO.getTaskId(), jobViewElementVO.getAppType());
            if (CollectionUtils.isNotEmpty(taskShades)) {
                // 绑定了规则任务
                jobViewElementVO.setExistsOnRule(Boolean.TRUE);
            } else {
                jobViewElementVO.setExistsOnRule(Boolean.FALSE);
            }
        }

        jobViewVO.setJobViewSideVOS(jobViewSideVOS);
        jobViewVO.setJobViewElementVOS(jobViewElementVOS);
        return jobViewVO;
    }

    private List<ScheduleJobJob> filterSelf(List<ScheduleJobJob> scheduleJobJobs) {
        List<ScheduleJobJob> scheduleJobJobTaskDTOS = Lists.newArrayList();
        for (ScheduleJobJob scheduleJobJob : scheduleJobJobs) {
            String jobKey = scheduleJobJob.getJobKey();
            String parentJobKey = scheduleJobJob.getParentJobKey();

            Long taskId = JobKeyUtils.getTaskShadeIdFromJobKey(jobKey);
            Long parentTaskId = JobKeyUtils.getTaskShadeIdFromJobKey(parentJobKey);

            if (taskId != -1 && parentTaskId != -1 && taskId.equals(parentTaskId)) {
                // 说明是子依赖
                continue;
            }
            scheduleJobJobTaskDTOS.add(scheduleJobJob);
        }

        return scheduleJobJobTaskDTOS;
    }

    private List<JobViewElementVO> buildJobViewElementVO(List<ScheduleJob> scheduleJobs) {
        List<JobViewElementVO> jobViewElementVOS = Lists.newArrayList();

        Map<Integer, List<Long>> taskInfoMap = scheduleJobs.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType,
                Collectors.mapping(ScheduleJob::getTaskId, Collectors.toList())));

        List<ScheduleTaskShade> taskLists = Lists.newArrayList();
        for (Integer appType : taskInfoMap.keySet()) {
            List<Long> nextTaskIds = taskInfoMap.get(appType);
            List<ScheduleTaskShade> tasks = taskShadeService.getTaskByIds(nextTaskIds,null, appType);
            if (CollectionUtils.isNotEmpty(tasks)) {
                taskLists.addAll(tasks);
            }
        }

        Map<String, String> taskMaps = taskLists.stream().collect(Collectors.
                toMap(taskShade -> taskShade.getTaskId() + "-" + taskShade.getAppType(), ScheduleTask::getName));
        for (ScheduleJob scheduleJob : scheduleJobs) {
            JobViewElementVO jobViewElementVO = scheduleJobStruct.toJobViewElementVO(scheduleJob);
            jobViewElementVO.setTaskName(taskMaps.get(scheduleJob.getTaskId()+"-"+scheduleJob.getAppType()));

            if ("0".equals(jobViewElementVO.getFlowJobId())) {
                jobViewElementVO.setFlowJobId(null);
            }
            jobViewElementVOS.add(jobViewElementVO);
        }

        return jobViewElementVOS;
    }

    public void deleteJobJobsByParentJobKey( List<String> jobKeyList) {

        if(CollectionUtils.isEmpty(jobKeyList)){
            return;
        }
        scheduleJobJobDao.deleteByParentJobKey(jobKeyList);
    }

    public WorkflowJobViewReturnVO workflowSubViewList(String jobId) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());

        if (scheduleJob == null) {
            throw new RdosDefineException("jobId:" + jobId + " job absent");
        }

        String flowSubId = jobId;
        if (!EScheduleJobType.WORK_FLOW.getType().equals(scheduleJob.getTaskType())) {
            flowSubId = scheduleJob.getFlowJobId();
        }

        if ("0".equals(flowSubId)) {
            // 非工作流任务
            throw new RdosDefineException("jobId:" + jobId + " must workflow task");
        }

        WorkflowJobViewReturnVO workflowJobViewReturnVO = new WorkflowJobViewReturnVO();
        workflowJobViewReturnVO.setJobId(flowSubId);
        JobViewVO jobViewVO = new JobViewVO();
        List<ScheduleJob> jobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(flowSubId));
        List<JobViewElementVO> jobViewElementVOS = Lists.newArrayList();
        List<String> jobKeys = Lists.newArrayList();
        for (ScheduleJob job : jobs) {
            JobViewElementVO jobViewElementVO = new JobViewElementVO();
            jobViewElementVO.setId(job.getId());
            jobViewElementVO.setProjectId(job.getProjectId());
            jobViewElementVO.setJobId(job.getJobId());
            jobViewElementVO.setJobKey(job.getJobKey());
            jobViewElementVO.setStatus(RdosTaskStatus.getShowStatusWithoutStop(job.getStatus()));
            jobViewElementVO.setAppType(job.getAppType());
            jobViewElementVO.setTaskId(job.getTaskId());
            jobViewElementVO.setTaskName(job.getJobName());
            jobViewElementVO.setTaskRule(job.getTaskRule());
            jobViewElementVO.setTaskType(job.getTaskType());
            jobViewElementVO.setFlowJobId(job.getFlowJobId());
            jobViewElementVOS.add(jobViewElementVO);

            jobKeys.add(job.getJobKey());
        }
        jobViewVO.setJobViewElementVOS(jobViewElementVOS);

        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKeys(jobKeys, null);
        List<JobViewSideVO> jobViewSideVOS = Lists.newArrayList();
        for (ScheduleJobJob scheduleJobJob : scheduleJobJobs) {
            if (!scheduleJobJob.getParentJobKey().equals(scheduleJob.getJobKey())) {
                JobViewSideVO jobViewSideVO = new JobViewSideVO();
                jobViewSideVO.setJobKey(scheduleJobJob.getJobKey());
                jobViewSideVO.setParentJobKey(scheduleJobJob.getParentJobKey());
                jobViewSideVOS.add(jobViewSideVO);
            }
        }
        jobViewVO.setJobViewSideVOS(jobViewSideVOS);
        workflowJobViewReturnVO.setJobViewVO(jobViewVO);
        return workflowJobViewReturnVO;
    }
}