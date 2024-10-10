package com.dtstack.engine.api.vo.schedule.job;

import com.dtstack.engine.api.vo.task.TaskCustomParamVO;
import com.dtstack.engine.api.vo.task.TaskKeyVO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:40 下午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillDataInfoEnhanceVO {

    /**
     * 补数据类型： 0 批量补数据 1 按照条件补数据 2 按照依赖关系补数据
     * 如果
     * fillDataType = 0时，taskIds字段有效。
     * fillDataType = 1 projects、whitelist、blacklist 有效
     * 必填
     */
    @NotNull(message = "fillDataType is not null")
    @Min(value = 0,message = " Supplement data type: 0 Batch supplement data 1 Project supplement data")
    @Max(value = 2,message = " Supplement data type: 0 Batch supplement data 1 Project supplement data")
    private Integer fillDataType;

    /**
     * 批量补数据
     *
     * fillDataType = 0 时必填
     */
    private List<TaskKeyVO> chooseTaskInfo;

    /**
     * 按照条件补数据
     * 当fillDataType = 1 时有效
     */
    private FillDataConditionInfoVO conditionInfo;

    /**
     * 按照依赖关系
     * 当fillDataType = 2 时有效
     */
    private FillDataRelyInfo relyInfo;

    /**
     * 白名单列表
     */
    private List<TaskKeyVO> whitelist;

    /**
     * 黑名单列表
     */
    private List<TaskKeyVO> blacklist;

    /**
     * 补数据自定义信息
     */
    private List<TaskCustomParamVO> taskCustomParamVOList;

    public Integer getFillDataType() {
        return fillDataType;
    }

    public void setFillDataType(Integer fillDataType) {
        this.fillDataType = fillDataType;
    }

    public List<TaskKeyVO> getChooseTaskInfo() {
        return chooseTaskInfo;
    }

    public void setChooseTaskInfo(List<TaskKeyVO> chooseTaskInfo) {
        this.chooseTaskInfo = chooseTaskInfo;
    }

    public FillDataConditionInfoVO getConditionInfo() {
        return conditionInfo;
    }

    public void setConditionInfo(FillDataConditionInfoVO conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    public FillDataRelyInfo getRelyInfo() {
        return relyInfo;
    }

    public void setRelyInfo(FillDataRelyInfo relyInfo) {
        this.relyInfo = relyInfo;
    }

    public List<TaskKeyVO> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<TaskKeyVO> whitelist) {
        this.whitelist = whitelist;
    }

    public List<TaskKeyVO> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<TaskKeyVO> blacklist) {
        this.blacklist = blacklist;
    }

    public List<TaskCustomParamVO> getTaskCustomParamVOList() {
        return taskCustomParamVOList;
    }

    public void setTaskCustomParamVOList(List<TaskCustomParamVO> taskCustomParamVOList) {
        this.taskCustomParamVOList = taskCustomParamVOList;
    }
}
