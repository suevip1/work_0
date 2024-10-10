package com.dtstack.engine.common.callback;

/**
 * @Auther: dazhi
 * @Date: 2023/2/15 11:04 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface Callback <P,R>{
    // 回调函数 返回Result 入参P
    R callback(P param);
}
