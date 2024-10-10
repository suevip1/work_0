package com.dtstack.schedule.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义周期调度日期 format
 *
 * @author ：wangchuan
 * date：Created in 3:54 PM 2022/7/15
 * company: www.dtstack.com
 */
public enum CalenderTimeFormat {

    /**
     * 年月日时分
     */
    YEAR_MONTH_DAY_HOUR_MINUTE("yyyyMMddHHmm"),

    /**
     * 年月日
     */
    YEAR_MONTH_DAY("yyyyMMdd");

    CalenderTimeFormat(String format) {
        this.format = format;
    }

    private final String format;

    public String getFormat() {
        return format;
    }

    /**
     * 日历时间长度
     */
    public static final int CALENDER_TIME_LENGTH = 12;

    /**
     * 格式化时间格式为 yyyyMMddHHmm 格式, 不够位数的补 0
     *
     * @param calenderTime       日历时间, eg: 20220718、202207181010
     * @param calenderTimeFormat 日历时间格式
     * @return 格式化的时间
     */
    public static String formatCalenderTime(String calenderTime, String calenderTimeFormat) {
        CalenderTimeFormat timeFormat = timeFormat(calenderTimeFormat);
        int zeroNum = CALENDER_TIME_LENGTH - timeFormat.getFormat().length();
        StringBuilder calenderTimeBuilder = new StringBuilder(calenderTime);
        for (int i = 0; i < zeroNum; i++) {
            calenderTimeBuilder.append("0");
        }
        return calenderTimeBuilder.toString();
    }

    /**
     * 转为触发时间
     *
     * @param calenderTime       日历时间, 格式都为 yyyyMMddHHmm
     * @param calenderTimeFormat 日历时间格式
     * @param expendTime         扩展时间
     * @return 触发时间, 格式为 yyyyMMddHHmmss
     */
    public static String toTriggerTime(String calenderTime, String calenderTimeFormat, String expendTime) {
        CalenderTimeFormat timeFormat = timeFormat(calenderTimeFormat);
        // 截取指定长度
        return calenderTime.substring(0, timeFormat.getFormat().length()) + (StringUtils.isBlank(expendTime) ? "" : expendTime) + "00";
    }

    /**
     * 获取日历时间格式枚举
     *
     * @param format 选择的时间格式
     * @return 日历时间格式枚举
     */
    public static CalenderTimeFormat timeFormat(String format) {
        for (CalenderTimeFormat timeFormat : values()) {
            if (timeFormat.getFormat().equals(format)) {
                return timeFormat;
            }
        }
        // 默认为 年月日时分
        return YEAR_MONTH_DAY_HOUR_MINUTE;
    }
}
