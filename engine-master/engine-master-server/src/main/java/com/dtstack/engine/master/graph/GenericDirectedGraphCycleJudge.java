package com.dtstack.engine.master.graph;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 有向图成环判断器
 * base on : https://dtstack.yuque.com/rd-center/sm6war/qoepli
 *
 * @author leon
 * @date 2022-08-01 19:23
 **/
public class GenericDirectedGraphCycleJudge<V, I, S extends DirectGraphSide<V, I>> implements DirectedGraphCycleJudge<V, I, S> {

    private final Set<S> history = new HashSet<>();

    private final List<S> sidesForJudge;

    public GenericDirectedGraphCycleJudge(List<S> sidesForJudge) {
        this.sidesForJudge = sidesForJudge.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<S> getSidesForJudge() {
        return this.sidesForJudge;
    }

    @Override
    public boolean isCycle(Function<List<V>, List<S>> parentSideProvider, Function<List<V>, List<S>> childSideProvider) {
        List<S> sidesForJudge = getSidesForJudge();

        if (Objects.isNull(sidesForJudge) || CollectionUtils.isEmpty(sidesForJudge)) {
            return false;
        }
        if (Objects.isNull(parentSideProvider) || Objects.isNull(childSideProvider)) {
            return false;
        }

        for (S side : sidesForJudge) {
            if (judgeSingleSide(side, parentSideProvider, childSideProvider)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<S> accessHistory() {
        return history;
    }

    private boolean judgeSingleSide(S side, Function<List<V>, List<S>> parentSideProvider, Function<List<V>, List<S>> childSideProvider) {
        return traversal(side, parentSideProvider, TraversalType.PARENT) || traversal(side, childSideProvider, TraversalType.CHILD);
    }

    private boolean traversal(S side, Function<List<V>, List<S>> sideProvider, TraversalType traversalTye) {
        Set<S> sideSet = new HashSet<>();

        List<S> nextLeveLSides = deduplicationExtraSide(side, sideProvider.apply(Lists.newArrayList(side.val())), traversalTye);

        history.add(side);
        history.addAll(nextLeveLSides);

        while (CollectionUtils.isNotEmpty(nextLeveLSides)) {
            if (doTraversal(sideSet, nextLeveLSides)) {
                return true;
            }
            Set<V> nextProviderRequireKey = getNextVisitKey(nextLeveLSides, traversalTye);
            nextLeveLSides = deduplicationExtraSide(nextProviderRequireKey, sideProvider.apply(Lists.newArrayList(nextProviderRequireKey)), traversalTye);
            history.addAll(nextLeveLSides);
        }
        return false;
    }

    private Set<V> getNextVisitKey(List<S> nextLeveLSides, TraversalType traversalTye) {
        Set<V> nextProviderRequireKey = new HashSet<>();
        List<V> alreadyVisit = new ArrayList<>();

        if (TraversalType.PARENT.equals(traversalTye)) {
            nextLeveLSides.forEach(ns -> nextProviderRequireKey.add(ns.parent()));
            nextLeveLSides.forEach(s -> alreadyVisit.add(s.val()));
        }

        if (TraversalType.CHILD.equals(traversalTye)) {
            nextLeveLSides.forEach(ns -> nextProviderRequireKey.add(ns.val()));
            nextLeveLSides.forEach(s -> alreadyVisit.add(s.parent()));
        }

        return nextProviderRequireKey.stream().filter(key -> !alreadyVisit.contains(key)).collect(Collectors.toSet());
    }

    private boolean doTraversal(Set<S> sideSet, List<S> parentSides) {
        for (S side : parentSides) {
            if (!sideSet.add(side)) {
                return true;
            }
        }
        return false;
    }

    private List<S> deduplicationExtraSide(S side, List<S> providerSide, TraversalType traversalTye) {
        Set<V> visitKey = new HashSet<>();
        if (Objects.nonNull(side.val())) {
            visitKey.add(side.val());
        }
        return deduplicationExtraSide(visitKey, providerSide, traversalTye);
    }


    private List<S> deduplicationExtraSide(Set<V> visitKey, List<S> providerSide, TraversalType traversalTye) {
        List<S> sidesForJudge = getSidesForJudge();

        if (CollectionUtils.isEmpty(visitKey)) {
            visitKey = new HashSet<>();
        }

        if (Objects.isNull(providerSide)) {
            providerSide = new ArrayList<>();
        }

        List<S> finalProviderSide = providerSide;

        // 去重
        List<S> deduplication = sidesForJudge.stream()
                .filter(s -> !finalProviderSide.contains(s)).collect(Collectors.toList());

        // 提取
        List<S> collect = new ArrayList<>();

        Set<V> finalVisitKey = visitKey;
        if (TraversalType.CHILD.equals(traversalTye)) {
            collect = deduplication.stream().filter(s -> finalVisitKey.contains(s.parent())).collect(Collectors.toList());
        }
        if (TraversalType.PARENT.equals(traversalTye)) {
            collect = deduplication.stream().filter(s -> finalVisitKey.contains(s.val())).collect(Collectors.toList());
        }

        providerSide.addAll(collect);
        return providerSide.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
