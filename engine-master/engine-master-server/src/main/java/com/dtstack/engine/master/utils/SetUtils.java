package com.dtstack.engine.master.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author leon
 * @date 2023-03-19 10:58
 **/
public class SetUtils {

    /**
     * 判断 set 之间是否有交集
     * @param sets
     * @return
     */
    public static boolean hasIntersection(Set<?>... sets) {
        if (sets.length == 0) {
            return false;
        }
        Set<Object> mergedSet = new HashSet<>();
        int totalSize = 0;
        for (Set<?> set : sets) {
            mergedSet.addAll(set);
            totalSize += set.size();
        }
        return mergedSet.size() < totalSize;
    }
}
