package com.dtstack.engine.master.utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/3/16 8:40 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ListUtil {


    public static <E> boolean isSubList(List<E> parent, List<E> children) {
        List<E> parent_1 = Lists.newArrayList();
        parent_1.addAll(parent);
        List<E> children_1 = Lists.newArrayList();
        children_1.addAll(children);
        int differ = parent_1.size() - children_1.size();
        parent_1.removeAll(children_1);
        return differ >= 0 && differ == parent_1.size();
    }
}
