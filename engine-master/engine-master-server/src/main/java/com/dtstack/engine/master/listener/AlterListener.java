package com.dtstack.engine.master.listener;

/**
 * @Auther: dazhi
 * @Date: 2022/5/20 2:38 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlterListener {

    /**
     * 告警时间触发
     * alterEventContext 触发的类型
     */
    void event(AlterEventContext alterEventContext);

}
