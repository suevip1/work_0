package com.dtstack.engine.master.event;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.enums.AlterKey;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.listener.AlterEvent;
import com.dtstack.engine.master.listener.AlterEventContext;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 3:12 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobAlterEventLister implements ScheduleJobEventListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(JobAlterEventLister.class);

    @Autowired
    private AlterEvent alterEvent;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Override
    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        List<String> jobIds = event.getJobIds();
        Integer status = event.getStatus();

        if (CollectionUtils.isEmpty(jobIds) || status == null) {
            return;
        }

        if (TaskStatusConstrant.FAILED_STATUS.contains(status) ||
                TaskStatusConstrant.FINISH_STATUS.contains(status) ||
                TaskStatusConstrant.STOP_STATUS.contains(status)
        ) {
            List<ScheduleJob> rdosJobByJobIds = scheduleJobDao.getSimpleInfoByJobIds(jobIds);

            // 过滤掉重跑掉任务实例
            rdosJobByJobIds=  rdosJobByJobIds.stream()
                    .filter(job -> Restarted.NORMAL.getStatus() == job.getIsRestart())
                    .collect(Collectors.toList());

            Map<Integer, List<Long>> taskMaps = rdosJobByJobIds.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType,
                    Collectors.mapping(ScheduleJob::getTaskId, Collectors.toList())));

            Map<String, ScheduleTaskShade> tasks = Maps.newHashMap();
            for (Map.Entry<Integer, List<Long>> entry : taskMaps.entrySet()) {
                List<ScheduleTaskShade> taskByIds = scheduleTaskShadeService.getTaskByIds(entry.getValue(), entry.getKey());

                if (CollectionUtils.isNotEmpty(taskByIds)) {
                    tasks.putAll(taskByIds.stream().collect(Collectors.toMap(scheduleTaskShade ->
                            scheduleTaskShade.getTaskId()+"_" + scheduleTaskShade.getAppType(), g -> (g))));
                }
            }

            for (ScheduleJob scheduleJob : rdosJobByJobIds) {
                if (EScheduleType.TEMP_JOB.getType().equals(scheduleJob.getType())) {
                    continue;
                }
                AlterEventContext context = new AlterEventContext();
                context.setKey(AlterKey.status_change);
                context.setStatus(status);
                context.setJobId(scheduleJob.getJobId());
                context.setTaskId(scheduleJob.getTaskId());
                context.setAppType(scheduleJob.getAppType());
                context.setScheduleType(scheduleJob.getType());

                ScheduleTaskShade scheduleTaskShade = tasks.get(scheduleJob.getTaskId() + "_" + scheduleJob.getAppType());

                if (scheduleTaskShade != null) {
                    context.setTaskName(scheduleTaskShade.getName());
                    context.setTaskType(scheduleTaskShade.getTaskType());
                    context.setOwnerUserId(scheduleTaskShade.getOwnerUserId());
                }
                alterEvent.event(context);

            }
        }
    }

    @PostConstruct
    public void registerEvent(){
        ScheduleJobEventPublisher.getInstance().register(this);
    }
}
