package com.dtstack.engine.master.thread.metric;


public class SubmitLeapArray extends LeapArray<SubmitResultBucket> {

    public SubmitLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }

    @Override
    public SubmitResultBucket newEmptyBucket(long time) {
        return new SubmitResultBucket();
    }

    @Override
    protected WindowWrap<SubmitResultBucket> resetWindowTo(WindowWrap<SubmitResultBucket> w, long startTime) {
        // Update the start time and reset value.
        w.resetTo(startTime);
        w.value().reset();
        return w;
    }
}
