package com.dtstack.engine.master.controller.sdk;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.StreamJobQueryParam;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.FlinkTableResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.stream.FlinkQueryResultVO;
import com.dtstack.engine.api.vo.stream.ScheduleStreamJobVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.master.enums.SearchTypeEnum;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.StreamTaskService;
import com.dtstack.engine.master.utils.CheckParamUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yuebai
 * @date 2021-09-16
 */
@RestController
@RequestMapping("/node/sdk/stream/")
public class StreamSdkController {

    @Autowired
    private StreamTaskService streamTaskService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 获取流计算任务checkpoint size总大小
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/totalSize", method = {RequestMethod.POST})
    public Long totalSize(@RequestParam("jobId") String jobId) throws Exception {
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        return streamTaskService.getTotalSize(jobId);
    }


    /**
     * 获取流计算任务列表
     *
     * @return
     */
    @RequestMapping(value = "/queryJobs", method = {RequestMethod.POST})
    public PageResult<List<ScheduleStreamJobVO>> queryJobs(@RequestBody @Valid StreamJobQueryParam queryParam) {
        CheckParamUtils.checkPageSize(queryParam.getCurrentPage(), queryParam.getPageSize());
        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(queryParam.getCurrentPage(), queryParam.getPageSize(), ConfigConstant.GMT_MODIFIED,
                Optional.ofNullable(queryParam.getModifyOrderSort()).orElse(Sort.DESC.name()));
        if (!StringUtils.isEmpty(queryParam.getTaskName()) || CollectionUtils.isNotEmpty(queryParam.getVersions())) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeService.listByNameLikeWithSearchType(queryParam.getProjectId(), queryParam.getTaskName(),
                    queryParam.getAppType(), null, null, SearchTypeEnum.FUZZY.getType(), queryParam.getVersions(), ComputeType.STREAM.getType());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                List<Long> taskIds = batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
                scheduleJobDTO.setTaskIds(taskIds);
            } else {
                return new PageResult<>(new ArrayList<>(0), 0, pageQuery);
            }
        }
        scheduleJobDTO.setComputeType(ComputeType.STREAM.getType());
        scheduleJobDTO.setAppType(queryParam.getAppType());
        scheduleJobDTO.setBusinessType(queryParam.getBusinessType());
        scheduleJobDTO.setJobStatuses(queryParam.getStatus());
        scheduleJobDTO.setProjectId(queryParam.getProjectId());
        scheduleJobDTO.setDtuicTenantId(queryParam.getDtuicTenantId());
        scheduleJobDTO.setPageQuery(true);
        pageQuery.setModel(scheduleJobDTO);
        return scheduleJobService.queryStreamJobs(pageQuery);
    }


    /**
     * 获取流计算状态列表
     *
     * @return
     */
    @RequestMapping(value = "/queryJobsStatusStatistics", method = {RequestMethod.POST})
    public Map<String, Integer> queryJobsStatusStatistics(@RequestBody @Valid StreamJobQueryParam queryParam) {
        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        if (!StringUtils.isEmpty(queryParam.getTaskName()) || CollectionUtils.isNotEmpty(queryParam.getVersions())) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeService.listByNameLikeWithSearchType(queryParam.getProjectId(), queryParam.getTaskName(),
                    queryParam.getAppType(), null, null, SearchTypeEnum.FUZZY.getType(), queryParam.getVersions(), ComputeType.STREAM.getType());
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                List<Long> taskIds = batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
                scheduleJobDTO.setTaskIds(taskIds);
            }
        }
        scheduleJobDTO.setComputeType(ComputeType.STREAM.getType());
        scheduleJobDTO.setAppType(queryParam.getAppType());
        scheduleJobDTO.setBusinessType(queryParam.getBusinessType());
        scheduleJobDTO.setJobStatuses(queryParam.getStatus());
        scheduleJobDTO.setProjectId(queryParam.getProjectId());
        scheduleJobDTO.setDtuicTenantId(queryParam.getDtuicTenantId());
        List<StatusCount> statusCountList = scheduleJobService.queryStreamJobsStatusStatistics(scheduleJobDTO);

        Map<String, Integer> statusResult = new HashMap<>();
        int sum = statusCountList.stream().mapToInt(StatusCount::getCount).sum();
        statusResult.putIfAbsent("ALL", sum);

        Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getStatusStream();
        Map<Integer, Integer> statusTotal = statusCountList.stream()
                .collect(Collectors.groupingBy(StatusCount::getStatus, Collectors.summingInt(StatusCount::getCount)));
        int collectStatusTotal = 0;
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            Integer status = entry.getKey();
            int total = entry.getValue().stream().flatMapToInt(statusVal -> IntStream.of(statusTotal.getOrDefault(statusVal, 0))).sum();
            statusResult.put(RdosTaskStatus.getCode(status), total);
            collectStatusTotal += total;
        }
        //不再状态集合里的 归为UNRUNNING
        statusResult.putIfAbsent("UNRUNNING", sum - collectStatusTotal);
        return statusResult;
    }


    @RequestMapping(value = "/executeSql", method = {RequestMethod.POST})
    public CheckResult executeSql(@RequestBody ParamActionExt paramActionExt) throws Exception {
        return streamTaskService.executeSql(paramActionExt);
    }

    @RequestMapping(value = "/executeFlinkSQL", method = {RequestMethod.POST})
    public List<FlinkTableResult> executeFlinkSQL(@RequestBody ParamActionExt paramActionExt) throws Exception {
        return streamTaskService.executeFlinkSQL(paramActionExt);
    }

    /**
     * 获取 flink select 查询结果
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryJobData", method = {RequestMethod.POST})
    public FlinkQueryResultVO queryJobData(@RequestParam("jobId") String jobId) throws Exception {
        return streamTaskService.queryJobData(jobId);
    }

    /**
     * 实时任务下线
     *
     * @param taskIds
     * @return
     */
    @RequestMapping(value = "/offline", method = {RequestMethod.POST})
    public OfflineReturnVO offline(@RequestParam("taskIds") List<String> taskIds) {
        if (org.springframework.util.CollectionUtils.isEmpty(taskIds)) {
            OfflineReturnVO offlineReturnVO = new OfflineReturnVO();
            offlineReturnVO.setSuccess(Boolean.FALSE);
            offlineReturnVO.setMsg("taskIds or appType must pass");
            return offlineReturnVO;
        }

        return streamTaskService.offline(taskIds);
    }

    @RequestMapping(value = "/deleteTask", method = {RequestMethod.POST})
    public Integer deleteTask(@RequestParam("taskIds") List<String> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return 0;
        }
        return scheduleJobService.updateToDelete(taskIds, AppType.STREAM.getType());
    }
}
