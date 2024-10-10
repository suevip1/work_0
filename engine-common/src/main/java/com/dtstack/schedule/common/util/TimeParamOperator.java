package com.dtstack.schedule.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.dto.SystemTimeParamQueryDTO;
import com.dtstack.schedule.common.enums.ESystemTimeSpecialParam;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基准时间是yyyyMMddHHmmss
 * 基准天是yyyyMMdd
 * 所有自定义操作必须是基于这两个
 * Date: 2017/6/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class TimeParamOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeParamOperator.class);

    public static final String STD_FMT = "yyyyMMddHHmmss";

    public static final String DAY_FMT = "yyyyMMdd";

    public static final String DAY_FMT2 = "yyyy-MM-dd";

    private static final String MONTH_FMT = "yyyyMM";
    private static final String YEAR_FMT = "yyyy";

    private static final String SYSTEM_CURRENTTIME = "bdp.system.currenttime";

    private static final DateTimeFormatter cycTimeFormat = DateTimeFormat.forPattern(STD_FMT);

    private static final DateTimeFormatter timeFormatter1 = DateTimeFormat.forPattern(DAY_FMT);

    private static final DateTimeFormatter timeFormatter2 = DateTimeFormat.forPattern(DAY_FMT2);


    private static final String ISODATE_FORMATE = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final Pattern pattern = Pattern.compile("([a-zA-Z]{4,14})\\s*([\\-\\+])\\s*(\\d+)");

    private static final Pattern customizePattern = Pattern.compile("^(yyyyMMdd|hh24miss)\\s*([\\-\\+])\\s*(.*)");

    private static final String FORMATTED_TIME = "^yyyy|MM|dd|HH|hh|hh24|mm|ss|yyyyMM|yyyyMMdd|yyyyMMddHH|yyyyMMddHHmm|yyyyMMddHHmmss|yyyyMMddhh24|yyyyMMddhh24mm|yyyyMMddhh24mmss" +
            "|HH:mm:ss|yyyy-MM-dd HH:mm:ss|yyyy-MM-dd?";

    private static final Pattern formattedPattern = Pattern.compile(FORMATTED_TIME);

    /**
     * format 函数解析 日期类型
     */
    private static final Pattern FORMAT_PATTERN = Pattern.compile(
            "(?i)format\\s*\\(\\s*(?<format>[a-zA-Z\\:\\-]+)\\s*(?<operate>[\\+\\-]*)\\s*(?<number>[0-9]*)\\s*(?<unit>[a-zA-Z]+)\\s*,\\s*\\'(?<formatResult>[0-9a-zA-Z\\:\\-\\s\\/]+)\\'");

    /**
     * 以 +/- 开头，后面是正整数: +/- n
     */
    public static final Pattern GLOBAL_PARAM_OFFSET_PATTERN = Pattern.compile("^(\\-|\\+)\\d+$");

    public static Map<String, ChronoUnit> FORMAT_UNIT_MAPPING = ImmutableMap.<String, ChronoUnit>builder()
            .put("yyyy", ChronoUnit.YEARS)
            .put("MM", ChronoUnit.MONTHS)
            .put("dd", ChronoUnit.DAYS)
            .put("HH", ChronoUnit.HOURS)
            .put("hh", ChronoUnit.HOURS)
            .put("hh24", ChronoUnit.HOURS)
            .put("mm", ChronoUnit.MINUTES)
            .put("ss", ChronoUnit.SECONDS)
            .put("yyyyMM", ChronoUnit.MONTHS)
            .put("yyyyMMdd", ChronoUnit.DAYS)
            .put("yyyyMMddHH", ChronoUnit.HOURS)
            .put("yyyyMMddHHmm", ChronoUnit.MINUTES)
            .put("yyyyMMddHHmmss", ChronoUnit.SECONDS)
            .put("yyyyMMddhh24", ChronoUnit.HOURS)
            .put("yyyyMMddhh24mm", ChronoUnit.MINUTES)
            .put("yyyyMMddhh24mmss", ChronoUnit.SECONDS)
            .put("HH:mm:ss", ChronoUnit.SECONDS)
            .put("yyyy-MM-dd HH:mm:ss", ChronoUnit.SECONDS)
            .put("yyyy-MM-dd?", ChronoUnit.DAYS)
            .build();

    /**
     * 根据基准的时间转换出目标时间
     *
     * @param command
     * @param cycTime 必须满足STD_FMT
     * @return
     */
    public static String transform(String command, String cycTime) {
        return transform(command, cycTime, null);
    }

    public static String transform(String command, String cycTime, Offset offset) {
        command = command.trim();
        if (StringUtils.isBlank(command)) {
            return "";
        } else if (command.equals(STD_FMT)) {
            return cycTime;
        } else if (command.equals(DAY_FMT)) {
            return StringUtils.substring(cycTime, 0, 8);
        } else {
            return dealTimeOperator(command, cycTime, offset);
        }
    }

    public static String dealTimeOperator(String command, String cycTime, Offset offset) {
        //增加一种系统参数。格式：yyyy-MM-dd,-1 逗号左侧为format，右边为时间运算表达式
        String[] split = command.split(",");
        String realCommand;
        String realOperator;
        String realOperatorNum;
        if (split.length == 2) {
            realCommand = split[0];
            realOperator = split[1].substring(0, 1);
            realOperatorNum = split[1].substring(1);
        } else {
            Matcher matcher = pattern.matcher(command);
            if (!(matcher.find() && matcher.groupCount() == 3)) {
                throw new RdosDefineException("illegal command " + command);
            }
            String timeFmtStr = matcher.group(1).trim();
            realCommand = timeFmtStr;
            String operatorStr = matcher.group(2).trim();
            realOperator = operatorStr;
            String operatorNumStr = matcher.group(3).trim();
            realOperatorNum = operatorNumStr;
        }

        int operatorNum = Offset.calculate(offset,MathUtil.getIntegerVal(realOperatorNum),realOperator);

        if ("-".equals(realOperator)) {
            if (realCommand.length() == 10) {
                return minusDay(operatorNum, cycTime, realCommand);
            } else if (realCommand.length() == 8) {
                return minusDay(operatorNum, cycTime, realCommand);
            } else if (realCommand.length() == 6) {
                return minusMonth(operatorNum, cycTime, realCommand);
            } else if (realCommand.length() == 4) {
                return minusYear(operatorNum, cycTime, realCommand);
            } else {
                throw new RdosDefineException("illegal command " + command);
            }
        } else if ("+".equals(realOperator)) {
            if (realCommand.length() == 10) {
                return plusDay(operatorNum, cycTime, realCommand);
            } else if (realCommand.length() == 8) {
                return plusDay(operatorNum, cycTime, realCommand);
            } else if (realCommand.length() == 6) {
                return plusMonth(operatorNum, cycTime, realCommand);
            } else if (realCommand.length() == 4) {
                return plusYear(operatorNum, cycTime, realCommand);
            } else {
                throw new RdosDefineException("illegal command " + command);
            }
        } else {
            throw new RdosDefineException("illegal command " + command);
        }

    }

    public static String dealCustomizeTimeOperator(String command, String cycTime) {
        return dealCustomizeTimeOperator(command, cycTime, null,null);
    }

    public static String dealCustomizeTimeOperator(String command, String cycTime, Offset offset) {
        return dealCustomizeTimeOperator(command, cycTime, offset, null);
    }

    public static String dealCustomizeTimeOperator(String command, String cycTime, Timestamp runtime) {
        return dealCustomizeTimeOperator(command, cycTime, null,runtime);
    }

    public static String dealCustomizeTimeOperator(String command, String cycTime, Offset offset, Timestamp runtime) {
        String result = "";
        String split = null;
        String timeFmtStr = "";
        if (command.startsWith("$[") && command.endsWith("]")) {  //需要计算的变量
            String line = command.substring(2, command.indexOf("]")).trim();
            if (line.startsWith("format")) {
                return doFormatFunction(line, cycTime, offset);
            } else if (line.startsWith("add_months")) {
                String params = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                String[] paramsArrays = params.split(",");
                timeFmtStr = paramsArrays[0].trim();
                if (paramsArrays.length > 2) {
                    //第三个参数为连接符
                    split = paramsArrays[2].replaceAll("'", "");
                }
                if (YEAR_FMT.equals(timeFmtStr)) {
                    String year = StringUtils.deleteWhitespace(paramsArrays[1]);
                    if (!year.contains("*")) {
                        result = plusYear(Offset.calculate(offset, MathUtil.getIntegerVal(year)), cycTime, timeFmtStr);
                    } else {
                        int m = MathUtil.getIntegerVal(year.split("\\*")[0]);
                        result = plusYear(Offset.calculate(offset, m), cycTime, timeFmtStr);
                    }
                } else if (DAY_FMT.equals(timeFmtStr) || MONTH_FMT.equals(timeFmtStr)) {
                    String months = StringUtils.deleteWhitespace(paramsArrays[1]);
                    if (!months.contains("*")) {
                        result = plusMonth(Offset.calculate(offset, MathUtil.getIntegerVal(months)), cycTime, timeFmtStr);
                    } else {
                        int m = MathUtil.getIntegerVal(months.split("\\*")[0]);
                        int n = MathUtil.getIntegerVal(months.split("\\*")[1]);
                        if (Math.abs(m) != 12 && Math.abs(n) != 12) {
                            throw new RdosDefineException("illegal command " + command);
                        }
                        result = plusMonth(Offset.calculate(offset, m * n), cycTime, timeFmtStr);
                    }
                }
            } else {  //没有函数
                if (line.contains(",")) {
                    String[] paramsArrays = line.split(",");
                    line = paramsArrays[0];
                    split = String.valueOf(paramsArrays[1]).replaceAll("'", "");
                }
                Matcher matcher = customizePattern.matcher(line);
                if (matcher.find() && matcher.groupCount() == 3) {
                    timeFmtStr = matcher.group(1).trim();
                    String operatorStr = matcher.group(2).trim();
                    String operatorNumStr = StringUtils.deleteWhitespace(matcher.group(3));
                    if (DAY_FMT.equals(timeFmtStr)) {
                        int days = 0;
                        if (!operatorNumStr.contains("*")) {
                            days = MathUtil.getIntegerVal(operatorNumStr);
                        } else if (operatorNumStr.split("\\*").length == 2) {
                            int m = Integer.parseInt(operatorNumStr.split("\\*")[0]);
                            int n = Integer.parseInt(operatorNumStr.split("\\*")[1]);
                            if (m != 7 && n != 7) {
                                throw new RdosDefineException("illegal command " + command);
                            }
                            days = m * n;
                        }
                        if ("-".equals(operatorStr)) {
                            result = minusDay(Offset.calculate(offset, days), cycTime, timeFmtStr);
                        } else if ("+".equals(operatorStr)) {
                            result = plusDay(Offset.calculate(offset, days), cycTime, timeFmtStr);
                        }
                    } else if ("hh24miss".equals(timeFmtStr)) {
                        int time;
                        if (operatorNumStr.split("/").length == 2) {  //小时
                            time = MathUtil.getIntegerVal(operatorNumStr.split("/")[0]);
                            if ("-".equals(operatorStr)) {
                                result = minusHour(Offset.calculate(offset, time), cycTime, STD_FMT);
                            }
                            if ("+".equals(operatorStr)) {
                                result = plusHour(Offset.calculate(offset, time), cycTime, STD_FMT);
                            }
                        } else if (operatorNumStr.split("/").length == 3) {  //分钟
                            time = MathUtil.getIntegerVal(operatorNumStr.split("/")[0]);
                            if ("-".equals(operatorStr)) {
                                result = minusMinute(Offset.calculate(offset, time), cycTime, STD_FMT);
                            }
                            if ("+".equals(operatorStr)) {
                                result = plusMinute(Offset.calculate(offset, time), cycTime, STD_FMT);
                            }
                        }
                    }
                }
                else if (formattedPattern.matcher(line).matches()) {
                    //时间格式化
                    try {
                        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
                        dateTime = slipOffset(line, dateTime, offset);

                        Date cycd = dateTime.toDate();

                        if (line.contains("hh24")) {
                            line = line.replaceAll("hh24", "HH");
                        }
                        result = FastDateFormat.getInstance(line, TimeZone.getTimeZone("GMT+8")).format(cycd);
                    } catch (Exception e) {
                        LOGGER.error("TimeParamOperator.dealCustomizeTimeOperator error:", e);
                    }
                }
            }
            if (StringUtils.isBlank(result)) {
                throw new RdosDefineException("illegal command " + command);
            }
            if (StringUtils.isNotEmpty(split) && StringUtils.isNotEmpty(result)) {
                if (StringUtils.isBlank(timeFmtStr)) {
                    timeFmtStr = line;
                }
                return convertResultWithSplit(result, timeFmtStr, split.trim());
            }
            return result;
        } else if (command.startsWith("${") && command.endsWith("}")) {
            // 特殊处理 ${bdp.system.currenttime}
            String realCommand = command.substring(2, command.length() - 1).trim();
            ESystemTimeSpecialParam eSystemTimeSpecialParam = ESystemTimeSpecialParam.getESystemTimeParam(realCommand);
            if (eSystemTimeSpecialParam != null) {
                SystemTimeParamQueryDTO systemTimeParamQueryDTO = SystemTimeParamQueryDTO.SystemTimeParamQueryDTOBuilder.builder()
                        .realCommand(realCommand)
                        .cycTime(cycTime)
                        .runtime(runtime)
                        .build();
                return eSystemTimeSpecialParam.getParsedValue(systemTimeParamQueryDTO);
            }

            // 支持基于业务日期作为基准取值的格式 时间减一天，其余照原逻辑处理 不多做任何校验
            String yesterdayCycTime = minusDay(1, cycTime, STD_FMT);
            String normalCommand = command.replaceFirst("\\{", "[").replaceFirst("}", "]");
            return dealCustomizeTimeOperator(normalCommand, yesterdayCycTime, offset);
        } else if (command.startsWith("$(") && command.endsWith(")")) {
            // 支持基于当前时间作为基准取值的格式，其余照原逻辑处理 不多做任何校验
            String currentTime = new SimpleDateFormat(STD_FMT).format(new Date());
            String normalCommand = command.replaceFirst("\\(", "[");
            normalCommand = normalCommand.substring(0, normalCommand.length() - 1) + "]";
            return dealCustomizeTimeOperator(normalCommand, currentTime, offset);
        } else {
            return command;  //直接返回
        }
    }

    private static DateTime slipOffset(String format, DateTime dateTime, Offset offset) {
        if (Objects.isNull(offset)) {
            return dateTime;
        }
        ChronoUnit unit = FORMAT_UNIT_MAPPING.get(format);
        int offsetNum = offset.getNum();
        String op = offset.getOp();

        if (Objects.isNull(unit) || offsetNum == 0) {
            return dateTime;
        }
        switch (unit) {
            case YEARS:
                if ("+".equals(op)) {
                    return dateTime.plusYears(offsetNum);
                } else {
                    return dateTime.minusYears(offsetNum);
                }
            case MONTHS:
                if ("+".equals(op)) {
                    return dateTime.plusMonths(offsetNum);
                } else {
                    return dateTime.minusMonths(offsetNum);
                }
            case DAYS:
                if ("+".equals(op)) {
                    return dateTime.plusDays(offsetNum);
                } else {
                    return dateTime.minusDays(offsetNum);
                }
            case HOURS:
                if ("+".equals(op)) {
                    return dateTime.plusHours(offsetNum);
                } else {
                    return dateTime.minusHours(offsetNum);
                }
            case MINUTES:
                if ("+".equals(op)) {
                    return dateTime.plusMinutes(offsetNum);
                } else {
                    return dateTime.minusMinutes(offsetNum);
                }
            case SECONDS:
                if ("+".equals(op)) {
                    return dateTime.plusSeconds(offsetNum);
                } else {
                    return dateTime.minusSeconds(offsetNum);
                }
            default:
                return dateTime;
        }
    }

    /**
     * 处理 format 函数
     *
     * @param line
     * @param cycTime
     * @return
     */
    private static String doFormatFunction(String line, String cycTime, Offset offset) {
        Matcher formattedMatch = FORMAT_PATTERN.matcher(line);
        if (!formattedMatch.find()) {
            throw new RdosDefineException("illegal command " + line);
        }

        // 获取需要处理的属性
        String format = formattedMatch.group("format");
        Boolean operate = "+".equals(formattedMatch.group("operate"));
        Integer number = StringUtils.isBlank(formattedMatch.group("number")) ? 0 : Integer.valueOf(formattedMatch.group("number"));
        String unit = formattedMatch.group("unit");
        String formatResult = formattedMatch.group("formatResult");
        return doFormatFunctionCyctime(cycTime, operate, Offset.calculate(offset, number), unit, formatResult);
    }

    /**
     * 处理时间 3y，3M，3d，3H，3m，3s
     * 基础单位 yyyyMMddHHmmss
     *
     * @param cycTime
     * @param operate      操作 + / -
     * @param number       数量
     * @param unit         单位
     * @param formatResult 返回值结构
     * @return
     */
    private static String doFormatFunctionCyctime(String cycTime, Boolean operate, Integer number, String unit, String formatResult) {
        switch (unit) {
            case "y":
                return operate ? plusYear(number, cycTime, formatResult) : minusYear(number, cycTime, formatResult);

            case "M":
                return operate ? plusMonth(number, cycTime, formatResult) : minusMonth(number, cycTime, formatResult);


            case "d":
                return operate ? plusDay(number, cycTime, formatResult) : minusDay(number, cycTime, formatResult);


            case "H":
                return operate ? plusHour(number, cycTime, formatResult) : minusHour(number, cycTime, formatResult);


            case "m":
                return operate ? plusMinute(number, cycTime, formatResult) : minusMinute(number, cycTime, formatResult);


            case "w":
                return operate ? plusDay(number * 7, cycTime, formatResult) : minusDay(number * 7, cycTime, formatResult);


            default:
                return operate ? plusSecond(number, cycTime, formatResult) : minusSecond(number, cycTime, formatResult);
        }
    }

    private static String convertResultWithSplit(String result, String timeFmtStr, String split) {
        String fmt = "";
        StringBuilder convertResult = new StringBuilder();
        if ("hh24miss".equals(timeFmtStr)) {
            timeFmtStr = STD_FMT;
        }
        for (int i = 0; i < timeFmtStr.length(); i++) {
            if (StringUtils.isBlank(fmt)) {
                fmt = String.valueOf(timeFmtStr.charAt(i));
            }
            if (!fmt.equals(String.valueOf(timeFmtStr.charAt(i)))) {
                convertResult.append(split);
                fmt = String.valueOf(timeFmtStr.charAt(i));
            }
            convertResult.append(result.charAt(i));
        }
        return convertResult.toString();
    }

    private static String dealDateTimeFormat(DateTime dateTime, String format) {
        // 如果为 Unix 时间戳
        if ("UnixTimestamp10".equalsIgnoreCase(format.trim())) {
            return String.valueOf(dateTime.getMillis() / 1000);
        }

        // 如果为 Unix 时间戳
        if ("UnixTimestamp13".equalsIgnoreCase(format.trim())) {
            return String.valueOf(dateTime.getMillis());
        }

        if ("ISODate".equalsIgnoreCase(format.trim())) {
            format = ISODATE_FORMATE;
        }

        return dateTime.toString(format);
    }

    public static String plusHour(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusHours(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String minusHour(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusHours(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String plusMinute(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusMinutes(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String minusMinute(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusMinutes(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String plusSecond(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusSeconds(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String minusSecond(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusSeconds(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String plusDay(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusDays(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String minusDay(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusDays(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String plusMonth(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusMonths(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String minusMonth(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusMonths(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String plusYear(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusYears(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static String minusYear(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusYears(n);
        return dealDateTimeFormat(dateTime, format);
    }

    public static DateTime parseCycTime(String cycTime) {
        return cycTimeFormat.parseDateTime(cycTime);
    }

    public static DateTime parseStrToDateTime(String timeStr,String timePattern) {

        if(DAY_FMT.equals(timePattern)){
            return timeFormatter1.parseDateTime(timeStr);
        }else if(DAY_FMT2.equals(timePattern)){
            return timeFormatter2.parseDateTime(timeStr);
        }else{
            throw new RdosDefineException("未支持的格式转换,timePattern:{}",timePattern);
        }

    }


    /**
     * 拆分出常规命令的各元素, 支持的常规命令类似 "yyyy-MM-dd", "yyyy-MM-dd,-0", "yyyyMMdd-0"
     *
     * @param paramCommand 如 "yyyy-MM-dd", "yyyy-MM-dd,-0", "yyyyMMdd-0"
     * @return TimeParamComposition
     */
    public static TimeParamComposition trans2Composition(String paramCommand) {
        if (StringUtils.isBlank(paramCommand)) {
            return null;
        }
        // 先匹配特定字符串
        if (formattedPattern.matcher(paramCommand).matches()) {
            return new TimeParamComposition(paramCommand);
        }

        String[] split = paramCommand.split(",");
        if (split.length == 2) {
            // 符号
            String operatorStr = split[1].substring(0, 1);
            if (!"+".equals(operatorStr) && !"-".equals(operatorStr)) {
                throw new RdosDefineException("illegal command " + paramCommand);
            }

            // 操作数
            String numberStr = split[1].substring(1);
            Integer number;
            try {
                number = MathUtil.getIntegerVal(numberStr);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new RdosDefineException("illegal command " + paramCommand);
            }
            if (number == null) {
                throw new RdosDefineException("illegal command " + paramCommand);
            }
            return new TimeParamComposition(split[0], operatorStr, number);
        } else {
            Matcher matcher = pattern.matcher(paramCommand);
            if (!(matcher.find() && matcher.groupCount() == 3)) {
                throw new RdosDefineException("illegal command " + paramCommand);
            }
            return new TimeParamComposition(matcher.group(1).trim(), matcher.group(2).trim(),
                    MathUtil.getIntegerVal(matcher.group(3).trim()));
        }
    }

    public static class TimeParamComposition {
        /**
         * 日期格式, 比如 yyyy-MM-dd, yyyyMMdd
         */
        private String realCommand;
        /**
         * 符号, 比如 +/-
         */
        private String operator;
        /**
         * 数字, 比如 0
         */
        private Integer operatorNum;

        public String getRealCommand() {
            return realCommand;
        }

        public void setRealCommand(String realCommand) {
            this.realCommand = realCommand;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Integer getOperatorNum() {
            return operatorNum;
        }

        public void setOperatorNum(Integer operatorNum) {
            this.operatorNum = operatorNum;
        }

        public TimeParamComposition(String realCommand) {
            this.realCommand = realCommand;
        }

        public TimeParamComposition(String realCommand, String operator, Integer operatorNum) {
            this.realCommand = realCommand;
            this.operator = operator;
            this.operatorNum = operatorNum;
        }

        public boolean hasOperator() {
            return StringUtils.isNotBlank(operator);
        }

        /**
         * 没有操作数，或者操作数为 0
         *
         * @return
         */
        public boolean equivalentToZeroOperator() {
            return operatorNum == null || operatorNum.equals(0);
        }

        public String format2yyyyMMdd(String cycTime) {
            DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
            return dealDateTimeFormat(dateTime, DAY_FMT);
        }
    }

    public static class Offset {
        private String op;
        private int num;
        private String replaceTarget;

        public Offset(String op, int num,String replaceTarget) {
            this.op = op;
            this.num = num;
            this.replaceTarget = replaceTarget;
        }

        public static int calculate(Offset offset, Integer base,String baseOperator) {
            try {
                if (Objects.isNull(offset) || Objects.isNull(base) || offset.getNum() <= 0) {
                    return base;
                }
                String op = offset.getOp();
                int num = offset.getNum();
                if (op.equals(baseOperator)) {
                    return num + base;
                }

                if ("-".equals(baseOperator)) {
                    // op + , baseOp -
                    return num > base ? base - num : num - base ;
                }
                if ("+".equals(baseOperator)) {
                    return base - num;
                }
                return base;
            } catch (Throwable e) {
                LOGGER.error("calculate offset,error:{}, offset:{}, base:{}", e.getMessage(), JSONObject.toJSONString(offset), base, e);
                throw new RdosDefineException(String.format("calculate offset,error:%s, offset:%s, base:%s",e.getMessage(), JSONObject.toJSONString(offset), base));
            }
        }

        public static int calculate(Offset offset, Integer base) {
            return calculate(offset,base,"-");
        }

        public static Offset transfer(String offsetStr,String replaceTarget) {
            try {
                if (StringUtils.isNotBlank(offsetStr) && !GLOBAL_PARAM_OFFSET_PATTERN.matcher(offsetStr).find()) {
                    throw new RdosDefineException("错误的偏移量");
                }
                if (StringUtils.isBlank(offsetStr)) {
                    return null;
                }
                return new Offset(offsetStr.substring(0, 1), Integer.parseInt(offsetStr.substring(1)),replaceTarget);
            } catch (Throwable e) {
                LOGGER.error("transfer offset:{} error:{}", offsetStr, e.getMessage(), e);
                throw new RdosDefineException(String.format("transfer offset:%s error:%s", offsetStr, e.getMessage()));
            }
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getReplaceTarget() {
            return replaceTarget;
        }

        public void setReplaceTarget(String replaceTarget) {
            this.replaceTarget = replaceTarget;
        }
    }
}
