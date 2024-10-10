package com.dtstack.engine.master.sync.fill.strategy;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/7/7 10:21 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AllSetStrategy {

    /**
     * 填充集合
     *
     * @param run 需要跑的节点
     * @return all 集合
     */
    Set<String> getAllList(Set<String> run);


}
