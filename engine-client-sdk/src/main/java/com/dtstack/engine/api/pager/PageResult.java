package com.dtstack.engine.api.pager;

import io.swagger.annotations.ApiModel;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/4/27
 */
@ApiModel
public class PageResult<T> {

    @SuppressWarnings("rawtypes")
    public final static PageResult EMPTY_PAGE_RESULT = new PageResult<>();

    public static <D> PageResult<D> emptyPageResult() {
        @SuppressWarnings("unchecked")
        PageResult<D> res = (PageResult<D>) EMPTY_PAGE_RESULT;
        return res;
    }

    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private T data;
    private Object attachment;
    private Boolean success = true;
    private String msg;

    private PageResult() {
    }

    /**
     * 分页查询通用方法
     */
    public PageResult(T data, int totalCount, PageQuery pageQuery) {
        this.data = data;
        this.totalCount = totalCount;
        this.currentPage = pageQuery.getPage();
        this.pageSize = pageQuery.getPageSize();
        int totalPage = totalCount / pageSize;
        this.totalPage = (totalCount % pageSize == 0 ? totalPage : totalPage + 1);
    }

    /**
     * 分页查询可用方法
     */
    public PageResult(int currentPage, int pageSize, int totalCount, int totalPage, T data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.data = data;
    }

    public PageResult(Integer currentPage, Integer pageSize,Integer totalCount, T data) {
        this.data = data;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        int totalPage = totalCount / pageSize;
        this.totalPage = (totalCount % pageSize == 0 ? totalPage : totalPage + 1);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public PageResult<T> setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageResult<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public PageResult<T> setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public PageResult<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public T getData() {
        return data;
    }

    public PageResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Object getAttachment() {
        return attachment;
    }

    public PageResult<T> setAttachment(Object attachment) {
        this.attachment = attachment;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", data=" + data +
                ", attachment=" + attachment +
                '}';
    }
}
