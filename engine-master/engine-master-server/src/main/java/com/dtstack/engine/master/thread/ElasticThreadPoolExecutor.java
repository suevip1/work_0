package com.dtstack.engine.master.thread;

import com.dtstack.engine.master.thread.metric.SubmitEvent;
import com.dtstack.engine.master.thread.metric.SubmitLeapArray;
import com.dtstack.engine.master.thread.metric.SubmitResultBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.LongStream;

/**
 * @author yuebai
 * @date 2023/4/20
 */
public class ElasticThreadPoolExecutor extends DelegateThreadPoolExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticThreadPoolExecutor.class);

    private Integer maxPoolSize;
    private float incrementFactor;
    private float decrementFactor;
    private LongSummaryStatistics maxSubmitTime = new LongSummaryStatistics();
    private Integer windowSize;
    private ConcurrentHashMap<Long, Long> threadSubmitRecord = new ConcurrentHashMap<>();
    private SubmitLeapArray submitLeapArray = null;
    private Integer originalMaxPoolSize = null;
    private int timeoutMs = 1000 * 60 * 5;

    public ElasticThreadPoolExecutor(ThreadPoolExecutor executor, Integer maxPoolSize, Integer windowSize,
                                     float incrementFactor, float decrementFactor) {
        super(executor);
        this.maxPoolSize = maxPoolSize;
        this.windowSize = windowSize;
        this.incrementFactor = incrementFactor;
        this.decrementFactor = decrementFactor;
        this.originalMaxPoolSize = executor.getMaximumPoolSize();
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);
    }

    public void collect(Long thread, SubmitEvent event) {
        if (submitLeapArray != null) {
            //collect
            SubmitResultBucket value = submitLeapArray.currentWindow().value();
            value.add(event, 1);
            incrementThreadPool();
        } else if (maxSubmitTime.getCount() < runExecutorService.getCorePoolSize() * 2L) {
            //compute interval ms
            threadSubmitRecord.compute(thread, (key, oldVal) -> {
                if (oldVal != null && System.currentTimeMillis() - oldVal <= timeoutMs) {
                    //filter exceed time record
                    maxSubmitTime.accept(System.currentTimeMillis() - oldVal);
                }
                return System.currentTimeMillis();
            });
        } else {
            //init
            synchronized (ElasticThreadPoolExecutor.class) {
                if (submitLeapArray != null) {
                    return;
                }
                int avgTime = (int) maxSubmitTime.getAverage();
                LOGGER.info("leapWindow avgTime:{}", avgTime);
                // time out params
                if (avgTime >= timeoutMs || avgTime <= 0) {
                    avgTime = timeoutMs;
                }
                submitLeapArray = new SubmitLeapArray(maxPoolSize * windowSize, avgTime * maxPoolSize * windowSize);
                LOGGER.info("leapWindow avgTime:{} " +
                        "intervalInMs: {} " +
                        "sampleCount: {} " +
                        " ===================", avgTime, submitLeapArray.getIntervalInMs(), submitLeapArray.getSampleCount());
                threadSubmitRecord.clear();
            }
        }
    }

    public void incrementThreadPool() {
        if (submitLeapArray.list().size() >= windowSize) {
            synchronized (ElasticThreadPoolExecutor.class) {
                long passSum = submitLeapArray.listAll().stream().map(a -> a.value().get(SubmitEvent.PASS))
                        .flatMapToLong(LongStream::of).sum();
                long exceptionSum = submitLeapArray.listAll().stream().map(a -> a.value().get(SubmitEvent.EXCEPTION))
                        .flatMapToLong(LongStream::of).sum();
                if (passSum == 0) {
                    return;
                }
                if (passSum + exceptionSum < windowSize) {
                    return;
                }
                BigDecimal submitFactorVal = new BigDecimal(passSum).divide(new BigDecimal(passSum + exceptionSum), 1, RoundingMode.DOWN);
                float submitFactor = submitFactorVal.floatValue();
                if (submitFactor >= incrementFactor && runExecutorService.getMaximumPoolSize() < maxPoolSize) {
                    runExecutorService.setMaximumPoolSize(Math.min(runExecutorService.getMaximumPoolSize() + 1, maxPoolSize));
                    LOGGER.info("========increment thread pool===========: ThreadPool:{} " +
                            "configMaxPoolSize: {} " +
                            "incrementFactor: {} " +
                            "canIncrementPoolSize: {} " +
                            "submitFactor: {} ", runExecutorService, maxPoolSize, incrementFactor, runExecutorService.getMaximumPoolSize(), submitFactor);
                } else if (submitFactor <= decrementFactor && runExecutorService.getMaximumPoolSize() > originalMaxPoolSize) {
                    runExecutorService.setMaximumPoolSize(Math.max(runExecutorService.getMaximumPoolSize() - 1, originalMaxPoolSize));
                    LOGGER.info("========decrement thread pool===========: ThreadPool:{} " +
                            "configMaxPoolSize: {} " +
                            "decrementFactor: {} " +
                            "canDecrementPoolSize: {} " +
                            "submitFactor: {} ", runExecutorService, maxPoolSize, decrementFactor, runExecutorService.getMaximumPoolSize(), submitFactor);
                }
                submitLeapArray.listAll().forEach(w -> w.value().reset());
            }

        }
    }

    public void print() {
        LOGGER.info("ThreadPool:{} " +
                "configMaxPoolSize: {} " +
                "incrementFactor: {} " +
                "canIncrementPoolSize: {} " +
                " ===================", runExecutorService, maxPoolSize, incrementFactor, runExecutorService.getMaximumPoolSize());
    }
}
