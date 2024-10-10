package com.dtstack.engine.master.dto;

import com.dtstack.engine.po.BaselineBlockJobRecord;

/**
 * @Auther: dazhi
 * @Date: 2022/6/8 4:42 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineBlockDTO {

    private Integer baselineStatus;

    private BaselineBlockJobRecord baselineBlockJobRecord;

    public Integer getBaselineStatus() {
        return baselineStatus;
    }

    public void setBaselineStatus(Integer baselineStatus) {
        this.baselineStatus = baselineStatus;
    }

    public BaselineBlockJobRecord getBaselineBlockJobRecord() {
        return baselineBlockJobRecord;
    }

    public void setBaselineBlockJobRecord(BaselineBlockJobRecord baselineBlockJobRecord) {
        this.baselineBlockJobRecord = baselineBlockJobRecord;
    }
}
