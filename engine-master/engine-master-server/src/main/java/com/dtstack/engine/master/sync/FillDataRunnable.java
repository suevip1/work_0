package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.FillLimitException;
import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.sync.fill.BatchFillDataTask;
import com.dtstack.engine.master.sync.fill.FillDataTask;
import com.dtstack.engine.master.sync.fill.ProjectFillDataTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 1:59 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataRunnable implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(FillDataRunnable.class);

    private Boolean isSuccess = Boolean.TRUE;
    private String failure = "";
    private final String fillName;
    private final Integer fillDataType;
    private final Boolean ignoreCycTime;
    private final Long fillId;
    private final String startDay;
    private final String endDay;
    private final String beginTime;
    private final String endTime;
    private final Long projectId;
    private final Long tenantId;
    private final Long dtuicTenantId;
    private final Long userId;
    private final EScheduleType scheduleType;

    private final List<FillDataChooseProjectVO> projects;
    private final List<FillDataChooseTaskVO> whitelist;
    private final List<FillDataChooseTaskVO> blacklist;
    private final List<FillDataChooseTaskVO> taskIds;
    private final FillDataChooseTaskVO rootTaskId;
    private final ApplicationContext applicationContext;

    private final List<String> blackTaskKeyList;

    private final ScheduleJobService scheduleJobService;
    private final ScheduleFillDataJobDao scheduleFillDataJobDao;
    private final ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    public FillDataRunnable(Long fillId,
                            String fillName,
                            Integer fillDataType,
                            Boolean ignoreCycTime,
                            List<FillDataChooseProjectVO> projects,
                            List<FillDataChooseTaskVO> taskIds,
                            List<FillDataChooseTaskVO> whitelist,
                            List<FillDataChooseTaskVO> blacklist,
                            FillDataChooseTaskVO rootTaskId,
                            String startDay,
                            String endDay,
                            String beginTime,
                            String endTime,
                            Long projectId,
                            Long tenantId,
                            Long dtuicTenantId,
                            Long userId,
                            EScheduleType scheduleType,
                            ApplicationContext applicationContext) {
        this.fillId = fillId;
        this.fillName = fillName;
        this.fillDataType = fillDataType;
        this.ignoreCycTime = ignoreCycTime;
        this.projects = projects;
        this.taskIds = taskIds;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.rootTaskId = rootTaskId;
        this.startDay = startDay;
        this.endDay = endDay;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.dtuicTenantId = dtuicTenantId;
        this.userId = userId;
        this.applicationContext = applicationContext;
        this.scheduleType = scheduleType;
        this.scheduleFillDataJobDao = applicationContext.getBean(ScheduleFillDataJobDao.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobOperatorRecordDao = applicationContext.getBean(ScheduleJobOperatorRecordDao.class);
        if (CollectionUtils.isNotEmpty(blacklist)) {
            blackTaskKeyList = this.blacklist.stream().map(vo -> vo.getTaskId() + "-" + vo.getAppType()).collect(Collectors.toList());
        } else {
            blackTaskKeyList = Lists.newArrayList();
        }
    }

    @Override
    public void run() {
        Integer status;
        try {
            FillDataTask fillDataTask = getFillDataTask();

            if (fillDataTask == null) {
                isSuccess = Boolean.FALSE;
                LOGGER.error("fillId:{} fail fillDataType:{} msg:Supplement data type: 0 Batch supplement data 1 Project supplement data", fillId, fillDataType);
                return;
            }

            // 所有要运行的节点集合 R集合
            LOGGER.info("fillId:{} start getRunList", fillId);
            Set<String> run = fillDataTask.getRunList();

            // 所有要生成的节点集合 A集合
            Set<String> all;
            if (rootTaskId == null) {
                // 如果rootTaskId是空，说明没有根节点，需要补充计算出有效路径
                all = fillDataTask.getAllList(run);
            } else {
                // 存在根节点，逻辑是补根节点及其下游，所以不需要计算有效路径
                all = Sets.newHashSet(run);
            }

            // 生成周期实例
            scheduleJobService.createFillJob(all, run, blackTaskKeyList, fillId, fillName, beginTime, endTime, startDay, endDay,
                    projectId, tenantId, dtuicTenantId, userId, ignoreCycTime, scheduleType);

        } catch (FillLimitException e) {
            LOGGER.error("fillId:{} create exception size limit:", fillId, e);
            scheduleFillDataJobDao.updateGeneratStatus(fillId, FillGeneratStatusEnum.REALLY_GENERATED.getType(),
                    FillGeneratStatusEnum.FILL_FAIL_LIMIT.getType(), e.getMessage());
            scheduleJobService.deleteOperator(fillId);
            return;
        } catch (Throwable e) {
            LOGGER.error("fillId:{} create exception:", fillId, e);
            isSuccess = Boolean.FALSE;
            failure = e.getMessage();
        }

        if (isSuccess) {
            status = FillGeneratStatusEnum.FILL_FINISH.getType();
        } else {
            status = FillGeneratStatusEnum.FILL_FAIL.getType();
        }
        scheduleFillDataJobDao.updateGeneratStatus(fillId, FillGeneratStatusEnum.REALLY_GENERATED.getType(), status, failure);
    }


    private FillDataTask getFillDataTask() {
        if (FillDataTypeEnum.BATCH.getType().equals(fillDataType) || FillDataTypeEnum.MANUAL.getType().equals(fillDataType)) {
            return new BatchFillDataTask(applicationContext, new FillDataInfoBO(projects, taskIds, rootTaskId, whitelist, blacklist, dtuicTenantId));
        } else if (FillDataTypeEnum.CONDITION_PROJECT_TASK.getType().equals(fillDataType)) {
            return new ProjectFillDataTask(applicationContext, new FillDataInfoBO(projects, taskIds, rootTaskId, whitelist, blacklist, dtuicTenantId));
        }
        return null;
    }
}
