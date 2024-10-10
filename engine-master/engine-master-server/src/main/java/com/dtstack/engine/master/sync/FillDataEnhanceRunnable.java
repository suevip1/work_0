package com.dtstack.engine.master.sync;

import com.dtstack.engine.api.vo.schedule.job.FillDataConditionInfoVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataRelyInfo;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.FillLimitException;
import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.master.bo.FillDataInfoBO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.sync.fill.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 1:59 下午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class FillDataEnhanceRunnable implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(FillDataEnhanceRunnable.class);

    private Boolean isSuccess = Boolean.TRUE;
    private String failure = "";
    private final String fillName;
    private final Integer fillDataType;
    private final Long fillId;
    private final String startDay;
    private final String endDay;
    private final String beginTime;
    private final String endTime;
    private final Long projectId;
    private final Long tenantId;
    private final Long dtuicTenantId;
    private final Long userId;
    private final Integer taskRunOrder;
    private final Integer closeRetry;
    private final EScheduleType scheduleType;

    private final List<String> blackTaskKeyList;

    private final ScheduleJobService scheduleJobService;
    private final ScheduleFillDataJobDao scheduleFillDataJobDao;
    private final FillDataTaskFactory fillDataTaskFactory;

    public FillDataEnhanceRunnable(Long fillId,
                                   String fillName,
                                   Integer fillDataType,
                                   List<TaskKeyVO> chooseTaskInfo,
                                   FillDataConditionInfoVO fillDataConditionInfoVO,
                                   FillDataRelyInfo fillDataRelyInfo,
                                   List<TaskKeyVO> whitelist,
                                   List<TaskKeyVO> blacklist,
                                   String startDay,
                                   String endDay,
                                   String beginTime,
                                   String endTime,
                                   Long projectId,
                                   Long tenantId,
                                   Long dtuicTenantId,
                                   Long userId,
                                   Integer taskRunOrder,
                                   Integer closeRetry,
                                   EScheduleType scheduleType,
                                   ApplicationContext applicationContext) {
        this.fillId = fillId;
        this.fillName = fillName;
        this.fillDataType = fillDataType;
        this.startDay = startDay;
        this.endDay = endDay;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.dtuicTenantId = dtuicTenantId;
        this.userId = userId;
        this.taskRunOrder = taskRunOrder;
        this.closeRetry = closeRetry;
        this.scheduleType = scheduleType;
        this.scheduleFillDataJobDao = applicationContext.getBean(ScheduleFillDataJobDao.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);

        if (CollectionUtils.isNotEmpty(blacklist)) {
            blackTaskKeyList = blacklist.stream().map(vo -> vo.getTaskId() + "-" + vo.getAppType()).collect(Collectors.toList());
        } else {
            blackTaskKeyList = Lists.newArrayList();
        }

       this.fillDataTaskFactory = FillDataTaskFactory.getInstance(applicationContext,chooseTaskInfo,
               fillDataConditionInfoVO,fillDataRelyInfo,whitelist);
    }

    @Override
    public void run() {
        Integer status;
        try {
            FillDataTask fillDataTask = this.fillDataTaskFactory.getFillDataTask(fillDataType);

            if (fillDataTask == null) {
                isSuccess = Boolean.FALSE;
                LOGGER.error("fillId:{} fail fillDataType:{} msg:Supplement data type: 0 Batch supplement data 1 Project supplement data", fillId, fillDataType);
                scheduleFillDataJobDao.updateGeneratStatus(fillId, FillGeneratStatusEnum.REALLY_GENERATED.getType(), FillGeneratStatusEnum.FILL_FAIL.getType(), failure);
                return;
            }

            // 所有要运行的节点集合 R集合
            LOGGER.info("fillId:{} start getRunList", fillId);
            Set<String> run = fillDataTask.getRunList();

            run = run.stream().filter(runKey -> !blackTaskKeyList.contains(runKey)).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(run)) {
                LOGGER.error("fillId:{} run is empty list", fillId);
                scheduleFillDataJobDao.updateGeneratStatus(fillId, FillGeneratStatusEnum.REALLY_GENERATED.getType(), FillGeneratStatusEnum.FILL_FINISH.getType(), failure);
                return;
            }

            Set<String> all = fillDataTask.getAllList(run);

            // 生成周期实例
            scheduleJobService.createEnhanceFillJob(all, run, blackTaskKeyList, fillId, fillName, beginTime, endTime, startDay, endDay,
                    projectId, tenantId, dtuicTenantId, userId, scheduleType,taskRunOrder,closeRetry);

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
}
