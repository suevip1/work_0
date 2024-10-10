package com.dtstack.engine.common.enums;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Reason:
 * Date: 2017/6/2
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public enum EScheduleType {

    //周期调度(0), 补数据(1),临时运行一次
    NORMAL_SCHEDULE(0, "周期调度"), FILL_DATA(1, "补数据"), TEMP_JOB(2, "临时运行"), RESTART(3, "重跑"), MANUAL(4, "手动任务");

    private Integer type;

    private String desc;

    /**
     * 周期实例和手动任务不去校验部分依赖
     */
    public static final List<Integer> manualOperatorType = Lists.newArrayList(FILL_DATA.getType(), MANUAL.getType());

    /**
     * 支持增量的任务类型
     */
    public static final List<Integer> incrementalType = Lists.newArrayList(NORMAL_SCHEDULE.getType(), MANUAL.getType());

    EScheduleType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static String getTypeName(int type) {
        for (EScheduleType value : EScheduleType.values()) {
            if (value.getType().equals(type)) {
                return value.desc;
            }
        }
        throw new UnsupportedOperationException("未知调度类型");
    }

    public static EScheduleType getScheduleType(Integer type) {
        for (EScheduleType value : EScheduleType.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new DtCenterDefException(String.format("未知调度类型: %s", type));
    }
}
