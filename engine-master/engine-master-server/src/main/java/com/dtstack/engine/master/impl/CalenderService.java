package com.dtstack.engine.master.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.enums.CandlerBatchTypeEnum;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.calender.CalenderTaskVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.ECalenderUseType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ConsoleCalenderDao;
import com.dtstack.engine.dao.ConsoleCalenderTimeDao;
import com.dtstack.engine.dao.ScheduleTaskCalenderDao;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.master.mapstruct.CalenderStruct;
import com.dtstack.engine.master.utils.CsvUtil;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.SetUtil;
import com.dtstack.engine.master.utils.TimeUtils;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleTaskCalender;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.schedule.common.enums.CalenderTimeFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CalenderService {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ConsoleCalenderDao consoleCalenderDao;

    @Autowired
    private ConsoleCalenderTimeDao consoleCalenderTimeDao;

    @Autowired
    private ScheduleTaskCalenderDao scheduleTaskCalenderDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CalenderStruct calenderStruct;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private WorkSpaceProjectService projectService;

    public List<String> parseExcel(File file, String calenderTimeFormat) {
        //第一行为标题
        List<List<String>> readResult = CsvUtil.readFromCsv(file.getPath(), 2, environmentContext.getMaxExcelSize() + 10, (data, result) -> {
            // 根据时间格式类型补 0
            String originTime = data[0];
            String formatTime = CalenderTimeFormat.formatCalenderTime(originTime, calenderTimeFormat);
            data[0] = formatTime;
            if (StringUtils.isBlank(formatTime)) {
                throw new RdosDefineException(String.format("存在调度日期为空不符合 %s 格式，请检查源文件", calenderTimeFormat));
            }
            if (CalenderTimeFormat.CALENDER_TIME_LENGTH != formatTime.length()) {
                throw new RdosDefineException(String.format("存在调度日期 %s 不符合 %s 格式，请检查源文件", originTime, calenderTimeFormat));
            }
            if (!CollectionUtils.isEmpty(result) && result.size() >= environmentContext.getMaxExcelSize()) {
                throw new RdosDefineException(ErrorCode.DATA_LIMIT);
            }
            try {
                DateUtil.parseDate(formatTime, CalenderTimeFormat.YEAR_MONTH_DAY_HOUR_MINUTE.getFormat(), false);
            } catch (Exception e) {
                throw new RdosDefineException(String.format("存在部分错误日期 %s，请检查源文件", originTime), e);
            }
            if (result.contains(Lists.newArrayList(data))) {
                throw new RdosDefineException(String.format("存在调度日期 %s 重复，请检查源文件", originTime));
            }
        });
        if (CollectionUtils.isEmpty(readResult)) {
            throw new RdosDefineException(ErrorCode.FILE_CONTENT_FOUND);
        }
        return readResult.stream().map(l -> l.get(0)).sorted().collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateCalenderExcel(File file, String calenderName, Long calenderId, String calenderTimeFormat) {
        ConsoleCalender dbConsoleCalender = consoleCalenderDao.selectByName(calenderName.trim());
        boolean updateFile = null != file;
        if (null != dbConsoleCalender && !dbConsoleCalender.getId().equals(calenderId)) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }

        ConsoleCalender consoleCalender = dbConsoleCalender;
        if (dbConsoleCalender == null) {
            consoleCalender = new ConsoleCalender();
            consoleCalender.setId(calenderId);
        }

        List<String> timeList = null;
        if (updateFile) {
            timeList = parseExcel(file, calenderTimeFormat);
            String lastTime = timeList.get(timeList.size() - 1);
            consoleCalender.setLatestCalenderTime(lastTime);
            consoleCalender.setCalenderFileName(file.getName());
        }

        consoleCalender.setCalenderTimeFormat(calenderTimeFormat);
        consoleCalender.setCalenderName(calenderName);
        if (null == calenderId) {
            int total = consoleCalenderDao.selectCount();
            if (total >= environmentContext.getMaxCalenderSize()) {
                throw new RdosDefineException(ErrorCode.DATA_LIMIT);
            }
            consoleCalender.setUseType(ECalenderUseType.CALENDER.getType());
            consoleCalenderDao.insert(consoleCalender);
            calenderId = consoleCalender.getId();
        } else {
            consoleCalenderDao.updateById(consoleCalender);
        }

        if (updateFile) {
            consoleCalenderTimeDao.delete(calenderId);
            Long finalCalenderId = calenderId;
            List<List<String>> partition = Lists.partition(timeList, environmentContext.getBatchJobInsertSize());
            for (List<String> parts : partition) {
                List<ConsoleCalenderTime> calenderTimes = parts.stream().map(t -> {
                    ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
                    consoleCalenderTime.setCalenderId(finalCalenderId);
                    consoleCalenderTime.setCalenderTime(t);
                    return consoleCalenderTime;
                }).collect(Collectors.toList());
                consoleCalenderTimeDao.insertBatch(calenderTimes);
            }
        }

    }

    public List<ConsoleCalender> pageQuery(PageQuery query) {
        return consoleCalenderDao.pageQuery(query);
    }

    public int generalCount() {
        return consoleCalenderDao.selectCount();
    }

    public void deleteById(long calenderId) {
        int totalTasks = scheduleTaskCalenderDao.findTotalTasks(calenderId);
        if (totalTasks > 0) {
            throw new RdosDefineException(ErrorCode.CALENDER_IS_USING);
        }
        consoleCalenderDao.delete(calenderId);
    }


    public ConsoleCalender findById(long calenderId) {
        return consoleCalenderDao.selectById(calenderId);
    }


    public ConsoleCalender findByName(String name) {
        return consoleCalenderDao.selectByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTaskCalender(Long taskId, Integer appType, Long calenderId, String expendTime,Integer candlerBatchType) {
        ScheduleTaskCalender scheduleTaskCalender = scheduleTaskCalenderDao.findByTaskId(taskId, appType);
        boolean notChanged = (null != scheduleTaskCalender
                && scheduleTaskCalender.getCalenderId().equals(calenderId)
                && StringUtils.equals(scheduleTaskCalender.getExpandTime(), expendTime)
                && ObjectUtils.equals(scheduleTaskCalender.getCandlerBatchType(),candlerBatchType));
        if (notChanged) {
            return;
        }

        // 校验多批次时间文本
        if (CandlerBatchTypeEnum.MANY.getType().equals(candlerBatchType)){
            checkIntervalTimesCondition(expendTime);
        }

        if (null != scheduleTaskCalender) {
            scheduleTaskCalenderDao.deleteByTaskIds(Lists.newArrayList(taskId), appType);
        }
        scheduleTaskCalender = new ScheduleTaskCalender();
        scheduleTaskCalender.setTaskId(taskId);
        scheduleTaskCalender.setAppType(appType);
        scheduleTaskCalender.setCalenderId(calenderId);
        scheduleTaskCalender.setExpandTime(StringUtils.trim(expendTime));
        // 默认单批次
        scheduleTaskCalender.setCandlerBatchType(candlerBatchType == null ? CandlerBatchTypeEnum.SINGLE.getType() :candlerBatchType);
        scheduleTaskCalenderDao.insertBatch(Lists.newArrayList(scheduleTaskCalender));
    }

    public boolean checkExpandTimeChanged(String newExpandTime, String oldExpandTime) {
        newExpandTime = StringUtils.trim(newExpandTime);
        oldExpandTime = StringUtils.trim(oldExpandTime);
        if (ObjectUtils.equals(oldExpandTime,newExpandTime)){
            return false;
        }
        String[] newExpandTimes = newExpandTime.split("\n");
        String[] oldExpandTimes = oldExpandTime.split("\n");
        Set<String> newTimesSet = Arrays.stream(newExpandTimes).collect(Collectors.toSet());
        Set<String> oldTimesSet = Arrays.stream(oldExpandTimes).collect(Collectors.toSet());
        return !SetUtil.isSetEqual(oldTimesSet, newTimesSet);
    }

    public void checkIntervalTimesCondition(String expendTime) {
        if (StringUtils.isEmpty(expendTime)){
            throw new RdosDefineException(ErrorCode.CALENDER_EXPAND_TIME_IS_NULL);
        }
        // 不超过255个字符
        if (expendTime.length() > 255){
            throw new RdosDefineException("多批次时间文本不能超过255字符");
        }
        expendTime = StringUtils.trim(expendTime);
        if (StringUtils.isEmpty(expendTime)){
            throw new RdosDefineException("多批次需配置时间");
        }
        // 自定义调度周期多批次，间隔至少五分钟
        String[] times = expendTime.split("\n");
        DateTime lastTime = null;
        for (String time : times){
            // check时间是否符合HH:mm格式
            String trim = StringUtils.trim(time);
            if (!TimeUtils.isTime(trim)) {
                throw new RdosDefineException(String.format("请输入正确的时间格式,time [%s]",time));
            }
            // check时间是否升序且间隔5分钟
            DateTime currTime = DateTime.of(trim, "HH:mm");
            if (lastTime != null && !lastTime.offset(DateField.MINUTE, environmentContext.getCandlerTimeInterval()).isBeforeOrEquals(currTime)) {
                throw new RdosDefineException(String.format("调度时间间隔至少为%s分钟，且按升序设置",environmentContext.getCandlerTimeInterval()));
            }
            lastTime = currTime;
        }
        if (times.length <= 1){
            throw new RdosDefineException("自定义调度日历多批次，需至少配置两个时间点");
        }
    }

    public ScheduleTaskCalender findTaskCalenderByTaskId (Long taskId, Integer appType) {
        return scheduleTaskCalenderDao.findByTaskId(taskId, appType);
    }

    public Long getCalenderIdByTaskId(Long taskId, Integer appType) {
        ScheduleTaskCalender taskCalender = scheduleTaskCalenderDao.findByTaskId(taskId, appType);
        return null == taskCalender ? null : taskCalender.getCalenderId();
    }


    public List<ConsoleCalenderTime> getTimeByCalenderId(Long calenderId, int size, Long afterTime) {
        return consoleCalenderTimeDao.getByCalenderId(calenderId, size, afterTime);
    }

    public List<CalenderTaskDTO> getTaskNameByCalenderId(List<Long> calenderIds) {
        return scheduleTaskCalenderDao.getTaskByCalenderId(calenderIds);
    }

    public List<Long> getAllTaskByCalenderId(List<Long> calenderIds, Integer appType) {
        return scheduleTaskCalenderDao.getAllTaskByCalenderId(calenderIds, appType);
    }

    public File downloadExcel(Long calenderId) {
        ConsoleCalender consoleCalender = consoleCalenderDao.selectById(calenderId);
        if (null == consoleCalender) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        List<ConsoleCalenderTime> calenderTimes = consoleCalenderTimeDao.listAfterTime(calenderId, 0L, Long.MAX_VALUE);
        List<String[]> times = calenderTimes.stream().map(c -> new String[]{parse2showDate(c.getCalenderTime(), consoleCalender)}).collect(Collectors.toList());
        String[] title = new String[]{String.format(GlobalConst.CALENDER_TITLE, consoleCalender.getCalenderTimeFormat())};
        File file = new File(ConfigConstant.USER_DIR_DOWNLOAD + File.separator + consoleCalender.getCalenderFileName());
        FileUtil.mkdirsIfNotExist(ConfigConstant.USER_DIR_DOWNLOAD);
        CsvUtil.writerCsv(file.getPath(), title, times, false);
        return file;
    }

    /**
     * 将 DB 中存储的 calenderTime 按照 {@link ConsoleCalender#calenderTimeFormat} 转换为日期格式显示
     * @param calenderTime
     * @param consoleCalender
     * @return
     */
    private String parse2showDate(String calenderTime, ConsoleCalender consoleCalender) {
        // 格式应该为 yyyyMMdd 但是底层由于兼容性后缀补「0000」存储的是 yyyyMMddHHmm
        boolean shouldFormat = CalenderTimeFormat.YEAR_MONTH_DAY.getFormat().equals(consoleCalender.getCalenderTimeFormat())
                && org.apache.commons.lang3.StringUtils.length(calenderTime) == CalenderTimeFormat.CALENDER_TIME_LENGTH;
        if (shouldFormat) {
            return calenderTime.substring(0, CalenderTimeFormat.YEAR_MONTH_DAY.getFormat().length());
        } else {
            return calenderTime;
        }
    }

    public PageResult<List<CalenderTaskVO>> listTaskByCalender(long calenderId, PageQuery<ScheduleTaskCalender> pageQuery) {
        ScheduleTaskCalender scheduleTaskCalender = new ScheduleTaskCalender();
        scheduleTaskCalender.setCalenderId(calenderId);
        pageQuery.setModel(scheduleTaskCalender);
        List<CalenderTaskDTO> calenderTaskDTOS = scheduleTaskCalenderDao.pageQuery(pageQuery);
        Set<Long> userIds = calenderTaskDTOS.stream().map(CalenderTaskDTO::getOwnerUserId).collect(Collectors.toSet());
        List<Long> tenantIds = calenderTaskDTOS.stream().map(CalenderTaskDTO::getTenantId).collect(Collectors.toList());
        Map<Integer, List<Long>> appProjectMap = calenderTaskDTOS.stream().collect(Collectors.groupingBy(CalenderTaskDTO::getAppType,
                Collectors.mapping(CalenderTaskDTO::getProjectId, Collectors.toList())));
        List<User> users = userService.findUserWithFill(userIds);
        Table<Integer,Long,AuthProjectVO> projectInfoTable = projectService.getProjectGroupAppType(appProjectMap);
        Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);
        Map<Long, String> userNameMapping = users.stream().collect(Collectors.toMap(User::getDtuicUserId, User::getUserName));
        List<CalenderTaskVO> taskVOList = calenderTaskDTOS.stream().map(calenderTaskDTO -> {
            CalenderTaskVO calenderTaskVO = calenderStruct.toTaskVO(calenderTaskDTO);
            calenderTaskVO.setOwnerUserName(userNameMapping.getOrDefault(calenderTaskDTO.getOwnerUserId(), ""));
            TenantDeletedVO tenantDetailVO = tenantMap.get(calenderTaskDTO.getTenantId());
            calenderTaskVO.setTenantName(tenantDetailVO == null ? "" : tenantDetailVO.getTenantName());
            AuthProjectVO authProjectVO = projectInfoTable.get(calenderTaskDTO.getAppType(), calenderTaskDTO.getProjectId());
            calenderTaskVO.setProjectName(authProjectVO == null ? "" : authProjectVO.getProjectName());
            return calenderTaskVO;
        }).collect(Collectors.toList());
        int totalTasks = scheduleTaskCalenderDao.findTotalTasks(calenderId);
        return new PageResult<>(taskVOList, totalTasks, pageQuery);
    }

    public void deleteTask(List<Long> taskIds, Integer appType) {
        scheduleTaskCalenderDao.deleteByTaskIds(taskIds, appType);
    }

    public List<CalenderTaskDTO> getCalenderByTasks(List<Long> taskIds, int appType) {
        return scheduleTaskCalenderDao.getCalenderByTasks(taskIds, appType);
    }
}
