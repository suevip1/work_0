package com.dtstack.engine.master.resource;

import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.util.StringUtils;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * pushGateway info
 *
 * @author ：wangchuan
 * date：Created in 17:22 2023/1/28
 * company: www.dtstack.com
 */
public class PushGatewayInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushGatewayInfo.class);

    /**
     * 集群名称
     */
    private final String clusterName;

    /**
     * prometheus pushGateway 地址, 可能发生改变
     */
    private String pmAddress;

    /**
     * pushGateway 客户端, 如果 pmAddress 发生变更则需要重新构建 pushGateway
     */
    private PushGateway pushGateway;

    /**
     * 指标注册器
     */
    private final CollectorRegistry collectorRegistry;

    /**
     * cpu 指标
     */
    private final Gauge coreGauge;

    /**
     * 内存指标
     */
    private final Gauge memoryGauge;

    /**
     * 作业进度百分比
     */
    private final Gauge progressGauge;

    private static final String[] LABELS = {"host", "jobId"};

    public PushGatewayInfo(String clusterName, String pmAddress) {
        this.clusterName = clusterName;
        this.pmAddress = pmAddress;
        this.collectorRegistry = new CollectorRegistry();
        this.pushGateway = new PushGateway(this.pmAddress);
        this.coreGauge = Gauge
                .build(GaugeType.JOB_RESOURCE_CORE.getGaugeName(), GaugeType.JOB_RESOURCE_CORE.getGaugeName())
                .labelNames(LABELS)
                .create();
        this.memoryGauge = Gauge
                .build(GaugeType.JOB_RESOURCE_MEMORY.getGaugeName(), GaugeType.JOB_RESOURCE_MEMORY.getGaugeName())
                .labelNames(LABELS)
                .create();
        this.progressGauge = Gauge
                .build(GaugeType.JOB_PROGRESS_REAL_PERCENTAGE.getGaugeName(), GaugeType.JOB_PROGRESS_REAL_PERCENTAGE.getGaugeName())
                .labelNames(LABELS)
                .create();
        collectorRegistry.register(coreGauge);
        collectorRegistry.register(memoryGauge);
        collectorRegistry.register(progressGauge);
    }

    public void refresh(String pmAddress) {
        if (!StringUtils.equals(pmAddress, this.pmAddress)) {
            // 地址发生变更
            LOGGER.info("cluster: {}, prometheus pushGateway address: {}", this.clusterName, pmAddress);
            this.pmAddress = pmAddress;
            this.pushGateway = new PushGateway(this.pmAddress);
        }
    }

    public PushGateway getPushGateway() {
        return pushGateway;
    }

    public CollectorRegistry getCollectorRegistry() {
        return collectorRegistry;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getPmAddress() {
        return pmAddress;
    }

    public Gauge getCoreGauge() {
        return coreGauge;
    }

    public Gauge getMemoryGauge() {
        return memoryGauge;
    }

    public Gauge getProgressGauge() {
        return progressGauge;
    }
}
