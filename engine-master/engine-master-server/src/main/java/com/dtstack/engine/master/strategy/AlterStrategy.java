package com.dtstack.engine.master.strategy;

import com.dtstack.engine.master.listener.AlterEventContext;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 2:36 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlterStrategy {

    void alert(AlterEventContext context);
}
