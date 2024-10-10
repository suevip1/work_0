package com.dtstack.engine.master.event;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public interface ScheduleJobEventListener {

    void publishBatchEvent(ScheduleJobBatchEvent event);
}
