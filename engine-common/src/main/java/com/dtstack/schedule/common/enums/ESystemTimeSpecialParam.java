package com.dtstack.schedule.common.enums;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.dto.SystemTimeParamQueryDTO;
import com.dtstack.schedule.common.util.TimeParamOperator;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-02-08 09:53
 * 需要特殊处理的系统时间参数
 */
public enum ESystemTimeSpecialParam {

    /**
     * 运行时间
     */
    SYSTEM_CURRENTTIME("bdp.system.runtime", "bdp.system.currenttime") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            Timestamp runtime = systemTimeParamQueryDTO.getRuntime();
            if (Objects.nonNull(runtime)) {
                return new SimpleDateFormat(TimeParamOperator.STD_FMT).format(runtime);
            }
            return new SimpleDateFormat(TimeParamOperator.STD_FMT).format(new Date());
        }
    },

    /**
     * 上季末,yyyyMMdd格式
     */
    SYSTEM_PRE_QRTR_END("bdp.system.preqrtrend", "bdp.system.preqrtrend") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            return lastQuarterEnd(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT);
        }
    },

    /**
     * 上季末，yyyy-MM-dd格式
     */
    SYSTEM_PRE_QRTR_END2("bdp.system.preqrtrend2", "bdp.system.preqrtrend2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            return lastQuarterEnd(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT2);
        }
    },

    /**
     * 上季初,yyyyMMdd格式
     */
    BDP_SYSTEM_PREQTRSTART("bdp.system.preqrtrstart", "bdp.system.preqrtrstart") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            return lastQuarterStart(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT);
        }
    },

    /**
     * 上季初，yyyy-MM-dd格式
     */
    BDP_SYSTEM_PREQTRSTART2("bdp.system.preqrtrstart2", "bdp.system.preqrtrstart2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            return lastQuarterStart(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT2);
        }
    },

    /**
     * 上月初，yyyyMMdd格式
     */
    BDP_SYSTEM_MONTH_START("bdp.system.premonthstart", "bdp.system.premonthstart") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            DateTime cycTime = TimeParamOperator.parseCycTime(systemTimeParamQueryDTO.getCycTime());
            return cycTime.minusMonths(1).dayOfMonth().withMinimumValue().toString(TimeParamOperator.DAY_FMT);
        }
    },


    /**
     * 上月初，yyyy-MM-dd格式
     */
    BDP_SYSTEM_MONTH_START2("bdp.system.premonthstart2", "bdp.system.premonthstart2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            DateTime cycTime = TimeParamOperator.parseCycTime(systemTimeParamQueryDTO.getCycTime());
            return cycTime.minusMonths(1).dayOfMonth().withMinimumValue().toString(TimeParamOperator.DAY_FMT2);
        }
    },


    /**
     * 上月末，yyyyMMdd格式
     */
    BDP_SYSTEM_MONTH_END("bdp.system.premonthend", "bdp.system.premonthend") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            DateTime cycTime = TimeParamOperator.parseCycTime(systemTimeParamQueryDTO.getCycTime());
            return cycTime.minusMonths(1).dayOfMonth().withMaximumValue().toString(TimeParamOperator.DAY_FMT);
        }
    },


    /**
     * 上月末，yyyy-MM-dd格式
     */
    BDP_SYSTEM_MONTH_END2("bdp.system.premonthend2", "bdp.system.premonthend2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            DateTime cycTime = TimeParamOperator.parseCycTime(systemTimeParamQueryDTO.getCycTime());
            return cycTime.minusMonths(1).dayOfMonth().withMaximumValue().toString(TimeParamOperator.DAY_FMT2);
        }
    },

    /**
     * 上年初，yyyyMMdd格式
     */
    BDP_SYSTEM_PREYRSTART("bdp.system.preyrstart", "bdp.system.preyrstart") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            DateTime cycTime = TimeParamOperator.parseCycTime(systemTimeParamQueryDTO.getCycTime());
            return cycTime.minusYears(1).withDayOfYear(1).toString(TimeParamOperator.DAY_FMT);
        }
    },

    /**
     * 上年初，yyyy-MM-dd格式
     */
    BDP_SYSTEM_PREYRSTART2("bdp.system.preyrstart2", "bdp.system.preyrstart2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            DateTime cycTime = TimeParamOperator.parseCycTime(systemTimeParamQueryDTO.getCycTime());
            return cycTime.minusYears(1).withDayOfYear(1).toString(TimeParamOperator.DAY_FMT2);
        }
    },


    /**
     * 上年末,yyyyMMdd格式
     */
    SYSTEM_PRE_YR_END("bdp.system.preyrend", "bdp.system.preyrend") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            return lastYearEnd(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT);
        }
    },

    /**
     * 上年末，yyyy-MM-dd格式
     */
    SYSTEM_PRE_YR_END2("bdp.system.preyrend2", "bdp.system.preyrend2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            return lastYearEnd(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT2);
        }
    },

    /**
     * 当季初，yyyyMMdd格式
     */
    SYSTEM_PRE_QRTR_START("bdp.system.qrtrstart", "bdp.system.qrtrstart") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            String parsedValue = lastQuarterEnd(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT);
            DateTime dateTime = TimeParamOperator.parseStrToDateTime(parsedValue, TimeParamOperator.DAY_FMT);
            //上季末+1天就是当季初
            return dateTime.plusDays(1).toString(TimeParamOperator.DAY_FMT);
        }
    },

    /**
     * 当季初，yyyy-MM-dd格式
     */
    SYSTEM_PRE_QRTR_START2("bdp.system.qrtrstart2", "bdp.system.qrtrstart2") {
        @Override
        public String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO) {
            String parsedValue = lastQuarterEnd(systemTimeParamQueryDTO.getCycTime(), TimeParamOperator.DAY_FMT2);
            DateTime dateTime = TimeParamOperator.parseStrToDateTime(parsedValue, TimeParamOperator.DAY_FMT2);
            //上季末+1天就是当季初
            return dateTime.plusDays(1).toString(TimeParamOperator.DAY_FMT2);
        }
    };


    /**
     * 备注：使用 HashMap 缓存，支持返回 null
     */
    private static final Map<String, ESystemTimeSpecialParam> allParamCommandMap = new HashMap<>(16);

    /**
     * 所有参数名称
     */
    private static final Set<String> allParamNameSet = new HashSet<>(16);

    /**
     * 所有的季末
     */
    private static final int[] ALL_QUARTER_END = new int[]{3, 6, 9, 12};
    private static final int[] ALL_QUARTER_START = new int[]{1, 4, 7, 10};

    static {
        for (ESystemTimeSpecialParam eSystemTimeSpecialParam : values()) {
            allParamNameSet.add(eSystemTimeSpecialParam.name);
            allParamCommandMap.put(eSystemTimeSpecialParam.realCommand, eSystemTimeSpecialParam);
        }
    }

    public static ESystemTimeSpecialParam getESystemTimeParam(String realCommand) {
        ESystemTimeSpecialParam eSystemTimeSpecialParam = allParamCommandMap.get(realCommand);
        return eSystemTimeSpecialParam;
    }

    /**
     * 上季末，格式 yyyyMMdd
     * 季末时间范围：3.31、6.30、9.30、12.31
     *
     * @param cycTime
     * @return
     */
    private static String lastQuarterEnd(String cycTime, String timePattern) {
        int currentMonth = Integer.parseInt(cycTime.substring(4, 6));
        int lastQuarterEnd = -1;
        for (int i = 0; i < ALL_QUARTER_END.length; i++) {
            int quarter = ALL_QUARTER_END[i];
            if (currentMonth <= quarter) {
                // 取小于等于 querter 的前一个索引的值
                lastQuarterEnd = ALL_QUARTER_END[(i - 1 + ALL_QUARTER_END.length) % ALL_QUARTER_END.length];
                break;
            }
        }
        if (lastQuarterEnd == -1) {
            throw new RdosDefineException(String.format("not found lastQuarterEnd, cycTime: %s", cycTime));
        }

        int currentYear = Integer.parseInt(cycTime.substring(0, 4));
        DateTime dateTime = TimeParamOperator.parseCycTime(cycTime);
        if (lastQuarterEnd == 12) {
            // 去年
            dateTime = dateTime.withYear(currentYear - 1);
        } else {
            dateTime = dateTime.withYear(currentYear);
        }
        dateTime = dateTime.withMonthOfYear(lastQuarterEnd);
        DateTime result = dateTime.dayOfMonth().withMaximumValue();
        return result.toString(timePattern);
    }

    /**
     * 上季初，格式 yyyyMMdd
     * 季末时间范围：1.1、4.1、7.1、10.1
     *
     * @param cycTime
     * @return
     */
    private static String lastQuarterStart(String cycTime, String timePattern) {
        int currentMonth = Integer.parseInt(cycTime.substring(4, 6));
        int lastQuarterStart = -1;
        for (int i = ALL_QUARTER_START.length - 1; i >= 0; i--) {
            int quarter = ALL_QUARTER_START[i];
            if (currentMonth >= quarter) {
                // 取大于等于 querter 的前一个索引的值
                lastQuarterStart = ALL_QUARTER_START[(i - 1 + ALL_QUARTER_START.length) % ALL_QUARTER_START.length];
                break;
            }
        }
        if (lastQuarterStart == -1) {
            throw new RdosDefineException(String.format("not found lastQuarterStart, cycTime: %s", cycTime));
        }

        int currentYear = Integer.parseInt(cycTime.substring(0, 4));
        DateTime dateTime = TimeParamOperator.parseCycTime(cycTime);
        if (lastQuarterStart == 10) {
            // 去年
            dateTime = dateTime.withYear(currentYear - 1);
        } else {
            dateTime = dateTime.withYear(currentYear);
        }
        dateTime = dateTime.withMonthOfYear(lastQuarterStart);
        DateTime result = dateTime.dayOfMonth().withMinimumValue();
        return result.toString(timePattern);
    }

    /**
     * 上年末，格式 yyyyMMdd
     *
     * @param cycTime
     * @return
     */
    private static String lastYearEnd(String cycTime, String datePattern) {
        int lastYear = Integer.parseInt(cycTime.substring(0, 4)) - 1;

        DateTime dateTime = TimeParamOperator.parseCycTime(cycTime);
        DateTime result = dateTime.withDate(lastYear, 12, 31);
        return result.toString(datePattern);
    }

    public static boolean containSystemTimeSpecialParam(String name) {
        return allParamNameSet.contains(name);
    }

    private final String name;
    private final String realCommand;

    ESystemTimeSpecialParam(String name, String realCommand) {
        this.name = name;
        this.realCommand = realCommand;
    }

    /**
     * 获取解析后的值
     *
     * @param systemTimeParamQueryDTO
     * @return
     */
    public abstract String getParsedValue(SystemTimeParamQueryDTO systemTimeParamQueryDTO);
}