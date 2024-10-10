package com.dtstack.engine.master.sync;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dao.ScheduleTaskTaskShadeDao;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author leon
 * @date 2023-03-03 11:18
 **/
public class ForkJoinTaskShadeTask extends RecursiveTask<Set<String>> {

    private final Long taskId;

    private final Integer appType;

    private final ScheduleTaskShadeDao scheduleTaskShadeDao;

    private final ScheduleTaskTaskShadeDao scheduleTaskTaskShadeDao;

    private final ConcurrentHashSet<String> results;

    public ForkJoinTaskShadeTask(Long taskId,
                                 Integer appType,
                                 ScheduleTaskShadeDao scheduleTaskShadeDao,
                                 ScheduleTaskTaskShadeDao scheduleTaskTaskShadeDao,
                                 ConcurrentHashSet<String> results) {
        this.taskId = taskId;
        this.appType = appType;
        this.scheduleTaskShadeDao = scheduleTaskShadeDao;
        this.scheduleTaskTaskShadeDao = scheduleTaskTaskShadeDao;
        this.results = results;
    }

    @Override
    protected Set<String> compute() {
        ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeDao.getSimpleByTaskIdAndAppType(taskId, appType);
        if (Objects.isNull(scheduleTaskShade)) {
            return null;
        }
        List<ForkJoinTaskShadeTask> tasks = new ArrayList<>();
        List<ScheduleTaskTaskShade> childTaskTask = scheduleTaskTaskShadeDao.listChildTask(taskId, appType);

        for (ScheduleTaskTaskShade child : childTaskTask) {
            if (results.contains(child.getTaskKey())) {
                return null;
            }
            results.add(child.getTaskKey());
            ForkJoinTaskShadeTask subTask = new ForkJoinTaskShadeTask(
                    child.getTaskId(),
                    child.getAppType(),
                    scheduleTaskShadeDao,
                    scheduleTaskTaskShadeDao,
                    results);
            tasks.add(subTask);
        }

        // 查询上游任务
        List<ScheduleTaskTaskShade> parentTaskTask = scheduleTaskTaskShadeDao.listParentTask(taskId, appType);
        for (ScheduleTaskTaskShade parent : parentTaskTask) {
            if (results.contains(parent.getParentTaskKey())) {
                return null;
            }
            results.add(parent.getTaskKey());
            ForkJoinTaskShadeTask subTask = new ForkJoinTaskShadeTask(
                    parent.getParentTaskId(),
                    parent.getParentAppType(),
                    scheduleTaskShadeDao,
                    scheduleTaskTaskShadeDao,
                    results);
            tasks.add(subTask);
        }

        Collection<ForkJoinTaskShadeTask> forkJoinTaskShadeTasks = ForkJoinTask.invokeAll(tasks);
        for (ForkJoinTaskShadeTask forkJoinTaskShadeTask : forkJoinTaskShadeTasks) {
            Set<String> taskKeys = forkJoinTaskShadeTask.join();
            if (CollectionUtils.isNotEmpty(taskKeys)) {
                results.addAll(taskKeys);
            }
        }
        return results;
    }
}
