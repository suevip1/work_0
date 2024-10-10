package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.enums.CrudEnum;
import com.dtstack.engine.master.mockcontainer.impl.ComponentServiceMock;
import com.dtstack.engine.master.mockcontainer.impl.DataSourceServiceMock;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-26 20:21
 */
@MockWith(value = DataSourceServiceMock.class)
@EnablePrivateAccess(srcClass = DataSourceService.class)
public class DataSourceServiceTest {
    DataSourceService dataSourceService = new DataSourceService();

    @Before
    public void before() {
        PrivateAccessor.set(dataSourceService, "dataSourceAPIClient",
                Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{DataSourceAPIClient.class}, (loader, interfaces, h) -> null));
    }


    @Test
    public void loadJdbcInfo() {
        JobClient jobClient = new JobClient();
        jobClient.setEngineType("hive");
        String pluginInfo = "{\"dtDataSourceId\":9}";
        dataSourceService.loadJdbcInfoAndUpdateCache(jobClient, pluginInfo);
    }

    @Test
    public void bindHistoryLdapIfNeeded() {
        dataSourceService.bindHistoryLdapIfNeeded(EComponentType.LDAP.getTypeCode(), -1L, CrudEnum.ADD);
    }

    @Test
    public void bindLdapIfNeeded() {
        dataSourceService.bindLdapIfNeeded(-1L, Sets.newHashSet(-2L));
    }

    @Test
    public void syncToRangerIfNeeded() {
        Component component = ComponentServiceMock.mockDefaultComponents().stream()
                .filter(c -> c.getComponentTypeCode().equals(EComponentType.HIVE_SERVER.getTypeCode()))
                .findFirst().orElse(null);
        dataSourceService.syncToRangerIfNeeded(-1L, component,
                Sets.newHashSet(-1L), CrudEnum.ADD);
    }

    @Test
    public void syncRelationToRangerIfNeeded() {
        Component component = ComponentServiceMock.mockDefaultComponents().stream()
                .filter(c -> c.getComponentTypeCode().equals(EComponentType.HIVE_SERVER.getTypeCode()))
                .findFirst().orElse(null);
        dataSourceService.syncRelationToRangerIfNeeded(-1L, component, -2L);
    }


    @Test
    public void validateServiceName() {
        DataSourceService.validateServiceName("abc", "HiveServer");
    }
}