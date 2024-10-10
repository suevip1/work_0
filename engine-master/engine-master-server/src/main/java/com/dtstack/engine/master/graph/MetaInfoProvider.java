package com.dtstack.engine.master.graph;

/**
 * @author leon
 * @date 2022-08-03 15:34
 **/
public interface MetaInfoProvider<A,I,N> extends IdProvider<I> {

    A appType();

    N name();
}
