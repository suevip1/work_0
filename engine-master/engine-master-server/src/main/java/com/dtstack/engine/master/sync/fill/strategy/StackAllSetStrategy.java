package com.dtstack.engine.master.sync.fill.strategy;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/7/7 10:14 AM
 * @Email: dazhi@dtstack.com
 * @Description: 堆栈法求全集合
 */
public class StackAllSetStrategy extends AbstractAllSetStrategy {
    private final static Logger LOGGER = LoggerFactory.getLogger(StackAllSetStrategy.class);

    private static StackAllSetStrategy stackAllSetStrategy;

    private final ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    private final EnvironmentContext environmentContext;


    private StackAllSetStrategy(ScheduleTaskTaskShadeService scheduleTaskTaskShadeService, EnvironmentContext environmentContext) {
        this.scheduleTaskTaskShadeService = scheduleTaskTaskShadeService;
        this.environmentContext = environmentContext;
    }

    public synchronized static StackAllSetStrategy getInstance(ApplicationContext applicationContext) {
        if (stackAllSetStrategy == null) {
            ScheduleTaskTaskShadeService scheduleTaskTaskShadeService = applicationContext.getBean(ScheduleTaskTaskShadeService.class);
            EnvironmentContext environmentContext = applicationContext.getBean(EnvironmentContext.class);
            stackAllSetStrategy = new StackAllSetStrategy(scheduleTaskTaskShadeService,environmentContext);
        }
        return stackAllSetStrategy;
    }

    @Override
    public Set<String> getAllList(Set<String> run) {
        Set<String> all = Sets.newHashSet(run);
        String md5String = MD5Util.getMd5String(JSON.toJSONString(run));
        if (run.size() ==1) {
            // R集合只有一个元素，其实也不用遍历计算有效路径
            LOGGER.info("run size 1,end fillList method");
            return all;
        }

        // 计算中使用的栈
        Map<String, DAGNode> nodeMap = initDAGNode(run);

        Set<String> passingAll = Sets.newHashSet();
        Set<String> endNode = findAndClearEndPath(nodeMap,passingAll,md5String);
        passingAll.addAll(endNode);
        List<ScheduleTaskTaskShadeDTO> childTaskKeys = selectDbSonTaskKey(endNode);
        int count = 0;
        while (CollectionUtils.isNotEmpty(endNode)) {
            LOGGER.info("{} run:{} count:{}",md5String,endNode,count++);
            Map<String, List<ScheduleTaskTaskShadeDTO>> taskTaskMap = childTaskKeys.stream()
                    .collect(Collectors.groupingBy(ScheduleTaskTaskShadeDTO::getParentTaskKey));
            for (DAGNode dagNode : nodeMap.values()) {
                List<DAGPath> paths = dagNode.getPaths();
                List<DAGPath> cpPaths = Lists.newArrayList();
                List<DAGPath> finishPaths = dagNode.getFinishPaths();
                for (DAGPath path : paths) {
                    fillingCpPaths(run, taskTaskMap, cpPaths, finishPaths,path,md5String);
                }

                dagNode.setPaths(cpPaths);
                paths.clear();
            }

            // 清除重复节点
            endNode = findAndClearEndPath(nodeMap,passingAll,md5String);
            childTaskKeys = selectDbSonTaskKey(endNode);
        }

        for (DAGNode node : nodeMap.values()) {
            List<DAGPath> paths = node.getFinishPaths();
            for (DAGPath dagPath : paths) {
                all.addAll(dagPath.getRTaskKeys());
            }
        }

        for (DAGNode node : nodeMap.values()) {
            List<DAGPath> paths = node.getFinishPaths();

            for (DAGPath path : paths) {
                if (path.getInterruptMark() && all.contains(path.getEnd())) {
                    all.addAll(path.getTaskKeys());
                }
            }
        }

        return all;
    }

    private void fillingCpPaths(Set<String> run,
                                Map<String, List<ScheduleTaskTaskShadeDTO>> taskTaskMap,
                                List<DAGPath> cpPaths,
                                List<DAGPath> finishPaths,
                                DAGPath path,String md5String) {
        String end = path.getEnd();
        List<ScheduleTaskTaskShadeDTO> scheduleTaskTaskShadeDTOS = taskTaskMap.get(end);

        if (CollectionUtils.isNotEmpty(scheduleTaskTaskShadeDTOS)) {
            if (scheduleTaskTaskShadeDTOS.size() == 1) {
                ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO = scheduleTaskTaskShadeDTOS.get(0);
                addNode(run, path, scheduleTaskTaskShadeDTO);
                cpPaths.add(path);
            } else {
                for (ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO : scheduleTaskTaskShadeDTOS) {
                    DAGPath cpDagPath = new DAGPath(path);
                    addNode(run, cpDagPath, scheduleTaskTaskShadeDTO);
                    cpPaths.add(cpDagPath);
                }
            }
        } else {
            // 这调链表已经完成，设置完成状态
            finishPaths.add(path);
            LOGGER.info("{} path finish {}",md5String,JSON.toJSONString(path));
        }
    }

    private List<ScheduleTaskTaskShadeDTO> selectDbSonTaskKey(Set<String> endNode) {
        List<ScheduleTaskTaskShadeDTO> childTaskKeys = Lists.newArrayList();
        List<String> needFindChildTaskKeyList = Lists.newArrayList(endNode);
        Integer fillDataLimitSize = environmentContext.getFillDataLimitSize();

        List<List<String>> needFindChildTaskKeyListPartitions = Lists.partition(needFindChildTaskKeyList, fillDataLimitSize);
        for (List<String> needFindChildTaskKeyListPartition : needFindChildTaskKeyListPartitions) {
            List<ScheduleTaskTaskShadeDTO> taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(needFindChildTaskKeyListPartition);
            if (CollectionUtils.isNotEmpty(taskTaskShadeDTOS)) {
                childTaskKeys.addAll(taskTaskShadeDTOS);
            }
        }
        return childTaskKeys;
    }

    private void addNode(Set<String> run, DAGPath path, ScheduleTaskTaskShadeDTO scheduleTaskTaskShadeDTO) {
        path.setEnd(scheduleTaskTaskShadeDTO.getTaskKey());
        if (run.contains(scheduleTaskTaskShadeDTO.getTaskKey())) {
            path.setREnd(scheduleTaskTaskShadeDTO.getTaskKey());
            path.addChildRTaskKey(scheduleTaskTaskShadeDTO.getTaskKey());
        } else {
            path.addChildTaskKey(scheduleTaskTaskShadeDTO.getTaskKey());
        }
    }

    private Set<String> findAndClearEndPath(Map<String, DAGNode> nodeMap,Set<String> passingAll,String md5String) {
        Set<String> endPathNode = Sets.newHashSet();
        for (DAGNode dagNode : nodeMap.values()) {
            List<DAGPath> paths = dagNode.getPaths();
            List<DAGPath> cpPaths = Lists.newArrayList();
            List<DAGPath> finishPaths = dagNode.getFinishPaths();
            for (DAGPath path : paths) {
                String end = path.getEnd();

                if (passingAll.contains(end)) {
                    // 说明已经向下查询了，所以这条路径不在向下查询
                    path.setInterruptMark(Boolean.TRUE);
                    finishPaths.add(path);
                    LOGGER.info("{} path interrupt {}",md5String,JSON.toJSONString(path));
                } else {
                    passingAll.add(end);
                    endPathNode.add(end);
                    cpPaths.add(path);
                }
            }
            dagNode.setPaths(cpPaths);
        }

        return endPathNode;
    }

    private Map<String, DAGNode> initDAGNode(Set<String> run) {
        Map<String, DAGNode> nodeMap = Maps.newHashMap();

        for (String aimNode : run) {
            DAGNode dagNode = new DAGNode();
            dagNode.setAimNode(aimNode);
            DAGPath dagPath = new DAGPath();
            dagPath.setTop(aimNode);
            dagPath.setEnd(aimNode);
            dagPath.setRTop(aimNode);
            dagPath.setREnd(aimNode);
            dagPath.setRTaskKeys(Lists.newArrayList(aimNode));
            dagNode.setPaths(Lists.newArrayList(dagPath));
            nodeMap.put(aimNode,dagNode);
            LOGGER.info("init aimNode:{} success",aimNode);
        }
        return nodeMap;
    }

}
