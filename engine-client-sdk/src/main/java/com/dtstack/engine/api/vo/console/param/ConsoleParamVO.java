package com.dtstack.engine.api.vo.console.param;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

public class ConsoleParamVO extends BaseEntity {

    @ApiModelProperty(notes = "参数值")
    private String paramValue;

    @ApiModelProperty(notes = "参数名称")
    private String paramName;

    @ApiModelProperty(notes = "参数描述")
    private String paramDesc;

    @ApiModelProperty(notes = "创建人")
    private String createUser;

    @ApiModelProperty(notes = "创建人 id")
    private Long createUserId;

    /**
     * {@code  com.dtstack.schedule.common.enums.EParamType}
     *
     */
    @ApiModelProperty(notes = "参数类型：0:系统参数, 9: 常量, 8: 时间, 10:按计划时间查找参数值； 其中 0 不能修改")
    private Integer paramType;

    /**
     * {@code  com.dtstack.schedule.common.enums.EParamType}
     * 8 ：时间
     * 9 ：常量
     * 10 ：按计划时间查找参数值
     */
    @ApiModelProperty(notes = "参数类型：0:系统参数, 9: 常量, 8: 时间, 10:按计划时间查找参数值； 其中 0 不能修改，离线展示使用")
    private Integer showParamType;

    /**
     * {@code com.dtstack.engine.common.enums.EDateBenchmark}
     * paramType = 8 才会有日期基准
     */
    @ApiModelProperty(notes = "日期基准：1 自然日，2 自定义日期")
    private Integer dateBenchmark;

    @ApiModelProperty(notes = "日期格式")
    private String dateFormat;

    @ApiModelProperty(notes = "调度日历 id")
    private Long calenderId;

    @ApiModelProperty(notes = "偏移量")
    private String offset;

    @ApiModelProperty(notes = "实际替换目标")
    private String replaceTarget;

    public Integer getShowParamType() {
        return showParamType;
    }

    public void setShowParamType(Integer showParamType) {
        this.showParamType = showParamType;
    }

    public Integer getDateBenchmark() {
        return dateBenchmark;
    }

    public void setDateBenchmark(Integer dateBenchmark) {
        this.dateBenchmark = dateBenchmark;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getReplaceTarget() {
        return replaceTarget;
    }

    public void setReplaceTarget(String replaceTarget) {
        this.replaceTarget = replaceTarget;
    }
}
