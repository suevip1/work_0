package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author leon
 * @date 2022-08-03 14:49
 **/
@ApiModel
public class ScheduleTaskRefTreeVO {

    private ScheduleTaskShade current;

    private List<ScheduleTaskShade> flatParents;

    public ScheduleTaskShade getCurrent() {
        return current;
    }

    public void setCurrent(ScheduleTaskShade current) {
        this.current = current;
    }

    public List<ScheduleTaskShade> getParents() {
        return flatParents;
    }

    public void setParents(List<ScheduleTaskShade> flatParents) {
        this.flatParents = flatParents;
    }


}
