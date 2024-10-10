package com.dtstack.engine.master.listener;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 9:56 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface JobGraphListener {

    /**
     * 生成实例成功事件
     * @param triggerDay 生成日期
     */
    void successEvent(String triggerDay);

}
