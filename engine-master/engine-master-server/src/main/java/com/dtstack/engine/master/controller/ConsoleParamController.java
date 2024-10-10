package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.common.enums.EDateBenchmark;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.master.utils.MultipartFileUtil;
import com.dtstack.engine.api.vo.console.param.ConsoleCalenderTimeShowVO;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.po.ScheduleTaskParam;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.util.TimeParamOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node/param")
@Api(value = "/node/param", tags = {"全局参数接口"})
public class ConsoleParamController {

    @Autowired
    private ParamService paramService;

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;


    @PostMapping(value = "/getParams")
    @ApiOperation(value = "获取参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, dataType = "string", example = "desc")
    })
    @Authenticate(all = "console_global_param_view_all")
    public PageResult<List<ConsoleParamVO>> getParams(@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
                                                      @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) throws Exception {
        PageQuery pageQuery = new PageQuery(currentPage, pageSize);
        List<ConsoleParam> consoleParams = paramService.pageQuery(pageQuery);
        int total = paramService.generalCount();
        List<ConsoleParamVO> consoleParamVOS = paramStruct.toVos(consoleParams);
        Set<Long> userIds = consoleParams.stream()
                .map(ConsoleParam::getCreateUserId)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userService.findUserWithFill(userIds).stream().collect(Collectors.toMap(User::getDtuicUserId, Function.identity()));
        for (ConsoleParamVO consoleParamVO : consoleParamVOS) {
            User user = userMap.get(consoleParamVO.getCreateUserId());
            consoleParamVO.setCreateUser(null == user ? "system" : user.getUserName());
        }
        return new PageResult<>(consoleParamVOS, total, pageQuery);
    }

    @PostMapping(value = "/deleteParam")
    @ApiOperation(value = "删除参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId", value = "参数id", required = true, dataType = "long")
    })
    @Authenticate(all = "console_global_param_delete_all")
    public void deleteParam(@RequestParam("paramId") long paramId, @RequestParam("dt_token") String dtToken) throws Exception {
        checkCanEdit(dtToken);
        int totalTask = paramService.findTotalTask(paramId);
        if (totalTask > 0) {
            throw new RdosDefineException("任务被应用，无法删除");
        }
        paramService.deleteById(paramId);
    }

    @PostMapping(value = "/getParamUseTask")
    @ApiOperation(value = "获取任务应用的参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId", value = "参数id", required = true, dataType = "long")
    })
    public List<String> getParamUseTask(@RequestParam("paramId") long paramId, @RequestParam(value = "limit", defaultValue = "3") int limit) throws Exception {
        List<ScheduleTaskParam> task = paramService.findTask(paramId, limit);
        if (CollectionUtils.isEmpty(task)) {
            return new ArrayList<>();
        }
        List<String> taskNames = new ArrayList<>(limit);
        Map<Integer, List<ScheduleTaskParam>> appTypeMap = task.stream().collect(Collectors.groupingBy(ScheduleTaskParam::getAppType));
        for (Integer appType : appTypeMap.keySet()) {
            List<Long> taskIds = appTypeMap.get(appType).stream().map(ScheduleTaskParam::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeService.findTaskIds(taskIds, Deleted.NORMAL.getStatus(), appType, true);
            if (!CollectionUtils.isEmpty(taskShades)) {
                taskNames.addAll(taskShades.stream().map(ScheduleTaskShade::getName).collect(Collectors.toList()));
            }
        }
        return taskNames;
    }

    private void checkCanEdit(String dtToken) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }
        boolean isAdmin = roleService.checkIsSysAdmin(user);
        if (!isAdmin) {
            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
        }
    }

    @PostMapping(value = "/addOrUpdateParam")
    @ApiOperation(value = "添加或更新参数")
    @Authenticate(all = "console_global_param_change_all")
    public void addOrUpdateParam(ConsoleParamVO consoleParamVO,
                                 MultipartFile file,
                                 @RequestParam("dt_token") String dtToken,
                                 @RequestParam(name = "dt_user_id") Long dtUserId) {
        checkCanEdit(dtToken);
        validateGlobalParam(consoleParamVO, file);
        setDefaultValue(consoleParamVO);
        consoleParamVO.setCreateUserId(dtUserId);
        File csvFile = null;
        if (file != null) {
            csvFile = MultipartFileUtil.parse2File(file);
        }
        paramService.addOrUpdate(consoleParamVO, csvFile);
    }

    @PostMapping(value = "/showCalenderTime")
    @ApiOperation(value = "获取日历时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId", value = "全局参数 id", required = true, dataType = "long")
    })
    public ConsoleCalenderTimeShowVO showCalenderTime(@RequestParam("paramId") long paramId) throws Exception {
        return paramService.getTimeByCalenderId(paramId);
    }

    /**
     * 参数校验
     * @param consoleParamVO
     * @param file
     */
    private void validateGlobalParam(ConsoleParamVO consoleParamVO, MultipartFile file) {
        String paramName = consoleParamVO.getParamName();
        if (StringUtils.isBlank(paramName)) {
            throw new RdosDefineException("全局参数名称不能为空");
        }
        if (paramName.length() > 128) {
            throw new RdosDefineException(ErrorCode.DATA_LIMIT);
        }

        Integer paramType = consoleParamVO.getParamType();
        if (paramType == null || !ParamService.GLOBAL_PARAM_TYPES.contains(paramType)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }

        String paramValue = consoleParamVO.getParamValue();
        // 非基于计划时间的类型，参数值不能为空
        if (!EParamType.isGlobalParamBaseCycTime(paramType) && StringUtils.isBlank(paramValue) ) {
            throw new RdosDefineException("全局参数值不能为空");
        }

        if (!EParamType.isGlobalParamBaseCycTime(paramType) && paramValue.length() > 2048) {
            throw new RdosDefineException(ErrorCode.DATA_LIMIT);
        }

        if (org.apache.commons.lang3.StringUtils.length(consoleParamVO.getParamDesc()) > 256) {
            throw new RdosDefineException(ErrorCode.DATA_LIMIT);
        }


        // 参数类型为时间
        if (EParamType.isGlobalParamBaseTime(paramType)) {
            validateGlobalParamBaseTime(consoleParamVO, file, paramValue);
            return;
        }

        //参数类型为按计划时间查找参数值
        if (EParamType.isGlobalParamBaseCycTime(paramType)) {
            validateGlobalParamBaseCycTime(consoleParamVO, file);
            return;
        }

        // 参数类型为常量
        if (EParamType.isGlobalParamConst(paramType)) {
            validateConstGlobalParam(paramValue);
        }

    }

    private static void validateGlobalParamBaseTime(ConsoleParamVO consoleParamVO, MultipartFile file, String paramValue) {
        // 校验日期基准
        Integer dateBenchmark = consoleParamVO.getDateBenchmark();
        EDateBenchmark eDateBenchmark = EDateBenchmark.valueOf(dateBenchmark);
        if (eDateBenchmark == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }

        // 自定义日期
        if (eDateBenchmark == EDateBenchmark.CUSTOM_DATE) {
            if (paramValue.length() > 32) {
                throw new RdosDefineException(ErrorCode.DATA_LIMIT);
            }
            // 校验参数值能否正确解析，比如 yyyyMMdd-1, "yyyy-MM-dd,-1"
            TimeParamOperator.trans2Composition(paramValue);
            // 上传文件的日期格式
            String dateFormat = consoleParamVO.getDateFormat();
            if (StringUtils.isBlank(dateFormat)) {
                throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
            }
            ParamService.validateDateFormat(dateFormat);
            if (consoleParamVO.getCalenderId() == null && file == null) {
                throw new RdosDefineException("请上传文件");
            }
        } else {
            // 自然日直接校验参数值
            TimeParamOperator.dealCustomizeTimeOperator(paramValue, DateTime.now().toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));
        }
    }

    private void validateGlobalParamBaseCycTime(ConsoleParamVO consoleParamVO, MultipartFile file) {
        if (consoleParamVO.getCalenderId() == null && file == null) {
            throw new RdosDefineException("请上传文件");
        }

    }

    private static void validateConstGlobalParam(String paramValue) {
        // 直接校验日期格式
        TimeParamOperator.dealCustomizeTimeOperator(paramValue, DateTime.now().toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));
    }


    private void setDefaultValue(ConsoleParamVO consoleParamVO) {
        Integer paramType = consoleParamVO.getParamType();
        if (EParamType.isGlobalParamBaseTime(paramType)) {
            Integer dateBenchmark = consoleParamVO.getDateBenchmark();
            EDateBenchmark eDateBenchmark = EDateBenchmark.valueOf(dateBenchmark);
            if (eDateBenchmark == EDateBenchmark.NATURAL_DATE) {
                // 自然日，多余字段设置为空
                consoleParamVO.setDateFormat(null);
            }
            return;
        }

        if (EParamType.isGlobalParamBaseCycTime(paramType)) {
            // 基于计划时间的全局参数时间格式目前是根据前端的单选项确定，支持yyyyMMdd或者yyyyMMddHHmmss
            consoleParamVO.setParamValue("");
            return;
        }

        if (EParamType.isGlobalParamConst(paramType)) {
            // 参数类型为常量，多余字段设置为空
            consoleParamVO.setDateBenchmark(null);
            consoleParamVO.setDateFormat(null);
        }

    }

}
