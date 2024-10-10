package com.dtstack.engine.master.graph;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author leon
 * @date 2022-08-04 10:32
 **/
public class GenericDirectedGraphTraverser<I> implements DirectedGraphTraverser<I> {

    @Override
    public List<I> travel(I id, Function<I, List<I>> nextLevelIdProvide) {
        List<I> result = new ArrayList<>();
        List<I> nextLevelId = nextLevelIdProvide.apply(id);
        while (CollectionUtils.isNotEmpty(nextLevelId)) {
            result.addAll(nextLevelId);
            List<I> level = new ArrayList<>();
            for (I nextId : nextLevelId) {
                List<I> apply = nextLevelIdProvide.apply(nextId);
                level.addAll(apply);
            }
            nextLevelId = level;
        }
        // 去重
        return result.stream().distinct().collect(Collectors.toList());
    }
}
