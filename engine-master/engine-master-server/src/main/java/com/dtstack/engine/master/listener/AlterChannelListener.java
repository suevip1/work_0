package com.dtstack.engine.master.listener;

import com.dtstack.engine.master.strategy.AlterStrategy;
import com.dtstack.engine.master.strategy.AlterStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/5/20 3:27 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class AlterChannelListener implements AlterListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlterChannelListener.class);

    @Autowired
    private AlterEvent alterEvent;

    @Autowired
    private AlterStrategyFactory alterStrategyFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        alterEvent.registerEventListener(this);
    }

    @Override
    public void event(AlterEventContext context) {
        AlterStrategy alterStrategy = alterStrategyFactory.getAlterStrategy(context.getKey());

        if (alterStrategy != null) {
            LOGGER.info("send jobId {} alter context {},status {}", context.getJobId(), alterStrategy.getClass(),context.getStatus());
            alterStrategy.alert(context);
        }
    }
}
