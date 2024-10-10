package com.dtstack.engine.master.controller;

import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.calender.CalenderTaskVO;
import com.dtstack.engine.api.vo.calender.ConsoleCalenderVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.mapstruct.CalenderStruct;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node/calender")
@Api(value = "/node/calender", tags = {"调度日历接口"})
public class CalenderController {

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private CalenderStruct calenderStruct;

    @Autowired
    private EnvironmentContext environmentContext;

    @PostMapping(value = "/getCalenders")
    @ApiOperation(value = "获取日历列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", required = true, dataType = "int", allowMultiple = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, dataType = "int", allowMultiple = true),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, dataType = "string", allowMultiple = true, example = "desc")
    })
    @Authenticate(all = "console_global_calender_view_all")
    public PageResult<List<ConsoleCalenderVO>> getCalenders(@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
                                                            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
                                                            @RequestParam(value = "sort") String sort) throws Exception {

        String orderBy = StringUtils.isBlank(sort) ? "gmt_modified" :  "latest_calender_time";
        String orderSort = StringUtils.isBlank(sort) ? "desc" : sort;
        // 默认修改时间倒序排列
        PageQuery pageQuery = new PageQuery(currentPage, pageSize, orderBy, orderSort);
        List<ConsoleCalender> consoleCalenders = calenderService.pageQuery(pageQuery);
        int total = calenderService.generalCount();
        String expireDate = DateUtil.getDate(new DateTime().plusDays(10).toDate(), DateUtil.MINUTE_FORMAT);
        long expireDateLong = Long.parseLong(expireDate);
        if (CollectionUtils.isNotEmpty(consoleCalenders)) {
            List<ConsoleCalenderVO> consoleCalenderVOS = calenderStruct.toVOs(consoleCalenders);
            List<Long> calenderIds = consoleCalenders.stream().map(ConsoleCalender::getId).collect(Collectors.toList());
            Map<Long, CalenderTaskDTO> calenderTaskDTOMap = calenderService.getTaskNameByCalenderId(calenderIds)
                    .stream().collect(Collectors.toMap(CalenderTaskDTO::getCalenderId, c -> c));
            for (ConsoleCalenderVO consoleCalenderVO : consoleCalenderVOS) {
                CalenderTaskDTO calenderTaskDTO = calenderTaskDTOMap.get(consoleCalenderVO.getId());
                if (null != calenderTaskDTO) {
                    consoleCalenderVO.setTaskNames(calenderTaskDTO.getConcatTaskNames());
                }
                consoleCalenderVO.setExpiringSoon(consoleCalenderVO.getLatestCalenderTime() <= expireDateLong);
            }
            return new PageResult<>(consoleCalenderVOS, total, pageQuery);
        }
        return new PageResult<>(new ArrayList<>(), total, pageQuery);
    }

    @PostMapping(value = "/deleteCalender")
    @ApiOperation(value = "删除日历")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "calenderId", value = "日历id", required = true, dataType = "long", allowMultiple = true),
    })
    @Authenticate(all = "console_global_calender_delete_all")
    public void deleteCalender(@RequestParam("calenderId") long calenderId, @RequestParam("dt_token") String dtToken) throws Exception {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }
        boolean isAdmin = roleService.checkIsSysAdmin(user);
        if (!isAdmin) {
            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
        }
        calenderService.deleteById(calenderId);
    }

    @PostMapping(value = "/listTaskByCalender")
    @ApiOperation(value = "获取日历关联任务数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "calenderId", value = "日历id", required = true, dataType = "long", allowMultiple = true),
    })
    @Authenticate(all = "console_global_calender_view_all")
    public PageResult<List<CalenderTaskVO>> listTaskByCalender(@RequestParam("calenderId") long calenderId,
                                                               @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
                                                               @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) throws Exception {
        PageQuery pageQuery = new PageQuery(currentPage, pageSize);
        return calenderService.listTaskByCalender(calenderId, pageQuery);
    }


    @PostMapping(value = "/getCalenderTime")
    @ApiOperation(value = "获取日历时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "calenderId", value = "日历id", required = true, dataType = "long", allowMultiple = true),
    })
    @Authenticate(all = "console_global_calender_view_all")
    public List<String> getCalenderTime(@RequestParam("calenderId") long calenderId) throws Exception {
        List<ConsoleCalenderTime> times = calenderService.getTimeByCalenderId(calenderId, environmentContext.getMaxExcelSize(), null);
        return times.stream().map(ConsoleCalenderTime::getCalenderTime).collect(Collectors.toList());
    }
}
