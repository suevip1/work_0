package com.dtstack.engine.master.compatible;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.master.impl.ComponentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author yuebai
 * @date 2022/12/12
 */
@Service
public class PublicServiceCompatibleService implements ApplicationListener<ApplicationStartedEvent> {


    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ComponentService componentService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        //历史的meta数据源 推送到业务中心
        //需要包含kerberos信息 sql处理不了
        CompletableFuture.runAsync(this::updateMetaToPublicService);
    }

    private void updateMetaToPublicService() {
        Thread.currentThread().setName("PublicServiceCompatibleThread");
        List<Cluster> clusters = clusterDao.listAll();
        for (Cluster cluster : clusters) {
            Component metadataComponent = componentDao.getMetadataComponent(cluster.getId());
            if (metadataComponent != null) {
                try {
                    componentService.updateCache(cluster.getId(), metadataComponent.getComponentTypeCode());
                } catch (Exception e) {
                    LOGGER.error("compatible cluster {} meta component {} error", cluster.getClusterName(), metadataComponent.getComponentTypeCode(), e);
                }
            }
        }
        LOGGER.error("compatible cluster [{}] finish", clusters.size());
    }
}
