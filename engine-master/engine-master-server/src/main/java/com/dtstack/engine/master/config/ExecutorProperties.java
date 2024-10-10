package com.dtstack.engine.master.config;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-10 19:47
 */
public class ExecutorProperties {
    public static final String ABORT_POLICY = "abortPolicy";
    public static final String CALLER_RUNS_POLICY = "callerRunsPolicy";
    public static final String DISCARD_OLDEST_POLICY = "discardOldestPolicy";
    public static final String DISCARD_POLICY = "discardPolicy";
    public static final String BLOCK_CALLER_POLICY = "blockCallerPolicy";

    public static final String CUSTOM_THREAD_FACTORY = "customThreadFactory";
    /**
     * 线程前缀名
     */
    private String threadName;

    /**
     * 核心线程池大小
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 队列大小
     */
    private int queueCapacity;

    /**
     * 线程池维护空闲线程存在时间
     */
    private int keepAliveSeconds;

    /**
     * 拒绝策略
     */
    private String rejectedExecutionHandler = ABORT_POLICY;

    /**
     * 线程工厂
     */
    private String threadFactory = CUSTOM_THREAD_FACTORY;

    public ExecutorProperties() {
    }

    ExecutorProperties(String threadName, int corePoolSize, int maxPoolSize, int queueCapacity, int keepAliveSeconds, String rejectedExecutionHandler, String threadFactory) {
        this.threadName = threadName;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.keepAliveSeconds = keepAliveSeconds;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        this.threadFactory = threadFactory;
    }

    public static ExecutorPropertiesBuilder builder() {
        return new ExecutorPropertiesBuilder();
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void setRejectedExecutionHandler(String rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public String getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(String threadFactory) {
        this.threadFactory = threadFactory;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return this.keepAliveSeconds;
    }

    public String getRejectedExecutionHandler() {
        return this.rejectedExecutionHandler;
    }

    public static class ExecutorPropertiesBuilder {
        private String threadName;
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private int keepAliveSeconds;
        private String rejectedExecutionHandler;
        private String threadFactory;

        ExecutorPropertiesBuilder() {
        }

        public ExecutorPropertiesBuilder threadFactory(String threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public ExecutorPropertiesBuilder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public ExecutorPropertiesBuilder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public ExecutorPropertiesBuilder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public ExecutorPropertiesBuilder queueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }

        public ExecutorPropertiesBuilder keepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        public ExecutorPropertiesBuilder rejectedExecutionHandler(String rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public ExecutorProperties build() {
            return new ExecutorProperties(threadName, corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds, rejectedExecutionHandler, threadFactory);
        }

        @Override
        public String toString() {
            return "ExecutorConfig.ExecutorProperties.ExecutorPropertiesBuilder(threadName=" + this.threadName + ", corePoolSize=" + this.corePoolSize + ", maxPoolSize=" + this.maxPoolSize + ", queueCapacity=" + this.queueCapacity + ", keepAliveSeconds=" + this.keepAliveSeconds
                    + ", rejectedExecutionHandler=" + this.rejectedExecutionHandler
                    + ", threadFactory=" + this.threadFactory + ")";
        }
    }
}
