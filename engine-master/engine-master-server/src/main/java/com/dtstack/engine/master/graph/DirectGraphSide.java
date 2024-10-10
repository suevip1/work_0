package com.dtstack.engine.master.graph;

/**
 * 有向的图的一条边
 *
 * @author leon
 * @date 2022-08-01 19:56
 **/
public interface DirectGraphSide<V, I> extends GraphNode<V>, IdProvider<I> {

    V parent();
}
