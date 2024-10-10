package com.dtstack.engine.master.scheduler.parser;

/**
 * Reason:
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum ESchedulePeriodType {

    MIN(0,"分钟"), HOUR(1,"小时"), DAY(2,"天"), WEEK(3,"周"), MONTH(4,"月"),
    CUSTOM(5,"自定义"),
    CALENDER(6,"日历");

    private int val;

    private String msg;

    ESchedulePeriodType(int val,String msg){
        this.val = val;
        this.msg = msg;
    }

    public int getVal(){
        return this.val;
    }

    public static String getMsg(int val) {
        ESchedulePeriodType[] values = values();

        for (ESchedulePeriodType value : values) {
            if (value.val == val) {
                return value.msg;
            }
        }
        return null;
    }
}
