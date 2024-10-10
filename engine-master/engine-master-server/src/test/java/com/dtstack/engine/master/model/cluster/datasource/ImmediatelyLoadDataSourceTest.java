package com.dtstack.engine.master.model.cluster.datasource;

import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.model.cluster.ComponentFacade;
import com.google.common.collect.Lists;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-24 09:30
 */
public class ImmediatelyLoadDataSourceTest {
    ImmediatelyLoadDataSource immediatelyLoadDataSource = null;

    @Before
    public void before() {
        ProxyFactory proxyFactory = new ProxyFactory();
        ComponentFacade componentFacade = new ComponentFacade();
        proxyFactory.setTarget(componentFacade);
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                Method method = invocation.getMethod();
                Class<?> returnType = method.getReturnType();
                if (returnType == List.class) {
                    return Collections.emptyList();
                }
                return null;
            }
        });
        proxyFactory.addAdvisor(defaultPointcutAdvisor);
        componentFacade = (ComponentFacade) proxyFactory.getProxy();

        immediatelyLoadDataSource = new ImmediatelyLoadDataSource(-1L, componentFacade);
    }

    @Test
    public void listAllByClusterId() {
        immediatelyLoadDataSource.listAllByClusterId();
    }

    @Test
    public void listComponentConfig() {
        immediatelyLoadDataSource.listComponentConfig(Lists.newArrayList(1L), true);
    }

    @Test
    public void getComponents() {
        immediatelyLoadDataSource.getComponents(EComponentType.SPARK, "2.4");
    }

    @Test
    public void testGetComponents() {
        int limit = 1;
        immediatelyLoadDataSource.getComponents(EComponentType.SPARK, limit);
    }

    @Test
    public void testGetComponents1() {
        immediatelyLoadDataSource.getComponents(EComponentType.HIVE_SERVER);
    }

    @Test
    public void testGetComponents2() {
        immediatelyLoadDataSource.getComponents(Lists.newArrayList(EComponentType.HIVE_SERVER));
    }
}