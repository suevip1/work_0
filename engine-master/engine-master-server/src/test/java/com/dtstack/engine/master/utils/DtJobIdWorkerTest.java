package com.dtstack.engine.master.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class DtJobIdWorkerTest {

    @Test
    public void getInstance() {
        DtJobIdWorker.getInstance(1,1L);
    }

    @Test
    public void digits32() {
        DtJobIdWorker.digits32(1L);
    }

    @Test
    public void nextJobId() {
        String s = DtJobIdWorker.getInstance(1,1L).nextJobId();
    }
}