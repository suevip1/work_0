package com.dtstack.engine.master.mockcontainer;

import com.alibaba.testable.core.annotation.MockInvoke;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.InvocationTargetException;

/**
 * @author leon
 * @date 2022-06-23 17:37
 **/
public class InvocationMock {

    @MockInvoke(targetClass = Invocation.class)
    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return new Object();
    }

    @MockInvoke(targetClass = BoundSql.class)
    public String getSql() {
        return "";
    }
}
