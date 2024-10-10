package com.dtstack.engine.master;

import com.dtstack.dtcenter.common.util.SystemPropertyUtil;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


public class DAGSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    /**
     * @param clazz 加载类
     * @throws InitializationError 单元测试初始化异常
     */
    public DAGSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        SystemPropertyUtil.setSystemUserDir();
    }
}
