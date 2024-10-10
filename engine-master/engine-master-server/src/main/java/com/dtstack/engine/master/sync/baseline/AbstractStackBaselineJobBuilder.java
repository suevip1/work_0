package com.dtstack.engine.master.sync.baseline;

import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.po.BaselineJobJob;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 4:39 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractStackBaselineJobBuilder extends ArrayList<Set<String>> implements BaselineJobBuilder {

    private Set<String> keySet = Sets.newHashSet();

    public boolean isEmpty() {
        return this.size() == 0;
    }

    //压栈
    public Boolean push(Set<String> jobKeys) {
        // 防止成环
        String key = Joiner.on("").join(jobKeys);
        String md5String = MD5Util.getMd5String(key);

        if (!keySet.contains(md5String)) {
            keySet.add(md5String);
            return this.add(new HashSet<>(jobKeys));
        } else {
            return Boolean.FALSE;
        }

    }

    //出栈
    public Set<String> pop() {
        if (this.size() == 0) {
            return Sets.newHashSet();
        } else {
            return this.remove(this.size()-1);
        }
    }
}
