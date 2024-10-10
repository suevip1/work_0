package com.dtstack.engine.master.controller.sdk;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.schedule.job.*;
import com.dtstack.engine.api.vo.task.FillDataTaskVO;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.dto.FillLimitationDTO;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.enums.OperateTypeEnum;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:30 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleJob")
public class FillDataSdkController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @RequestMapping(value = "/fillData", method = {RequestMethod.POST})
    @ApiOperation(value = "补数据接口:支持批量补数据和工程补数据")
    public Long fillData(@RequestBody @Valid ScheduleFillJobParticipateVO scheduleFillJobParticipateVO) {
        Long projectId = scheduleFillJobParticipateVO.getProjectId();
        String startDay = scheduleFillJobParticipateVO.getStartDay();
        String endDay = scheduleFillJobParticipateVO.getEndDay();
        DateTime startTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(startDay, DateUtil.DATE_FORMAT));
        DateTime endTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(endDay, DateUtil.DATE_FORMAT));
        // 必要的校验
        scheduleJobService.checkFillDataParams(scheduleFillJobParticipateVO.getFillName(), projectId, startTime, DateTime.now());
        ScheduleFillDataInfoVO fillDataInfo = scheduleFillJobParticipateVO.getFillDataInfo();
        if (fillDataInfo == null) {
            throw new RdosDefineException("fillDataInfo is not null", ErrorCode.INVALID_PARAMETERS);
        }

        if (FillDataTypeEnum.CONDITION_PROJECT_TASK.getType().equals(fillDataInfo.getFillDataType()) &&
                (endTime.getMillis() - startTime.getMillis()) / (1000 * 3600 * 24) > 7) {
            throw new RdosDefineException("The difference between the start and end days cannot exceed 7 days", ErrorCode.INVALID_PARAMETERS);
        }

        List<ScheduleDict> scheduleDicts = scheduleDictService.listByDictType(DictType.FILL_LIMIT);
        FillLimitationDTO fillLimitationDTO = getLimitationValue(scheduleDicts);

        Long count = scheduleJobOperatorRecordDao.countByType(OperatorType.FILL_DATA.getType());
        if (environmentContext.getOpenFillLimitation() && fillLimitationDTO.getMaxOperate() <= count) {
            throw new RdosDefineException("补数据数量超过上限 : %s", count);
        }

        return scheduleJobService.fillData(scheduleFillJobParticipateVO,fillLimitationDTO);
    }

    private FillLimitationDTO getLimitationValue(List<ScheduleDict> scheduleDicts) {
        if (CollectionUtils.isNotEmpty(scheduleDicts)) {
            ScheduleDict scheduleDict = scheduleDicts.get(0);

            String dictValue = scheduleDict.getDictValue();

            try {
                return JSON.parseObject(dictValue, FillLimitationDTO.class);
            } catch (Exception e) {
                return FillLimitationDTO.build();
            }
        }
        return FillLimitationDTO.build();
    }

    @RequestMapping(value = "/immediately/fillData", method = {RequestMethod.POST})
    @ApiOperation(value = "补数据接口:无依赖关系，且会立即运行")
    public String immediatelyFillJob(@RequestBody ScheduleFillJobImmediatelyVO scheduleFillJobImmediatelyVO) throws Exception {
        return scheduleJobService.immediatelyFillJob(scheduleFillJobImmediatelyVO);
    }

    @PostMapping(value = "/manualTask")
    @ApiOperation(value = "手动任务")
    public Long manualTask(@RequestBody @Valid ScheduleFillJobParticipateVO scheduleFillJobParticipateVO) {
        Long projectId = scheduleFillJobParticipateVO.getProjectId();
        String startDay = scheduleFillJobParticipateVO.getStartDay();
        DateTime startTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(startDay, DateUtil.DATE_FORMAT));
        // 必要的校验
        scheduleJobService.checkFillDataParams(scheduleFillJobParticipateVO.getFillName(), projectId, startTime, DateTime.now());
        scheduleFillJobParticipateVO.getFillDataInfo().setFillDataType(FillDataTypeEnum.MANUAL.getType());
        List<ScheduleDict> scheduleDicts = scheduleDictService.listByDictType(DictType.FILL_LIMIT);
        FillLimitationDTO fillLimitationDTO = getLimitationValue(scheduleDicts);

        Long count = scheduleJobOperatorRecordDao.countByType(OperatorType.FILL_DATA.getType());
        if (environmentContext.getOpenFillLimitation() && fillLimitationDTO.getMaxOperate() <= count) {
            throw new RdosDefineException("补数据数量超过上限:%s", count);
        }
        return scheduleJobService.fillData(scheduleFillJobParticipateVO,fillLimitationDTO);
    }

    @RequestMapping(value = "/enhance/fillData", method = {RequestMethod.POST})
    public Long enhanceFillData(@RequestBody @Valid ScheduleFillJobParticipateEnhanceVO scheduleFillJobParticipateEnhanceVO) {
        Long projectId = scheduleFillJobParticipateEnhanceVO.getProjectId();
        String startDay = scheduleFillJobParticipateEnhanceVO.getStartDay();
        String endDay = scheduleFillJobParticipateEnhanceVO.getEndDay();
        DateTime startTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(startDay, DateUtil.DATE_FORMAT));
        DateTime endTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(endDay, DateUtil.DATE_FORMAT));
        // 必要的校验
        scheduleJobService.checkEnhanceFillDataParams(scheduleFillJobParticipateEnhanceVO.getFillName(), projectId, startTime, DateTime.now());
        ScheduleFillDataInfoEnhanceVO fillDataInfo = scheduleFillJobParticipateEnhanceVO.getFillDataInfo();
        if (fillDataInfo == null) {
            throw new RdosDefineException("fillDataInfo is not null", ErrorCode.INVALID_PARAMETERS);
        }

        if (FillDataTypeEnum.CONDITION_PROJECT_TASK.getType().equals(fillDataInfo.getFillDataType()) &&
                (endTime.getMillis() - startTime.getMillis()) / (1000 * 3600 * 24) > 7) {
            throw new RdosDefineException("The difference between the start and end days cannot exceed 7 days", ErrorCode.INVALID_PARAMETERS);
        }

        List<ScheduleDict> scheduleDicts = scheduleDictService.listByDictType(DictType.FILL_LIMIT);
        FillLimitationDTO fillLimitationDTO = getLimitationValue(scheduleDicts);

        Long count = scheduleJobOperatorRecordDao.countByType(OperatorType.FILL_DATA.getType());
        if (environmentContext.getOpenFillLimitation() && fillLimitationDTO.getMaxOperate() <= count) {
            throw new RdosDefineException("补数据数量超过上限:%s", count);
        }
        return scheduleJobService.enhanceFillData(scheduleFillJobParticipateEnhanceVO,fillLimitationDTO);
    }

    @RequestMapping(value = "/fillDataList", method = {RequestMethod.POST})
    public PageResult<List<ScheduleFillDataJobPreViewVO>> fillDataList(@RequestBody @Valid FillDataListVO vo) {
        return scheduleJobService.getFillDataJobInfoPreview(vo.getJobName(), vo.getRunDay(), vo.getBizStartDay(), vo.getBizEndDay(),
                vo.getDutyUserId(), vo.getProjectId(), vo.getAppType(), vo.getCurrentPage(), vo.getPageSize(), vo.getTenantId(), vo.getDtuicTenantId()
                , vo.getFillDataType());
    }

    @RequestMapping(value = "/fillDataJobList", method = {RequestMethod.POST})
    public PageResult<ScheduleFillDataJobDetailVO> fillDataJobList(@RequestBody @Valid FillDataJobListVO vo) {
        return scheduleJobService.fillDataJobList(vo);
    }

    @RequestMapping(value = "/createFillDataTaskList", method = {RequestMethod.POST})
    public FillDataTaskVO createFillDataTaskList(@RequestParam("taskId") Long taskId,
                                                 @RequestParam("appType") Integer appType,
                                                 @RequestParam("level") Integer level) {
        return scheduleJobService.createFillDataTaskList(taskId, appType, level);
    }


}
