package com.dtstack.engine.master.graph;

/**
 * @author leon
 * @date 2022-08-01 19:56
 **/
public abstract class AbstractDirectGraphSide<V, I> implements DirectGraphSide<V, I> {

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
