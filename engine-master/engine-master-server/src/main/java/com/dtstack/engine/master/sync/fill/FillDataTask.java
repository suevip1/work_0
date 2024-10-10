package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.sync.fill.strategy.AllSetStrategy;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface FillDataTask extends AllSetStrategy {

    /**
     * 设置补数据类型
     *
     * @param fillDataType
     * @return
     */
    FillDataTypeEnum setFillDataType(Integer fillDataType);

    /**
     * 获取运行集合 R集合
     *
     * @return R集合
     */
    Set<String> getRunList();

}
