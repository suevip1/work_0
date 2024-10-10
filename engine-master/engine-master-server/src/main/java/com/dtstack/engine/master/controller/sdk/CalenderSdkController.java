package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CalenderParam;
import com.dtstack.engine.api.vo.calender.ConsoleCalenderVO;
import com.dtstack.engine.api.vo.calender.TaskCalenderVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.mapstruct.CalenderStruct;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Sort;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022-03-09
 * sdk接口
 */
@RestController
@RequestMapping({"/node/sdk/calender"})
public class CalenderSdkController {

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private CalenderStruct calenderStruct;

    @PostMapping(value = "/getAllCalender")
    public PageResult<List<ConsoleCalenderVO>> getAllCalender(@RequestBody CalenderParam calenderParam) {
        PageQuery pageQuery = new PageQuery(calenderParam.getCurrentPage(), calenderParam.getPageSize(), "gmt_modified", Sort.DESC.name());
        List<ConsoleCalender> consoleCalenders = calenderService.pageQuery(pageQuery);
        int total = calenderService.generalCount();
        if (CollectionUtils.isNotEmpty(consoleCalenders)) {
            List<ConsoleCalenderVO> consoleCalenderVOS = calenderStruct.toVOs(consoleCalenders);
            return new PageResult<>(consoleCalenderVOS, total, pageQuery);
        }
        return new PageResult<>(new ArrayList<>(), total, pageQuery);
    }


    @PostMapping(value = "/overview")
    public ConsoleCalenderVO overview(@RequestBody CalenderParam calenderParam) {
        long calenderId = calenderParam.getCalenderId();
        int limit = calenderParam.getLimit();
        Long afterTime = calenderParam.getAfterTime();
        ConsoleCalender consoleCalender = calenderService.findById(calenderId);
        if (consoleCalender == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        if(Deleted.DELETED.getStatus().equals(consoleCalender.getIsDeleted())){
            return null;
        }
        List<ConsoleCalenderTime> times = calenderService.getTimeByCalenderId(calenderId, limit, afterTime);
        ConsoleCalenderVO consoleCalenderVO = calenderStruct.toVo(consoleCalender);
        List<String> timeStrings = times.stream().map(ConsoleCalenderTime::getCalenderTime).collect(Collectors.toList());
        consoleCalenderVO.setTimes(timeStrings);
        return consoleCalenderVO;
    }


    @PostMapping(value = "/queryByName")
    public ConsoleCalenderVO queryByName(@RequestBody CalenderParam calenderParam) {
        if (StringUtils.isBlank(calenderParam.getCalenderName())) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        ConsoleCalender calender = calenderService.findByName(calenderParam.getCalenderName());
        return calenderStruct.toVo(calender);
    }

    @PostMapping(value = "/queryTaskCalender")
    public List<TaskCalenderVO> queryTaskCalender(@RequestBody CalenderParam calenderParam) {
        if(CollectionUtils.isEmpty(calenderParam.getTaskIds())){
            return new ArrayList<>(0);
        }
        if(calenderParam.getAppType() <= 0L){
            throw new RdosDefineException(ErrorCode.APP_TYPE_NOT_NULL);
        }
        List<Long> taskIds = calenderParam.getTaskIds();
        int appType = calenderParam.getAppType();
        List<CalenderTaskDTO> calenderByTasks = calenderService.getCalenderByTasks(taskIds, appType);
        return calenderStruct.toTaskCalenderVO(calenderByTasks);
    }

    @PostMapping(value = "/findById")
    public ConsoleCalenderVO findById(@RequestBody CalenderParam calenderParam) {
        if(calenderParam.getCalenderId()<0L){
            return null;
        }
        ConsoleCalender consoleCalender = calenderService.findById(calenderParam.getCalenderId());
        return calenderStruct.toVo(consoleCalender);
    }
    @PostMapping(value = "/checkIntervalTimesCondition")
    public boolean checkIntervalTimesCondition(String expandTime) {
        calenderService.checkIntervalTimesCondition(expandTime);
        return true;
    }


}
