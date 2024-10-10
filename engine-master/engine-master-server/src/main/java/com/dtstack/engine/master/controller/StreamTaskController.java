package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.CheckpointDetail;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CheckpointQueryParam;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.stream.EngineStreamJobVO;
import com.dtstack.engine.common.client.bean.FlinkWebUrlResultDTO;
import com.dtstack.engine.master.impl.StreamTaskService;
import com.dtstack.sdk.core.feign.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/node/streamTask")
@Api(value = "/node/streamTask", tags = {"流任务接口"})
public class StreamTaskController {

    @Autowired
    private StreamTaskService streamTaskService;

    @RequestMapping(value="/getCheckPoint", method = {RequestMethod.POST})
    @ApiOperation(value = "查询checkPoint")
    public List<EngineJobCheckpoint> getCheckPoint(@RequestParam("taskId") String taskId, @RequestParam("triggerStart") Long triggerStart, @RequestParam("triggerEnd") Long triggerEnd) {
        return streamTaskService.getCheckPoint(taskId, triggerStart, triggerEnd);
    }

    @RequestMapping(value="/getFailedCheckPoint", method = {RequestMethod.POST})
    @ApiOperation(value = "查询生成失败的checkPoint")
    public List<EngineJobCheckpoint> getFailedCheckPoint(@RequestParam("taskId") String taskId, @RequestParam("triggerStart") Long triggerStart, @RequestParam("triggerEnd") Long triggerEnd, @RequestParam("size") Integer size) {
        return streamTaskService.getFailedCheckPoint(taskId, triggerStart, triggerEnd, size);
    }

    @RequestMapping(value="/getSavePoint", method = {RequestMethod.POST})
    @ApiOperation(value = "查询savePoint")
    public EngineJobCheckpoint getSavePoint(@RequestParam("taskId") String taskId) {
        return streamTaskService.getSavePoint(taskId);
    }

    @RequestMapping(value="/getByTaskIdAndEngineTaskId", method = {RequestMethod.POST})
    public EngineJobCheckpoint getByTaskIdAndEngineTaskId(@RequestParam("taskId") String taskId, @RequestParam("engineTaskId") String engineTaskId) {
        return streamTaskService.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    @RequestMapping(value="/getEngineStreamJob", method = {RequestMethod.POST})
    @ApiOperation(value = "查询stream job")
    public List<ScheduleJob> getEngineStreamJob(@RequestParam("taskIds") List<String> taskIds) {
        return streamTaskService.getEngineStreamJob(taskIds);
    }

    @RequestMapping(value="/getEngineStreamJobNew", method = {RequestMethod.POST})
    @ApiOperation(value = "查询stream job")
    public List<EngineStreamJobVO> getEngineStreamJobNew(@RequestParam("taskIds") List<String> taskIds) {
        return streamTaskService.getEngineStreamJobNew(taskIds);
    }

    @RequestMapping(value="/getTaskIdsByStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "获取某个状态的任务task_id")
    public List<String> getTaskIdsByStatus(@RequestParam("status") Integer status) {
        return streamTaskService.getTaskIdsByStatus(status);
    }

    @RequestMapping(value="/getTaskStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务的状态")
    public Integer getTaskStatus(@RequestParam("taskId") String taskId) {
        return streamTaskService.getTaskStatus(taskId);
    }

    @RequestMapping(value="/getRunningTaskLogUrl", method = {RequestMethod.POST})
    @ApiOperation(value = "获取实时计算运行中任务的日志URL")
    public List<String> getRunningTaskLogUrl(@RequestParam("taskId") String taskId) {
        return streamTaskService.getRunningTaskLogUrl(taskId);
    }

    @RequestMapping(value="/grammarCheck", method = {RequestMethod.POST})
    @ApiOperation(value = "语法检测")
    @ApiImplicitParams({
            @ApiImplicitParam(name="paramActionExt",value="语法检测相关参数信息",required=true, paramType="body", dataType = "ParamActionExt")
    })
    public CheckResult grammarCheck(@RequestBody ParamActionExt paramActionExt) {
        return streamTaskService.grammarCheck(paramActionExt);
    }

    @RequestMapping(value="/getWebMonitorUrl", method = {RequestMethod.POST})
    @ApiOperation(value = "获取flink web url地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name="paramActionExt",value="获取finkWebMonitorUrl",required=true, paramType="body", dataType = "ParamActionExt")
    })
    public FlinkWebUrlResultDTO getWebMonitorUrl(@RequestParam("taskId") String  taskId) {
        return streamTaskService.getWebMonitorUrl(taskId);
    }

    @RequestMapping(value = "/queryCheckPoint", method = {RequestMethod.POST})
    @ApiOperation(value = "根据条件查询 checkpoint")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checkpointQueryParam", value = "checkpoint 查询条件", required = true, paramType = "body", dataType = "CheckpointQueryParam")
    })
    public PageResult<List<CheckpointDetail>> queryCheckPoint(@RequestBody CheckpointQueryParam checkpointQueryParam) {
        if (Objects.isNull(checkpointQueryParam.getCurrentPage())) {
            checkpointQueryParam.setCurrentPage(1);
        }
        if (Objects.isNull(checkpointQueryParam.getPageSize())) {
            checkpointQueryParam.setPageSize(20);
        }
        return streamTaskService.queryCheckPoint(checkpointQueryParam);
    }

    /**
     * @param jobId
     * @param componentVersion
     * @return
     * 请使用 com.dtstack.engine.master.impl.StreamTaskService#queryCheckpointTotalSize(com.dtstack.engine.api.param.CheckpointQueryParam)
     */
    @Deprecated
    @RequestMapping(value = "/getCheckpointTotalSize", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务 id 查询 checkpoint 总大小")
    public Long getCheckpointTotalSize(@Param("jobId") String jobId, @Param("componentVersion") String componentVersion) {
        return streamTaskService.getCheckpointTotalSize(jobId, componentVersion);
    }

    @RequestMapping(value = "/queryCheckpointTotalSize", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务 id 查询 checkpoint 总大小")
    public Long queryCheckpointTotalSize(@RequestBody CheckpointQueryParam checkpointQueryParam) {
        return streamTaskService.queryCheckpointTotalSize(checkpointQueryParam);
    }
}
