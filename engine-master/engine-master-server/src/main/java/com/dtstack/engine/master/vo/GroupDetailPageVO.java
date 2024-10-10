package com.dtstack.engine.master.vo;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 * @date 2022-09-13 14:09
 **/
public class GroupDetailPageVO {

    // 未杀死的任务
    private Integer notYetKilledJobCount;

    private List<Map<String, Object>> pageData;

    public Integer getNotYetKilledJobCount() {
        return notYetKilledJobCount;
    }

    public void setNotYetKilledJobCount(Integer notYetKilledJobCount) {
        this.notYetKilledJobCount = notYetKilledJobCount;
    }

    public List<Map<String, Object>> getPageData() {
        return pageData;
    }

    public void setPageData(List<Map<String, Object>> pageData) {
        this.pageData = pageData;
    }
}
