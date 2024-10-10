package com.dtstack.engine.master.utils;

public class CacheKeyUtil {

    public static final long THREE_MINUTE_MILL = 180000;

    public static final long SIX_MINUTE_MILL = 360000;

    public static final long ONE_HOUR_MILL = 3600000;

    public static final long ONE_DAY_MILL = 24 * ONE_HOUR_MILL;


    public static final String SPLIT = ":";

    public static final String APP_TYPE = "DAG:";


    public static class AlarmTaskKey{

        public static final String ALARM_TASK =  APP_TYPE + "ALARM_TASK";

        public static String flushDb(){
            return ALARM_TASK+SPLIT;
        }

    }

}
