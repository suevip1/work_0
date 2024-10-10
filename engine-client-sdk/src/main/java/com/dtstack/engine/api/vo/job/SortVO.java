package com.dtstack.engine.api.vo.job;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName SortVO
 * @date 2022/7/19 2:33 PM
 */
public class SortVO {

    /**
     * 排序字段：
     * 0 按照业务日期排序
     * 1 按照计划时间排序
     * 2 按照实际开始时间排序
     * 3 按照实际结束时间排序
     * 4 按照运行时长排序
     * 5 按照重试次数排序
     */
    private Integer sortFiled;

    /**
     * 排序类型：0 正序 1 倒序
     */
    private Integer sortType;

    public Integer getSortFiled() {
        return sortFiled;
    }

    public void setSortFiled(Integer sortFiled) {
        this.sortFiled = sortFiled;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }
}
