package com.dtstack.engine.worker;

import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/26
 */
@Component
public class WorkerBeanConfig implements InitializingBean {

    @Autowired
    private EnvironmentContext environmentContext;

    @Bean
    @ConditionalOnMissingBean
    public ClientOperator clientOperator(){
        return ClientOperator.getInstance(environmentContext.getPluginPath());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String outTime = environmentContext.getTimeOutOfClient();
        System.setProperty(ConfigConstant.AKKA_WORKER_TIMEOUT,outTime);

        System.setProperty(ConfigConstant.WORKER_PROXY_QUEUE_SIZE,
                environmentContext.getQueueSizeOfClient());

        System.setProperty(ConfigConstant.WORKER_PROXY_MAX_POOL_SIZE,
                environmentContext.getMaxPoolSizeOfClient());

        System.setProperty(ConfigConstant.WORKER_PROXY_MIN_POOL_SIZE,
                environmentContext.getMinPoolSizeOfClient());

        System.setProperty(ConfigConstant.WORKER_PROXY_IDLE_KEEP_ALIVE_TIME,
                environmentContext.getIdleThreadKeepAliveTimeInMilliSeconds());
    }
}
