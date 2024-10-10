package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobHistoryVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.mapstruct.ScheduleJobHistoryStruct;
import com.dtstack.engine.po.ScheduleJobHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-09-10
 */
@RestController
@RequestMapping("/node/sdk/history")
public class ScheduleJobHistorySdkController {

    @Autowired
    private ScheduleJobHistoryService scheduleJobHistoryService;

    @Autowired
    private ScheduleJobHistoryStruct jobHistoryStruct;

    @RequestMapping(value = "/queryJobHistory", method = {RequestMethod.POST})
    public List<ScheduleJobHistoryVO> queryJobHistory(@RequestParam("jobId") String jobId, @RequestParam("limitSize") Integer limitSize) throws Exception {
        if (limitSize == null || limitSize > 100) {
            limitSize = 100;
        }
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        PageResult<List<ScheduleJobHistory>> listPageResult = scheduleJobHistoryService.pageByJobId(jobId, limitSize, 1);
        List<ScheduleJobHistory> data = listPageResult.getData();
        return data.stream().map(jobHistoryStruct::toJobHistoryVO).collect(Collectors.toList());
    }

}
