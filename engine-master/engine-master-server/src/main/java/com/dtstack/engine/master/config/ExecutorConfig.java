package com.dtstack.engine.master.config;

import com.dtstack.engine.master.utils.ExecutorUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-09-19 16:16
 */
@Configuration
@EnableAsync
public class ExecutorConfig {
    private static int CPU_PROCESSOR = Runtime.getRuntime().availableProcessors();

    /******************* commonExecutor start *************************/
    @Bean
    public ThreadPoolTaskExecutor commonExecutor(ExecutorProperties commonExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(commonExecutorProp);
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.common")
    public ExecutorProperties commonExecutorProp() {
        // 设置默认值，可配置
        return ExecutorProperties.builder()
                .corePoolSize((int) (CPU_PROCESSOR / (1 - 0.2)))
                .maxPoolSize((int) (CPU_PROCESSOR / (1 - 0.5)))
                .queueCapacity(500)
                .threadName("commonExecutor")
                .keepAliveSeconds(3600)
                .rejectedExecutionHandler(ExecutorProperties.ABORT_POLICY)
                .build();
    }
    /******************* commonExecutor end *************************/

    /******************* securityAuditExecutor start *************************/
    @Bean
    public ThreadPoolTaskExecutor securityAuditExecutor(ExecutorProperties securityAuditExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(securityAuditExecutorProp);
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.security.audit")
    public ExecutorProperties securityAuditExecutorProp() {
        // 设置默认值，可配置
        return ExecutorProperties.builder()
                .corePoolSize(5)
                .maxPoolSize(5)
                .queueCapacity(1000)
                .threadName("securityAuditExecutor")
                .rejectedExecutionHandler(ExecutorProperties.BLOCK_CALLER_POLICY)
                .build();
    }
    /******************* securityAuditExecutor end *************************/

    /******************* projectMinuteStatisticsExecutor start *************************/
    @Bean
    public ThreadPoolTaskExecutor projectMinuteStatisticsExecutor(ExecutorProperties projectMinuteStatisticsExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(projectMinuteStatisticsExecutorProp);
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.project.minute.statistics")
    public ExecutorProperties projectMinuteStatisticsExecutorProp() {
        // 设置默认值，可配置
        return ExecutorProperties.builder()
                .corePoolSize(5)
                .maxPoolSize(5)
                .queueCapacity(500)
                .threadName("projectMinuteStatistics")
                .rejectedExecutionHandler(ExecutorProperties.ABORT_POLICY)
                .build();
    }
    /******************* projectMinuteStatisticsExecutor end *************************/

    /******************* jobLogExecutor start *************************/
    @Bean
    public ThreadPoolTaskExecutor jobLogExecutor(ExecutorProperties jobLogExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(jobLogExecutorProp);
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.job.kill.log")
    public ExecutorProperties jobLogExecutorProp() {
        // 设置默认值，可配置
        return ExecutorProperties.builder()
                .corePoolSize(4)
                .maxPoolSize(4)
                .queueCapacity(2000)
                .threadName("jobKillLog")
                .build();
    }
    /******************* jobLogExecutor end *************************/

    /******************* cronExecutor start *************************/
    @Bean
    public ThreadPoolTaskExecutor cronExecutor(ExecutorProperties cronExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(cronExecutorProp);
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.cron")
    public ExecutorProperties cronExecutorProp() {
        // 设置默认值
        return ExecutorProperties.builder()
                .corePoolSize(5)
                .maxPoolSize(10)
                .queueCapacity(100)
                .threadName("cronExecutor")
                .rejectedExecutionHandler(ExecutorProperties.CALLER_RUNS_POLICY)
                .build();
    }
    /******************* cronExecutor end *************************/


    /******************* workflow temporary running Executor start *************************/

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.workflow.temporary.acceptor")
    public ExecutorProperties workflowTemporaryAcceptorExecutorProp() {
        return ExecutorProperties.builder()
                .corePoolSize(5)
                .maxPoolSize(10)
                .queueCapacity(100)
                .threadName("workflowTemporaryScheduleExecutor")
                .rejectedExecutionHandler(ExecutorProperties.CALLER_RUNS_POLICY)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.workflow.temporary")
    public ExecutorProperties workflowTemporaryExecutorProp() {
        return ExecutorProperties.builder()
                .corePoolSize(20)
                .maxPoolSize(30)
                .queueCapacity(100)
                .threadName("workflowTemporaryExecutor")
                .rejectedExecutionHandler(ExecutorProperties.CALLER_RUNS_POLICY)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor workflowTemporaryAcceptorExecutor(ExecutorProperties workflowTemporaryAcceptorExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(workflowTemporaryAcceptorExecutorProp);
    }

    @Bean
    public ThreadPoolTaskExecutor workflowTemporaryExecutor(ExecutorProperties workflowTemporaryExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(workflowTemporaryExecutorProp);
    }

    /******************* workflow temporary running Executor end *************************/

    /******************* mqConsumeExecutor start *************************/
    @Bean
    public ThreadPoolTaskExecutor mqConsumeExecutor(ExecutorProperties mqConsumeExecutorProp) {
        return ExecutorUtil.newThreadPoolTaskExecutor(mqConsumeExecutorProp);
    }

    @Bean
    @ConfigurationProperties(prefix = "engine.executor.mq.consume")
    public ExecutorProperties mqConsumeExecutorProp() {
        // 设置默认值，可配置
        return ExecutorProperties.builder()
                .corePoolSize(2)
                .maxPoolSize(4)
                .queueCapacity(500)
                .threadName("mqConsumeExecutor")
                .keepAliveSeconds(3600)
                .rejectedExecutionHandler(ExecutorProperties.CALLER_RUNS_POLICY)
                .build();
    }
    /******************* commonExecutor end *************************/

}