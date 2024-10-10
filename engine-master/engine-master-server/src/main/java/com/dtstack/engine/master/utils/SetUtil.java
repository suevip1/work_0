package com.dtstack.engine.master.utils;

import java.util.Iterator;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2023/2/2 3:24 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class SetUtil {

    public static boolean isSetEqual(Set<String> set1, Set<String> set2) {

        if (set1 == null && set2 == null) {
            return true; // Both are null
        }

        if (set1 == null || set2 == null || set1.size() != set2.size() || set1.size() == 0) {
            return false;
        }

        Iterator<String> ite2 = set2.iterator();

        boolean isFullEqual = true;

        while (ite2.hasNext()) {
            if (!set1.contains(ite2.next())) {
                isFullEqual = false;
            }
        }

        return isFullEqual;
    }
}
