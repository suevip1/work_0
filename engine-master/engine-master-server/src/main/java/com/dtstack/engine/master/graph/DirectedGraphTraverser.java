package com.dtstack.engine.master.graph;

import java.util.List;
import java.util.function.Function;

/**
 * @author leon
 * @date 2022-08-04 10:30
 **/
public interface DirectedGraphTraverser<I> {

    List<I> travel(I id, Function<I, List<I>> nextLevelIdProvide);
}
