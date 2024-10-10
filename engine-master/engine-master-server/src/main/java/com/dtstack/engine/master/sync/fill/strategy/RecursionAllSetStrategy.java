package com.dtstack.engine.master.sync.fill.strategy;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/7/7 10:18 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RecursionAllSetStrategy extends AbstractAllSetStrategy {

    private final static Logger LOGGER = LoggerFactory.getLogger(RecursionAllSetStrategy.class);

    private final ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    private final EnvironmentContext environmentContext;

    private static RecursionAllSetStrategy recursionAllSetStrategy;

    private RecursionAllSetStrategy(ScheduleTaskTaskShadeService scheduleTaskTaskShadeService, EnvironmentContext environmentContext) {
        if (recursionAllSetStrategy != null) {
            throw new RdosDefineException("recursionAllSetStrategy is exist，not create recursionAllSetStrategy");
        }

        this.scheduleTaskTaskShadeService = scheduleTaskTaskShadeService;
        this.environmentContext = environmentContext;
    }

    public synchronized static RecursionAllSetStrategy getInstance(ApplicationContext applicationContext) {
        if (recursionAllSetStrategy == null) {
            ScheduleTaskTaskShadeService scheduleTaskTaskShadeService = applicationContext.getBean(ScheduleTaskTaskShadeService.class);
            EnvironmentContext environmentContext = applicationContext.getBean(EnvironmentContext.class);
            recursionAllSetStrategy = new RecursionAllSetStrategy(scheduleTaskTaskShadeService,environmentContext);
        }

        return recursionAllSetStrategy;
    }


    @Override
    public Set<String> getAllList(Set<String> run) {
        Set<String> all = Sets.newHashSet(run);

        if (run.size() ==1) {
            // R集合只有一个元素，其实也不用遍历计算有效路径
            LOGGER.info("run size 1,end fillList method");
            return all;
        }

        // 获得R集合所在的dag图的所有边
        Map<String, List<String>> nodeSide = getNodeSideByRun(run);

        LOGGER.info("run:{} nodeSide:{} ",run,nodeSide);
        // 开始查询有效路径
        Collection<DAGNode> nodes = getDAGNodeByRun(run,nodeSide);

        // 封装节点信息
        for (DAGNode node : nodes) {
            List<DAGPath> paths = node.getPaths();
            for (DAGPath dagPath : paths) {
                all.addAll(dagPath.getRTaskKeys());
            }
        }
        return all;
    }

    /**
     * 查询R集合所在的dag图的所有边
     *
     * @param run R集合
     * @return nodeSide
     */
    private Map<String, List<String>> getNodeSideByRun(Set<String> run) {
        Map<String, List<String>> nodeSide = Maps.newHashMap();
        Integer fillDataLimitSize = environmentContext.getFillDataLimitSize();

        List<String> needFindChildTaskKeyList = Lists.newArrayList(run);
        while (CollectionUtils.isNotEmpty(needFindChildTaskKeyList)) {
            List<String> childTaskKeys = Lists.newArrayList();
            // 切割,防止runList太大
            List<List<String>> needFindChildTaskKeyListPartitions = Lists.partition(needFindChildTaskKeyList, fillDataLimitSize);
            needFindChildTaskKeyListPartitions.forEach(needFindChildTaskKeyListPartition->childTaskKeys.addAll(getListChildTaskKeyAndFillNodeSide(needFindChildTaskKeyListPartition,nodeSide)));
            needFindChildTaskKeyList = childTaskKeys;
        }

        return nodeSide;
    }

    /**
     * 获得RunList的下游taskKey和填充nodeSide
     *
     * @param needFindChildTaskKeyList 需要查询下游的节点的集合列表
     * @param nodeSide 边的关系映射
     * @return
     */
    private List<String> getListChildTaskKeyAndFillNodeSide(List<String> needFindChildTaskKeyList,Map<String, List<String>> nodeSide) {
        List<String> childTaskKeys = Lists.newArrayList();
        List<ScheduleTaskTaskShadeDTO> taskTaskShadeDTOS = scheduleTaskTaskShadeService.listChildByTaskKeys(needFindChildTaskKeyList);

        if (CollectionUtils.isNotEmpty(taskTaskShadeDTOS)) {
            Map<String, List<ScheduleTaskTaskShadeDTO>> keyMaps = taskTaskShadeDTOS.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShadeDTO::getParentTaskKey));

            for (Map.Entry<String, List<ScheduleTaskTaskShadeDTO>> entry : keyMaps.entrySet()) {
                String key = entry.getKey();
                if (!nodeSide.containsKey(key)) {
                    List<ScheduleTaskTaskShadeDTO> value = entry.getValue();
                    List<String> childValueTaskKeys = value.stream().map(ScheduleTaskTaskShadeDTO::getTaskKey).collect(Collectors.toList());
                    nodeSide.put(key, childValueTaskKeys);
                    childTaskKeys.addAll(childValueTaskKeys);
                }
            }
        }
        return childTaskKeys;
    }

    /**
     * 初始化路径
     * @param aimNode
     * @return
     */
    private DAGPath initPath(String aimNode) {
        DAGPath dagPath = new DAGPath();
        dagPath.setTop(aimNode);
        dagPath.setRTop(aimNode);
        dagPath.setRTaskKeys(Lists.newArrayList(aimNode));
        return dagPath;
    }

    /**
     * 查询有效路径
     *
     * @param run 运行集合
     * @param nodeSide 边集合
     * @return
     */
    private Collection<DAGNode> getDAGNodeByRun(Set<String> run, Map<String, List<String>> nodeSide) {
        Set<String> validPathTaskKey = Sets.newHashSet();
        Map<String, DAGNode> dagNodes = Maps.newHashMap();
        for (String aimNode : run) {
            if (!validPathTaskKey.contains(aimNode)) {
                DAGNode dagNode = new DAGNode();
                dagNode.setAimNode(aimNode);
                DAGPath dagPath = initPath(aimNode);
                dagNode.setPaths(fillDAGPaths(aimNode,dagPath,validPathTaskKey,dagNodes,run,nodeSide));
                validPathTaskKey.add(aimNode);
                dagNodes.put(aimNode,dagNode);
            }
        }

        return dagNodes.values();
    }

    public List<DAGPath> fillDAGPaths(String aimPath, DAGPath dagPath,
                                      Set<String> validPathTaskKey, Map<String, DAGNode> dagNodes,
                                      Set<String> run, Map<String, List<String>> nodeSide) {
        LOGGER.info("{} start fillDAGPath ",aimPath);
        List<String> childTaskKeys = nodeSide.get(aimPath);
        List<DAGPath> paths = Lists.newArrayList();
        if (CollectionUtils.isEmpty(childTaskKeys)) {
            // 遍历到子节点，说明该路径已经遍历结束，判断这条路径是否有效
            LOGGER.info("{} end dagPath:{}",aimPath, JSON.toJSONString(dagPath));
            if (dagPath.getREnd() != null) {
                paths.add(dagPath);
            }
        } else if (childTaskKeys.size() == 1) {
            // 一个节点，直接复用原来的path
            String childTaskKey = childTaskKeys.get(0);
            fillPaths(dagPath, validPathTaskKey, dagNodes, run, nodeSide, childTaskKey, paths);
        } else {
            // 2个或者两个以上节点，复制path
            for (String childTaskKey : childTaskKeys) {
                DAGPath cpDagPath = new DAGPath(dagPath);
                fillPaths(cpDagPath, validPathTaskKey, dagNodes, run, nodeSide, childTaskKey, paths);
            }
        }

        return paths;
    }

    private void fillPaths(DAGPath dagPath,
                           Set<String> validPathTaskKey,
                           Map<String, DAGNode> dagNodes,
                           Set<String> run,
                           Map<String, List<String>> nodeSide,
                           String childTaskKey,
                           List<DAGPath> paths) {
        if (run.contains(childTaskKey)) {
            // child属于R集合元素，
            // 第一点说明最外层遍历不需要遍历这个节点，且path有了Rend节点，
            // 第二点如果这个节点前面已经遍历过，那么久可以拿来直接用，而不需要遍历
            validPathTaskKey.add(childTaskKey);
            dagPath.addChildRTaskKey(childTaskKey);
            DAGNode childNode = dagNodes.get(childTaskKey);
            if (childNode == null) {
                // childNode是空的，说明前面没有对这个节点遍历过，那么需要继续递归遍历这个节点
                dagPath.setREnd(childTaskKey);
                dagPath.setEnd(childTaskKey);
                paths.addAll(fillDAGPaths(childTaskKey, dagPath, validPathTaskKey, dagNodes, run, nodeSide));
            } else {
                // 该节点的树已经遍历完了，合并树操作
                margePath(dagPath, paths, childNode);
            }
        } else {
            // child不属于R集合，继续往下递归
            dagPath.addChildTaskKey(childTaskKey);
            dagPath.setEnd(childTaskKey);
            paths.addAll(fillDAGPaths(childTaskKey, dagPath, validPathTaskKey, dagNodes, run, nodeSide));
        }
    }

    private void margePath(DAGPath dagPath, List<DAGPath> paths, DAGNode childNode) {
        List<DAGPath> pathsChildNodes = childNode.getPaths();
        if (CollectionUtils.isEmpty(pathsChildNodes)) {
            // 说明这棵树是单节点,可以直接停止
            dagPath.setEnd(childNode.getAimNode());
            dagPath.setREnd(childNode.getAimNode());
            paths.add(dagPath);
        } else {
            for (DAGPath pathsChildNode : pathsChildNodes) {
                pathsChildNode.setTop(dagPath.getTop());
                pathsChildNode.setRTop(dagPath.getRTop());
                pathsChildNode.addChildRTaskKeys(dagPath.getRTaskKeys());
            }
            paths.addAll(pathsChildNodes);
        }
    }


}
