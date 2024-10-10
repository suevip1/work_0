package com.dtstack.engine.master.thread.metric;

import java.util.concurrent.atomic.LongAdder;


public class SubmitResultBucket {

    private final LongAdder[] counters;

    public SubmitResultBucket() {
        SubmitEvent[] events = SubmitEvent.values();
        this.counters = new LongAdder[events.length];
        for (SubmitEvent event : events) {
            counters[event.ordinal()] = new LongAdder();
        }
    }

    public SubmitResultBucket reset(SubmitResultBucket bucket) {
        for (SubmitEvent event : SubmitEvent.values()) {
            counters[event.ordinal()].reset();
            counters[event.ordinal()].add(bucket.get(event));
        }
        return this;
    }

    public SubmitResultBucket reset() {
        for (SubmitEvent event : SubmitEvent.values()) {
            counters[event.ordinal()].reset();
        }
        return this;
    }

    public long get(SubmitEvent event) {
        return counters[event.ordinal()].sum();
    }

    public SubmitResultBucket add(SubmitEvent event, long n) {
        counters[event.ordinal()].add(n);
        return this;
    }


}
