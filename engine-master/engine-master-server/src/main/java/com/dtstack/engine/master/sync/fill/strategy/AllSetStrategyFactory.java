package com.dtstack.engine.master.sync.fill.strategy;

import org.springframework.context.ApplicationContext;

/**
 * @Auther: dazhi
 * @Date: 2022/7/7 10:46 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AllSetStrategyFactory {

    public static AllSetStrategy getAllSetStrategy(String name, ApplicationContext applicationContext) {
        if (SetStrategy.recursion.name().toLowerCase().equals(name)) {
            return RecursionAllSetStrategy.getInstance(applicationContext);
        } else {
            return StackAllSetStrategy.getInstance(applicationContext);
        }
    }


    enum SetStrategy {
        recursion, stack
    }
}
