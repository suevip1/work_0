package com.dtstack.engine.api.vo.task;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskTimeVO
 * @date 2022/7/18 5:01 PM
 */
public class TaskTimeVO {

    /**
     * 查询任务创建（提交）时间的最小时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private String startCreateDate;

    /**
     * 查询任务创建（提交）时间的最大时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private String endCreateDate;

    /**
     * 查询任务更新时间的最小时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private String startUpdateDate;

    /**
     * 查询任务更新时间的最大时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private String endUpdateDate;


    public String getStartCreateDate() {
        return startCreateDate;
    }

    public void setStartCreateDate(String startCreateDate) {
        this.startCreateDate = startCreateDate;
    }

    public String getEndCreateDate() {
        return endCreateDate;
    }

    public void setEndCreateDate(String endCreateDate) {
        this.endCreateDate = endCreateDate;
    }

    public String getStartUpdateDate() {
        return startUpdateDate;
    }

    public void setStartUpdateDate(String startUpdateDate) {
        this.startUpdateDate = startUpdateDate;
    }

    public String getEndUpdateDate() {
        return endUpdateDate;
    }

    public void setEndUpdateDate(String endUpdateDate) {
        this.endUpdateDate = endUpdateDate;
    }
}
