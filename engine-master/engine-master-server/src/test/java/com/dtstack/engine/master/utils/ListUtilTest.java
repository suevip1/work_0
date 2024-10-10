package com.dtstack.engine.master.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2024-01-11 17:25
 */
public class ListUtilTest {
    @Test
    public void testIsSubList() {
        List<Long> taskTagIds = Lists.newArrayList(5L);
        List<Long> tagIds = Lists.newArrayList(5L);
        System.out.println(ListUtil.isSubList(taskTagIds, tagIds));
    }

    @Test
    public void testRetains() {
        Set<Long> selectTaskIds = Sets.newHashSet(1L, 2L, 3L);
        List<Long> taskIds = Lists.newArrayList(3L);
        // 保留 selectTaskIds 中也存在于 taskIds 的记录
        selectTaskIds.retainAll(taskIds);
        // [3]
        System.out.println(selectTaskIds);
    }
}
