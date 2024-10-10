package com.dtstack.engine.master.graph;


import java.util.List;
import java.util.function.Function;

/**
 * 有向图成环判断器
 *
 * @author leon
 * @date 2022-08-01 18:55
 **/
public interface DirectedGraphCycleJudge<V, I, S extends DirectGraphSide<V, I>> extends TravelHistoryAccess<V,I,S> {

    List<S> getSidesForJudge();

    boolean isCycle(Function<List<V>, List<S>> parentSideProvider, Function<List<V>, List<S>> childSideProvider);

}
