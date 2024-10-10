package com.dtstack.engine.api.vo.console.param;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-13 20:17
 */
public class ConsoleCalenderTimeShowVO {
    /**
     * 参数 id
     */
    private Long paramId;

    /**
     * 日历 id
     */
    private Long calenderId;

    /**
     * csv 文件名称
     */
    private String csvFileName;

    /**
     * 时间列表 -- 用于日历展示
     */
    private List<String> times;

    /**
     * 最后一个自定义日期 -- 用于界面展示
     */
    private String latestCalenderTime;

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public String getLatestCalenderTime() {
        return latestCalenderTime;
    }

    public void setLatestCalenderTime(String latestCalenderTime) {
        this.latestCalenderTime = latestCalenderTime;
    }

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }
}
