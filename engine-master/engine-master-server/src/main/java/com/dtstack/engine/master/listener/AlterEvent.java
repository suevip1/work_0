package com.dtstack.engine.master.listener;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/20 2:36 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class AlterEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlterEvent.class);

    private final List<AlterListener> listeners;

    public AlterEvent() {
        this.listeners = Lists.newArrayList();
    }

    /**
     * 注册监听器
     */
    public void registerEventListener(AlterListener listener) {
        listeners.add(listener);
    }

    public void event(final AlterEventContext context) {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }

        for (AlterListener listener : listeners) {
            String jobId = context.getJobId();
            try {
                LOGGER.info("send jobId {} alter context {}", jobId, listener.getClass());
                listener.event(context);
            } catch (Throwable e) {
                LOGGER.error("jobId {} event error: ", jobId, e);
            }
        }
    }
}
