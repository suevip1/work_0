package com.dtstack.engine.master.event;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public class ScheduleJobEventPublisher {

    private static volatile ScheduleJobEventPublisher publisher = null;

    private final Logger logger = LoggerFactory.getLogger(ScheduleJobEventPublisher.class);

    private List<ScheduleJobEventListener> scheduleJobEventMulticaster;

    private static final ExecutorService eventPublishExecutor = new ThreadPoolExecutor(1, 8,
            10L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100), new CustomThreadFactory("event-publish-executor"),
            new CustomThreadRunsPolicy("event-publish-executor-reject", "event"));


    private ScheduleJobEventPublisher() {
        this.scheduleJobEventMulticaster = new ArrayList<>();
    }

    public static ScheduleJobEventPublisher getInstance() {
        if (publisher == null) {
            synchronized (ScheduleJobEventPublisher.class) {
                if (publisher == null) {
                    publisher = new ScheduleJobEventPublisher();
                }
            }
        }
        return publisher;
    }

    public void register(ScheduleJobEventListener lister) {
        scheduleJobEventMulticaster.add(lister);
    }

    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        if (null == event) {
            return;
        }
        if (CollectionUtils.isEmpty(scheduleJobEventMulticaster)) {
            return;
        }
        if (CollectionUtils.isEmpty(event.getJobIds()) || null == event.getStatus()) {
            return;
        }
        if (RdosTaskStatus.getStoppedStatus().contains(event.getStatus())) {
            logger.info("publishBatchEvent {}", event);
        }
        for (ScheduleJobEventListener scheduleJobEventLister : scheduleJobEventMulticaster) {
            eventPublishExecutor.execute(() -> {
                try {
                    scheduleJobEventLister.publishBatchEvent(event);
                } catch (Exception e) {
                    logger.error("publish event {} error", event.getJobIds(), e);
                }
            });
        }
    }
}
