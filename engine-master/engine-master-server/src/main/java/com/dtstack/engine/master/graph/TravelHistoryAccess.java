package com.dtstack.engine.master.graph;

import java.util.Set;

/**
 * @author leon
 * @date 2022-08-12 10:53
 **/
public interface TravelHistoryAccess<V, I, S extends DirectGraphSide<V, I>> {

    Set<S> accessHistory();

}
