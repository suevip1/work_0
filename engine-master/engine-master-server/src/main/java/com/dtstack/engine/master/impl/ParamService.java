package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.console.param.ConsoleCalenderTimeShowVO;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.ECalenderUseType;
import com.dtstack.engine.common.enums.EDateBenchmark;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ConsoleCalenderDao;
import com.dtstack.engine.dao.ConsoleCalenderTimeDao;
import com.dtstack.engine.dao.ConsoleParamDao;
import com.dtstack.engine.dao.ScheduleJobParamDao;
import com.dtstack.engine.dao.ScheduleTaskParamDao;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.utils.CsvChecker;
import com.dtstack.engine.master.utils.CsvUtil;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleJobParam;
import com.dtstack.engine.po.ScheduleTaskParam;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.util.TimeParamOperator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.dtstack.schedule.common.util.TimeParamOperator.GLOBAL_PARAM_OFFSET_PATTERN;

@Component
public class ParamService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamService.class);

    public static final Set<Integer> GLOBAL_PARAM_TYPES = ImmutableSet.of(
            EParamType.GLOBAL_PARAM_CONST.getType(),
            EParamType.GLOBAL_PARAM_BASE_TIME.getType(),
            EParamType.GLOBAL_PARAM_BASE_CYCTIME.getType()
    );

    @Autowired
    private ConsoleParamDao consoleParamDao;

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private ScheduleTaskParamDao scheduleTaskParamDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ConsoleCalenderDao consoleCalenderDao;

    @Autowired
    private ConsoleCalenderTimeDao consoleCalenderTimeDao;

    @Resource
    private ScheduleJobParamDao scheduleJobParamDao;

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(ConsoleParamVO consoleParamVO, File csvFile) {
        ConsoleParam consoleParam = paramStruct.toEntity(consoleParamVO);
        boolean isUpdate = (consoleParamVO.getId() != null && consoleParam.getId() > 0);
        ConsoleParam dbParam = null;
        if (isUpdate) {
            dbParam = consoleParamDao.selectById(consoleParamVO.getId());
            if (dbParam == null) {
                throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
            }
            if (!dbParam.getParamName().equals(consoleParamVO.getParamName())) {
                checkName(consoleParamVO);
            }
            if (csvFile == null && consoleParamVO.getCalenderId() != null) {
                // 文件未发生变更，校验文件 id 是否一致，防止前端传错
                if (!consoleParamVO.getCalenderId().equals(dbParam.getCalenderId())) {
                    throw new RdosDefineException("日历 id 与原先不匹配");
                }
            }
            if (EParamType.SYS_TYPE.getType().equals(consoleParamVO.getParamType()) || EParamType.SYS_TYPE.getType().equals(dbParam.getParamType())) {
                throw new RdosDefineException("系统参数不允许修改");
            }
            // 发生切换，需要清理掉 DB 中的旧数据
            boolean needCleanCalendar = EParamType.GLOBAL_PARAM_CONST.getType().equals(consoleParamVO.getParamType())
                    || EDateBenchmark.NATURAL_DATE.getType().equals(consoleParamVO.getDateBenchmark());
            if (needCleanCalendar && dbParam.getCalenderId() != null) {
                consoleCalenderDao.remove(dbParam.getCalenderId());
                consoleCalenderTimeDao.delete(dbParam.getCalenderId());
            }
            consoleParamDao.updateById(consoleParam);
        } else {
            checkName(consoleParamVO);
            int total = consoleParamDao.selectCount();
            if (total > environmentContext.getMaxGlobalParamSize()) {
                throw new RdosDefineException(ErrorCode.DATA_LIMIT);
            }
            consoleParamDao.insert(consoleParam);
        }

        // 如果上传文件，则插入自定义调度日历
        Long newCalenderId = dealCsvCalendarIfNeeded(consoleParamVO, csvFile, dbParam);
        if (newCalenderId != null) {
            consoleParamDao.updateCalenderIdByPrimaryKey(newCalenderId, consoleParam.getId());
        }
    }

    private void checkName(ConsoleParamVO consoleParamVO) {
        ConsoleParam nameParam = consoleParamDao.selectByName(consoleParamVO.getParamName());
        if (null != nameParam) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }
    }

    public List<ConsoleParam> pageQuery(PageQuery query) {
        return consoleParamDao.pageQuery(query);
    }

    public int generalCount() {
        return consoleParamDao.selectCount();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long paramId) {
        ConsoleParam param = consoleParamDao.selectById(paramId);
        if (param == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        if (EParamType.SYS_TYPE.getType().equals(param.getParamType())) {
            throw new RdosDefineException("系统参数不能删除");
        }
        consoleParamDao.delete(paramId);
        Long calenderId = param.getCalenderId();
        if (calenderId != null) {
            consoleCalenderDao.remove(calenderId);
            consoleCalenderTimeDao.delete(calenderId);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTaskParam(Long taskId, Integer appType, List<ConsoleParamBO> consoleParamBOS) {
        List<Long> paramIds = consoleParamBOS.stream().map(ConsoleParamBO::getId)
                .collect(Collectors.toList());
        List<ScheduleTaskParam> scheduleTaskParams = scheduleTaskParamDao.findByTaskId(taskId, appType);
        if (CollectionUtils.isNotEmpty(scheduleTaskParams)) {
            scheduleTaskParamDao.deleteByTaskIds(Lists.newArrayList(taskId), appType);
        }
        if (CollectionUtils.isEmpty(paramIds)) {
            return;
        }
        List<ConsoleParam> consoleParams = consoleParamDao.selectByIds(paramIds);
        List<Long> dbParams = consoleParams.stream().map(ConsoleParam::getId).collect(Collectors.toList());
        if (!dbParams.containsAll(paramIds)) {
            throw new RdosDefineException("系统参数不存在" + paramIds);
        }

        List<ScheduleTaskParam> paramList = consoleParamBOS.stream().map(bo -> {
            ScheduleTaskParam scheduleTaskParam = new ScheduleTaskParam();
            scheduleTaskParam.setTaskId(taskId);
            scheduleTaskParam.setAppType(appType);
            scheduleTaskParam.setParamId(bo.getId());
            // 将偏移量和替换目标存下来
            // 这种场景是基于偏移量的全局参数设置
            scheduleTaskParam.setOffset(bo.getOffset());
            scheduleTaskParam.setReplaceTarget(bo.getReplaceTarget());
            return scheduleTaskParam;

        }).collect(Collectors.toList());
        scheduleTaskParamDao.insertBatch(paramList);
    }

    /**
     * 根据全局参数 id 获取自定义调度日历明细
     *
     * @param paramId
     * @return
     */
    public ConsoleCalenderTimeShowVO getTimeByCalenderId(Long paramId) {
        ConsoleParam consoleParam = consoleParamDao.selectById(paramId);
        if (consoleParam == null || consoleParam.getCalenderId() == null) {
            return null;
        }
        Long calenderId = consoleParam.getCalenderId();
        ConsoleCalender consoleCalender = consoleCalenderDao.selectById(calenderId);
        if (consoleCalender == null) {
            return null;
        }
        ConsoleCalenderTimeShowVO result = new ConsoleCalenderTimeShowVO();
        result.setParamId(paramId);
        result.setCalenderId(calenderId);
        result.setCsvFileName(consoleCalender.getCalenderFileName());

        List<ConsoleCalenderTime> calenderTimes = consoleCalenderTimeDao.getByCalenderId(calenderId,
                environmentContext.getMaxExcelSize(), null);
        List<String> times = calenderTimes.stream().map(ConsoleCalenderTime::getCalenderTime).collect(Collectors.toList());
        result.setTimes(times);
        result.setLatestCalenderTime(CollectionUtils.isNotEmpty(times) ? times.get(times.size() - 1) : null);
        return result;
    }

    public ConsoleParam findById(long paramId) {
        return consoleParamDao.selectById(paramId);
    }

    public ConsoleParam selectByName(String paramName) {
        return consoleParamDao.selectByName(paramName);
    }

    public List<ConsoleParam> selectSysParam() {
        return consoleParamDao.selectSysParam();
    }

    public List<ScheduleJobParam> selectParamByJobId(String jobId) {
        return scheduleJobParamDao.selectByJobId(jobId);
    }

    public List<ConsoleParamBO> selectByTaskId(Long taskId, int appType) {
        List<ScheduleTaskParam> taskParam = scheduleTaskParamDao.findByTaskId(taskId, appType);
        if (CollectionUtils.isEmpty(taskParam)) {
            return new ArrayList<>();
        }
        List<Long> paramIds = taskParam.stream().map(ScheduleTaskParam::getParamId).collect(Collectors.toList());
        // 基于全局参数偏移的场景，一个 paramId 可能关联两个 ScheduleTaskParam，replaceTarget 不同
        Map<Long, List<ScheduleTaskParam>> taskParamMap = taskParam.stream().collect(Collectors.groupingBy(ScheduleTaskParam::getParamId, Collectors.toList()));

        List<ConsoleParam> consoleParams = consoleParamDao.selectByIds(paramIds);
        List<ConsoleParamBO> consoleParamBOS = new ArrayList<>();
        consoleParams.forEach(consoleParam -> {
            List<ScheduleTaskParam> scheduleTaskParams = taskParamMap.get(consoleParam.getId());
            for (ScheduleTaskParam scheduleTaskParam:scheduleTaskParams) {
                ConsoleParamBO consoleParamBO = paramStruct.toBO(consoleParam);
                if (Objects.nonNull(scheduleTaskParam)) {
                    consoleParamBO.setReplaceTarget(scheduleTaskParam.getReplaceTarget());
                    consoleParamBO.setOffset(scheduleTaskParam.getOffset());
                }
                consoleParamBOS.add(consoleParamBO);
            }
        });
        return consoleParamBOS;
    }

    public void deleteTaskParamByTaskId(List<Long> taskId, Integer appType) {
        scheduleTaskParamDao.deleteByTaskIds(taskId, appType);
    }

    public int findTotalTask(long paramId) {
        return scheduleTaskParamDao.findTotalTasks(paramId);
    }

    public List<ScheduleTaskParam> findTask(long paramId, int limit) {
        return scheduleTaskParamDao.findTask(paramId, limit);
    }

    public void convertGlobalToParamType(String info, BiConsumer<List<ConsoleParamBO>, List<ScheduleTaskParamShade>> convertConsumer,BiConsumer<String, List<ScheduleTaskParamShade>> consumer) {
        JSONObject commitInfo = JSONObject.parseObject(info);
        if (commitInfo == null) {
            return;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) commitInfo.get(GlobalConst.taskParamToReplace), ScheduleTaskParamShade.class);

        this.removeDuplicate(taskParamsToReplace);

        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            consumer.accept(info,taskParamsToReplace);
        } else {
            convertGlobalToParamType(taskParamsToReplace, convertConsumer);
        }
    }

    /**
     * 去除同名参数
     * @param taskParamsToReplace
     */
    private void removeDuplicate(List<ScheduleTaskParamShade> taskParamsToReplace) {
        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            return;
        }
        // 去重
        Set<String> params = new HashSet<>(taskParamsToReplace.size());
        Iterator<ScheduleTaskParamShade> iterator = taskParamsToReplace.iterator();
        while (iterator.hasNext()) {
            ScheduleTaskParamShade taskParamShade = iterator.next();
            if (!params.add(taskParamShade.getParamName())) {
                iterator.remove();
            }
        }
    }

    public void convertGlobalToParamType(String info, BiConsumer<List<ConsoleParamBO>, List<ScheduleTaskParamShade>> convertConsumer) {
        JSONObject commitInfo = JSONObject.parseObject(info);
        if (commitInfo == null) {
            return;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) commitInfo.get(GlobalConst.taskParamToReplace), ScheduleTaskParamShade.class);

        this.removeDuplicate(taskParamsToReplace);

        convertGlobalToParamType(taskParamsToReplace, convertConsumer);
    }

    public void convertGlobalToParamType(List<ScheduleTaskParamShade> taskParamsToReplace, BiConsumer<List<ConsoleParamBO>, List<ScheduleTaskParamShade>> convertConsumer) {
        if (CollectionUtils.isNotEmpty(taskParamsToReplace)) {
            //系统参数默认会添加 无需加上
            Map<Boolean, List<ScheduleTaskParamShade>> taskParamReplaceMap = taskParamsToReplace.stream()
                    .filter(s -> !EParamType.SYS_TYPE.getType().equals(s.getType()))
                    .collect(Collectors.groupingBy(s ->
                                            EParamType.GLOBAL.getType().equals(s.getType())
                                            || EParamType.GLOBAL_PARAM_BASE_TIME.getType().equals(s.getType())
                                            || EParamType.GLOBAL_PARAM_CONST.getType().equals(s.getType())
                                            || EParamType.GLOBAL_PARAM_BASE_CYCTIME.getType().equals(s.getType()),
                            Collectors.mapping(Function.identity(), Collectors.toList())));
            // 全局参数
            List<ScheduleTaskParamShade> globalTaskParams = taskParamReplaceMap.getOrDefault(Boolean.TRUE, new ArrayList<>());
            //将全局参数过滤掉 转换为task_param表关系
            List<ScheduleTaskParamShade> otherTaskParams = taskParamReplaceMap.getOrDefault(Boolean.FALSE, new ArrayList<>());

            List<ConsoleParamBO> params = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(globalTaskParams)) {
                for (ScheduleTaskParamShade globalTaskParam : globalTaskParams) {
                    ConsoleParamBO consoleParamBO = getConsoleParamBO(globalTaskParam);
                    if (consoleParamBO == null) {
                        throw new RdosDefineException("全局参数不存在" + globalTaskParam.getParamName());
                    }
                    params.add(consoleParamBO);
                }
            }
            convertConsumer.accept(params, otherTaskParams);
        }
    }

    private ConsoleParamBO getConsoleParamBO(ScheduleTaskParamShade globalTaskParam) {
        Boolean useOffset = globalTaskParam.getUseOffset();
        // 不使用偏移量
        if (Objects.isNull(useOffset) || !useOffset) {
            ConsoleParam consoleParam = selectByName(globalTaskParam.getParamName());
            return paramStruct.toBO(consoleParam);
        }

        // 使用偏移量
        String paramCommand = globalTaskParam.getParamCommand();
        if (!paramCommand.contains("#")) {
            throw new RdosDefineException(String.format("基于全局参数的偏移，参数值 %s 错误，未包含 #", globalTaskParam.getParamCommand()));
        }

        // 使用偏移量的情况下，从 command 中解析出实际控制台的全局参数名和偏移量
        String offset = getOffset(paramCommand);
        String actualParamName = getActualParamName(paramCommand);

        ConsoleParam consoleParam = selectByName(actualParamName);
        if (consoleParam == null) {
            throw new RdosDefineException(globalTaskParam.getParamName() + " 全局参数不存在");
        }

        ConsoleParamBO consoleParamBO = paramStruct.toBO(consoleParam);
        consoleParamBO.setOffset(offset);
        // 离线基于全局参数偏移量的情况下，paramName 对应任务脚本实际要替换的 key
        consoleParamBO.setReplaceTarget(globalTaskParam.getParamName());
        return consoleParamBO;
    }

    private String getActualParamName(String paramCommand) {
        return paramCommand.substring(0, paramCommand.indexOf("#"));
    }

    public String getOffset(String paramCommand) {
        String offset = paramCommand.substring(paramCommand.indexOf("#") + 1);
        Matcher matcher = GLOBAL_PARAM_OFFSET_PATTERN.matcher(offset);
        if (StringUtils.isBlank(offset) || !matcher.find()) {
            throw new RdosDefineException(String.format("基于全局参数的偏移，参数值 %s 错误，偏移量有误", paramCommand));
        }
        return offset;
    }

    /**
     * 将 DB 中存储的 number 按照 dateFormat 转换为日期格式显示
     *
     * @param numberStr  DB 中存储的 yyyyMMdd 形式的数字
     * @param dateFormat 日期格式模板
     * @return
     */
    private static String parse2ShowDate(String numberStr, String dateFormat) {
        if (DateUtil.DAY_FORMAT.equals(dateFormat)) {
            return numberStr;
        } else {
            long timestamp = DateUtil.getTimestamp(numberStr, DateUtil.DAY_FORMAT);
            return DateUtil.getFormattedDate(timestamp, dateFormat);
        }
    }

    public File downloadExcel(Long consoleParamId) {
        ConsoleParam consoleParam = consoleParamDao.selectById(consoleParamId);
        if (null == consoleParam || consoleParam.getCalenderId() == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        Long calenderId = consoleParam.getCalenderId();
        ConsoleCalender consoleCalender = consoleCalenderDao.selectById(calenderId);
        if (consoleCalender == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        List<ConsoleCalenderTime> calenderTimes = consoleCalenderTimeDao.listAfterTime(calenderId, 0L, Long.MAX_VALUE);
        // 日期格式
        String dateFormat = consoleParam.getDateFormat();

        List<String[]> times = new ArrayList<>();
        if (EParamType.isGlobalParamBaseTime(consoleParam.getParamType())) {
            times = calenderTimes.stream()
                    .map(c -> new String[]{parse2ShowDate(c.getCalenderTime(), dateFormat)})
                    .collect(Collectors.toList());
        }
        if (EParamType.isGlobalParamBaseCycTime(consoleParam.getParamType())) {
            times = calenderTimes.stream()
                    .map(c -> new String[]{c.getCalenderTime(), c.getExtraInfo()})
                    .collect(Collectors.toList());
        }

        String[] title = getTitles(consoleParam.getParamType(), dateFormat);
        File file = new File(ConfigConstant.USER_DIR_DOWNLOAD + File.separator + consoleCalender.getCalenderFileName());
        CsvUtil.writerCsv(file.getPath(), title, times, false);
        return file;
    }

    /**
     * 处理自定义调度日历文件
     *
     * @param consoleParamVO
     * @param csvFile
     */
    private Long dealCsvCalendarIfNeeded(ConsoleParamVO consoleParamVO, File csvFile, ConsoleParam dbConsoleParam) {
        Integer paramType = consoleParamVO.getParamType();
        String dateFormat = consoleParamVO.getDateFormat();

        if (csvFile == null) {
            return null;
        }
        // 解析 excel 文件
        List<List<String>> csvContent = parseCsv(paramType, dateFormat, csvFile);
        // 插入 consoleCalender 和 consoleCalenderTime 记录
        return insertRecord(consoleParamVO, csvContent, dbConsoleParam, csvFile.getName());
    }

    public List<List<String>> parseCsv(Integer paramType, String dateFormat, File csvFile) {
        if (EParamType.isGlobalParamBaseTime(paramType)) {
            validateDateFormat(dateFormat);
        }

        CsvChecker csvChecker = getCsvChecker(paramType, dateFormat);
        List<List<String>> readResult = CsvUtil.readFromCsv(csvFile.getPath(), 2, environmentContext.getMaxExcelSize() + 10, csvChecker);

        if (CollectionUtils.isEmpty(readResult)) {
            throw new RdosDefineException(ErrorCode.FILE_CONTENT_FOUND);
        }

        return readResult;
    }


    private Long insertRecord(ConsoleParamVO consoleParamVO,
                              List<List<String>> csvContent,
                              ConsoleParam dbConsoleParam,
                              String fileName) {

        Integer paramType = consoleParamVO.getParamType();
        String dateFormat = consoleParamVO.getDateFormat();

        // 日历列表先删后插
        if (dbConsoleParam != null && dbConsoleParam.getCalenderId() != null) {
            consoleCalenderDao.remove(dbConsoleParam.getCalenderId());
            consoleCalenderTimeDao.delete(dbConsoleParam.getCalenderId());
        }

        if (EParamType.isGlobalParamBaseTime(paramType)) {
            // 基于时间的全局参数，时间格式只保存 yyyyMMdd 的格式
            csvContent.forEach(line -> {
                String time = line.get(0);
                if (!DateUtil.DAY_FORMAT.equals(dateFormat)) {
                    long timestamp = DateUtil.getTimestamp(time, dateFormat);
                    line.set(0, DateUtil.getFormattedDate(timestamp, DateUtil.DAY_FORMAT));
                }
            });
        }

        // 根据时间排序下
        csvContent = csvContent.stream()
                .sorted(Comparator.comparingLong(a -> Long.parseLong(a.get(0))))
                .collect(Collectors.toList());

        ConsoleCalender consoleCalender = new ConsoleCalender();
        String lastTime = csvContent.get(csvContent.size() - 1).get(0);
        consoleCalender.setLatestCalenderTime(lastTime);
        consoleCalender.setCalenderName(UUID.randomUUID().toString().replace("-", ""));
        consoleCalender.setCalenderFileName(fileName);
        if (EParamType.isGlobalParamBaseCycTime(paramType)) {
            consoleCalender.setUseType(ECalenderUseType.GLOBAL_PARAM_BASE_CYCTIME.getType());
            consoleCalender.setCalenderTimeFormat(consoleParamVO.getDateFormat());
        }
        if (EParamType.isGlobalParamBaseTime(paramType)) {
            consoleCalender.setUseType(ECalenderUseType.GLOBAL_PARAM_BASE_TIME.getType());
            consoleCalender.setCalenderTimeFormat(DateUtil.DAY_FORMAT);
        }
        consoleCalenderDao.insert(consoleCalender);

        Long newCalenderId = consoleCalender.getId();
        List<List<List<String>>> partition = Lists.partition(csvContent, environmentContext.getMaxBatchTaskInsert());
        for (List<List<String>> parts : partition) {
            List<ConsoleCalenderTime> calenderTimes = parts.stream().map(t -> {
                ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
                consoleCalenderTime.setCalenderId(newCalenderId);
                consoleCalenderTime.setCalenderTime(t.get(0));
                if (EParamType.isGlobalParamBaseCycTime(paramType)) {
                    consoleCalenderTime.setExtraInfo(Optional.ofNullable(t.get(1)).orElse(""));
                }
                return consoleCalenderTime;
            }).collect(Collectors.toList());
            // 副表
            consoleCalenderTimeDao.insertBatch(calenderTimes);
        }
        return newCalenderId;
    }

    private CsvChecker getCsvChecker(Integer paramType, String dateFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setLenient(false);
        sdf.applyPattern(dateFormat);

        String finalDateFormat = dateFormat;
        return (lineData, result) -> {
            // 第一列数据
            String firstColumnContent = lineData[0];
            checkColumnContent(firstColumnContent, finalDateFormat, sdf);
            checkLinesLimit(result);
            checkDataDuplication(firstColumnContent, result);
        };
    }

    private void checkColumnContent(String firstColumnContent, String dateFormat, SimpleDateFormat simpleDateFormat) {
        // 不能为空
        if (StringUtils.isBlank(firstColumnContent) || firstColumnContent.length() != dateFormat.length()) {
            throw new RdosDefineException(String.format("存在日期为空，不符合 %s 格式，请检查源文件", dateFormat));
        }

        // 时间格式校验
        try {
            simpleDateFormat.applyPattern(dateFormat);
            simpleDateFormat.parse(firstColumnContent);
        } catch (Exception e) {
            LOGGER.error("check calendar first column content error:{}", e.getMessage(), e);
            throw new RdosDefineException(String.format("存在时间不符合 %s 格式，请检查源文件", dateFormat), e);
        }

    }

    private void checkLinesLimit(List<List<String>> result) {
        if (CollectionUtils.isNotEmpty(result) && result.size() >= environmentContext.getMaxExcelSize()) {
            throw new RdosDefineException(String.format("可添加的数据上限 %s 行", environmentContext.getMaxExcelSize()));
        }
    }

    private void checkDataDuplication(String firstColumnContent, List<List<String>> result) {
        List<String> firstColumns = result.stream().map(e -> e.get(0)).collect(Collectors.toList());
        if (firstColumns.contains(firstColumnContent)) {
            throw new RdosDefineException(String.format("存在重复项 %s，请检查源文件", firstColumnContent));
        }
    }

    public static void validateDateFormat(String dateFormat) {
        if (StringUtils.isBlank(dateFormat)) {
            throw new RdosDefineException("请先填写日期格式");
        }
        String yyyyMMdd = dateFormat.replaceAll("[^yMd]", "");
        if (!DateUtil.DAY_FORMAT.equals(yyyyMMdd)) {
            throw new RdosDefineException("该日期格式无法解析，请核实后重试");
        }
        try {
            new SimpleDateFormat(dateFormat);
        } catch (Exception e) {
            throw new RdosDefineException("该日期格式无法解析，请核实后重试");
        }
    }

    /**
     * 替换 cycTime 为最近的交易日期
     *
     * @param paramName  参数名称
     * @param paramValue 参数值
     * @param cycTime    任务执行时间, yyyyMMddHHmmss
     * @return 格式化后的最近的交易日期
     */
    public String dealCustomizeDate(String paramName, String paramValue, String cycTime, TimeParamOperator.Offset offset) {
        ConsoleParam consoleParam = consoleParamDao.selectByName(paramName);
        // 前置校验
        if (consoleParam == null) {
            LOGGER.info("paramName:{} not found", paramName);
            throw new RdosDefineException(String.format("全局参数 %s 不存在", paramName));
        }
        if (!EParamType.GLOBAL_PARAM_BASE_TIME.getType().equals(consoleParam.getParamType())) {
            LOGGER.info("paramName:{} not CUSTOMIZE_DATE", paramName);
            throw new RdosDefineException(String.format("全局参数 %s 配置异常", paramName));
        }
        Integer dateBenchmark = consoleParam.getDateBenchmark();
        EDateBenchmark eDateBenchmark = EDateBenchmark.valueOf(dateBenchmark);
        if (eDateBenchmark == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        // 自然日，直接转换
        if (EDateBenchmark.NATURAL_DATE.getType().equals(dateBenchmark)) {
            return TimeParamOperator.dealCustomizeTimeOperator(paramValue, cycTime, offset);
        }
        // 自定义日期
        if (consoleParam.getCalenderId() == null) {
            LOGGER.info("paramName:{} no calenderId", paramName);
            throw new RdosDefineException(String.format("全局参数 %s 配置异常", paramName));
        }
        // 一致性校验，防止离线传值跟 DB 中不同
        if (StringUtils.isBlank(paramValue) || !paramValue.equals(consoleParam.getParamValue())) {
            throw new RdosDefineException(Strings.format("paramName:{}, paramValue:{} empty or not consistent with DB:{}",
                    paramName, paramValue, consoleParam.getParamValue()));
        }
        // 比如 yyyyMMdd, yyyyMMdd - 1, yyyy-MM-dd, 「yyyy-MM-dd,-1」
        TimeParamOperator.TimeParamComposition timeParamComposition = TimeParamOperator.trans2Composition(paramValue);

        if (Objects.nonNull(timeParamComposition)) {
            timeParamComposition.setOperatorNum(TimeParamOperator.Offset.calculate(offset,timeParamComposition.getOperatorNum()));
        }

        String hitDate = calculateTargetDayBasedOnCalenderTime(cycTime, timeParamComposition, consoleParam);

        if (StringUtils.isBlank(hitDate)) {
            LOGGER.info("paramName:{}, cycTime:{} not hit", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 无法找到对应参数值", offset.getReplaceTarget()));
        }
        String dateFormat = consoleParam.getDateFormat();
        String result = parse2ShowDate(hitDate, dateFormat);
        LOGGER.info("paramName:{}, cycTime:{} result:{}", paramName, cycTime, result);
        return Objects.toString(result, "");
    }


    private String calculateTargetDayBasedOnCalenderTime(String cycTime,
                                                         TimeParamOperator.TimeParamComposition operator,
                                                         ConsoleParam consoleParam) {
        if (Objects.isNull(operator)) {
            return null;
        }
        Long calenderId = consoleParam.getCalenderId();
        return calculate(operator, cycTime, calenderId);
    }

    private String calculate(TimeParamOperator.TimeParamComposition operator,
                             String cycTime,
                             Long calenderId) {
        cycTime = operator.format2yyyyMMdd(cycTime);
        int operatorNum = Optional.ofNullable(operator.getOperatorNum()).orElse(0);

        // 先拿到日历的所有存在的日历时间区间
        List<String> existCalenderTimes = consoleCalenderTimeDao.findClosestBusinessDate(calenderId, operator.getOperator(), cycTime, operatorNum);

        if (CollectionUtils.isEmpty(existCalenderTimes)) {
            return null;
        }

        // 有操作数并且没有足够的日历时间用于计算，返回空
        if (operatorNum > 0 && existCalenderTimes.size() != operatorNum) {
            return null;
        }

        return existCalenderTimes.get(existCalenderTimes.size() - 1);
    }

    public String dealCycTimeGlobalParam(String paramName, String cycTime, TimeParamOperator.Offset offset, Integer scheduleType) {
        ConsoleParam consoleParam = consoleParamDao.selectByName(paramName);
        String replaceTarget = Optional.ofNullable(offset).map(TimeParamOperator.Offset::getReplaceTarget).orElse(paramName);
        // 前置校验
        if (consoleParam == null) {
            LOGGER.info("paramName:{} not found", paramName);
            throw new RdosDefineException(String.format("全局参数 %s 不存在", paramName));
        }

        Long calenderId = consoleParam.getCalenderId();

        if (calenderId == null) {
            LOGGER.info("paramName:{} no calenderId", paramName);
            throw new RdosDefineException(String.format("全局参数 %s 配置异常", paramName));
        }

        List<ConsoleCalenderTime> consoleCalenderTimeList = consoleCalenderTimeDao.getByCalenderId(calenderId, 1, null);
        if (CollectionUtils.isEmpty(consoleCalenderTimeList)) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 无法找到对应参数值", replaceTarget));
        }

        // 由于按照时间的全局参数新增加了yyyyMMdd的格式，因此需要兼容
        ConsoleCalenderTime consoleCalenderTime = consoleCalenderTimeList.get(0);
        //天格式
        if (DateUtil.DAY_FORMAT.length() ==  consoleCalenderTime.getCalenderTime().length()) {
            cycTime = cycTime.substring(0, DateUtil.DAY_FORMAT.length());
        }

        // 临时运行时参数取值取基于当前计划时间的最近一个历史值；
        if (EScheduleType.TEMP_JOB.getType().equals(scheduleType)) {
            cycTime = getNearestCycTimeForTempJob(calenderId, cycTime, paramName, replaceTarget);
        }

        // 检查下当前这个计划时间是否能找到对应参数值
        ConsoleCalenderTime consoleCalenderTimeWithoutOffset =consoleCalenderTimeDao.getByCalenderIdAndCalenderTime(calenderId, cycTime);

        if (Objects.isNull(consoleCalenderTimeWithoutOffset)) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 无法找到对应参数值", replaceTarget));
        }

        // 没有偏移量或者偏移量为 0 直接返回
        if (Objects.isNull(offset) || offset.getNum() == 0) {
            return consoleCalenderTimeWithoutOffset.getExtraInfo();
        }

        // 计算偏移量
        // 先拿到日历的所有存在的日历时间区间
        List<String> existCalenderTimes = consoleCalenderTimeDao.findClosestBusinessDateNotEquals(calenderId, offset.getOp(), cycTime, offset.getNum());

        if (CollectionUtils.isEmpty(existCalenderTimes)) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 基于偏移量无法找到对应参数值", replaceTarget));
        }

        // 有操作数并且没有足够的计划时间用于计算，返回空
        if (offset.getNum() > 0 && existCalenderTimes.size() != offset.getNum()) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 基于偏移量无法找到对应参数值", replaceTarget));
        }

        String offsetCycTime = existCalenderTimes.get(existCalenderTimes.size() - 1);
        if (StringUtils.isBlank(offsetCycTime)) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 基于偏移量无法找到对应参数值",replaceTarget));
        }

        ConsoleCalenderTime offsetConsoleCalenderTime = consoleCalenderTimeDao.getByCalenderIdAndCalenderTime(calenderId, offsetCycTime);
        if (Objects.isNull(offsetConsoleCalenderTime)) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("${%s} 基于偏移量无法找到对应参数值", replaceTarget));
        }

        return offsetConsoleCalenderTime.getExtraInfo();

    }

    private String getNearestCycTimeForTempJob(Long calenderId, String cycTime, String paramName, String replaceTarget) {
        ConsoleCalenderTime nearestCycTime = consoleCalenderTimeDao.getNearestTime(calenderId, Long.parseLong(cycTime), true);
        if (Objects.isNull(nearestCycTime)) {
            LOGGER.info("paramName:{} cyctime:{} paramValue not exist!", paramName, cycTime);
            throw new RdosDefineException(String.format("临时运行，${%s} 无法找到对应参数值", replaceTarget));
        }
        return nearestCycTime.getCalenderTime();
    }

    public String[] getTitles(Integer paramType, String dataFormat) {
        if (EParamType.isGlobalParamBaseTime(paramType)) {
            return new String[]{GlobalConst.GLOBAL_PARAM_CALENDER_TITLE};
        }
        if (EParamType.isGlobalParamBaseCycTime(paramType)) {
            return new String[]{String.format(GlobalConst.GLOBAL_PARAM_CYCTIME_TITLE_FIRST_COLUMN, dataFormat), GlobalConst.GLOBAL_PARAM_CYCTIME_TITLE_SECOND_COLUMN};
        }
        throw new RdosDefineException("不支持的参数格式");
    }

    public String getFileName(Integer paramType) {
        if (EParamType.isGlobalParamBaseTime(paramType)) {
            return "calender.csv";
        }
        if (EParamType.isGlobalParamBaseCycTime(paramType)) {
            return "param_lookup.csv";
        }
        throw new RdosDefineException("不支持的参数格式");
    }

    public List<String> listNamesByTypes(List<Integer> globalParamTypes) {
        List<String> paramNames = consoleParamDao.listByTypes(globalParamTypes);
        return paramNames;
    }

    /**
     * 移除同名参数
     * @param taskId
     * @param appType
     * @param scheduleTaskParamShades
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeIdenticalParam(Long taskId, Integer appType, List<ScheduleTaskParamShade> scheduleTaskParamShades) {
        if (CollectionUtils.isEmpty(scheduleTaskParamShades)) {
            return;
        }
        List<String> paramNames = scheduleTaskParamShades.stream().map(ScheduleTaskParamShade::getParamName).collect(Collectors.toList());
        scheduleTaskParamDao.removeIdenticalParam(taskId, appType, paramNames);
    }
}
