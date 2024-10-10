package com.dtstack.engine.thread;

import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.thread.ElasticThreadPoolExecutor;
import com.dtstack.engine.master.thread.metric.SubmitEvent;
import com.dtstack.engine.master.thread.metric.SubmitLeapArray;
import com.dtstack.engine.master.thread.metric.SubmitResultBucket;
import com.dtstack.engine.master.thread.metric.WindowWrap;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

/**
 * @author yuebai
 * @date 2023/5/5
 */
public class SubmitLeapArrayTest {

    @Test
    public void testWindow() throws Exception {
        SubmitLeapArray submitLeapArray = new SubmitLeapArray(3, 3500);

        for (int i = 0; i < 10; i++) {
            WindowWrap<SubmitResultBucket> submitResultBucketWindowWrap = submitLeapArray.currentWindow(System.currentTimeMillis());
            submitResultBucketWindowWrap.value().add(i % 2 == 0 ? SubmitEvent.PASS : SubmitEvent.EXCEPTION, 1);
            Thread.sleep(1000);
        }

        long passTotal = submitLeapArray
                .listAll()
                .stream()
                .map(a -> a.value().get(SubmitEvent.PASS))
                .flatMapToLong(LongStream::of).sum();
        Assert.assertTrue(passTotal <= 3);
        Assert.assertTrue(passTotal >= 1);
    }

    @Test
    public void testSubmit() throws Exception {
        ThreadPoolExecutor jobSubmitConcurrent = new ThreadPoolExecutor(3, 3, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("_JobSubmitConcurrent"), new BlockCallerPolicy());
        SubmitLeapArray submitLeapArray = new SubmitLeapArray(3, 3000);

        for (int i = 0; i < 30; i++) {
            int finalI = i;
            CompletableFuture.runAsync(() -> {
                try {
                    WindowWrap<SubmitResultBucket> submitResultBucketWindowWrap = submitLeapArray.currentWindow(System.currentTimeMillis());
                    submitResultBucketWindowWrap.value().add(finalI % 2 == 0 ? SubmitEvent.PASS : SubmitEvent.EXCEPTION, 1);
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }, jobSubmitConcurrent).join();
        }
        long passTotal = submitLeapArray
                .listAll()
                .stream()
                .map(a -> a.value().get(SubmitEvent.PASS))
                .flatMapToLong(LongStream::of).sum();
        Assert.assertTrue(passTotal > 0);

    }


    @Test
    public void testElasticSubmit() throws Exception {
        ThreadPoolExecutor jobSubmitConcurrent = new ThreadPoolExecutor(3, 3, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("_JobSubmitConcurrent"), new BlockCallerPolicy());
        ElasticThreadPoolExecutor elasticSubmitThreadPoolExecutor = new ElasticThreadPoolExecutor(
                jobSubmitConcurrent, 10, 10, 0.8F, 0.6F);
        for (int i = 0; i < 200; i++) {
            int finalI = i;
            CompletableFuture.runAsync(() -> {
                try {
                    SubmitEvent submitEvent;
                    if (finalI < 100) {
                        submitEvent = SubmitEvent.PASS;
                    } else {
                        submitEvent = finalI % 2 == 1 ? SubmitEvent.EXCEPTION : SubmitEvent.PASS;
                    }
                    System.out.println(Thread.currentThread().getName() + "----------" + finalI + " === " + submitEvent.name());
                    elasticSubmitThreadPoolExecutor.collect(Thread.currentThread().getId(), submitEvent);
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, elasticSubmitThreadPoolExecutor);
        }
        elasticSubmitThreadPoolExecutor.print();
    }
}
