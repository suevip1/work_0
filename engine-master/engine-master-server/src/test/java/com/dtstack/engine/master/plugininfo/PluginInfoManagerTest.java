package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.TaskParamsService;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@MockWith(PluginInfoManagerTest.PluginInfoManagerMock.class)
public class PluginInfoManagerTest {

    private static PluginInfoManager manager = new PluginInfoManager();


    static class PluginInfoManagerMock {
        @MockInvoke(targetClass = TaskParamsService.class)
        public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType, String engineType, Long tenantId) {
            return EDeployMode.PERJOB;
        }

        @MockInvoke(targetClass = ScheduleDictService.class)
        public String convertVersionNameToValue(String componentVersion, String engineType) {
            return "112";
        }


        @MockInvoke(targetClass = ClusterService.class)
        public Cluster getCluster(Long dtUicTenantId) {
            String clusterJson = "{\"clusterName\":\"CDP_kerberos\",\"gmtCreate\":1667381762000,\"gmtModified\":1667555635000,\"hadoopVersion\":\"\",\"id\":1075,\"isDeleted\":0}";
            return JSONObject.parseObject(clusterJson, Cluster.class);
        }

        @MockInvoke(targetClass = ComponentService.class)
        public Component getComponentByVersion(Long clusterId, Integer componentType, String componentVersion) {
            return JSONObject.parseObject("[{\"clusterId\":1075,\"componentName\":\"YARN\",\"componentTypeCode\":5,\"gmtCreate\":1667382067000,\"gmtModified\":1667550532000,\"hadoopVersion\":\"3.1.1\",\"id\":5223,\"isDefault\":true,\"isDeleted\":0,\"kerberosFileName\":\"cdp_hive.keytab.zip\",\"sslFileName\":\"cdp_ssl.zip\",\"storeType\":4,\"uploadFileName\":\"cdp_config.zip\",\"versionName\":\"CDP 7.x\"}]",
                    new TypeReference<List<Component>>() {
                    }).get(0);
        }

        @MockInvoke(targetClass = AbstractPluginAdapter.class)
        public JSONObject buildPluginInfo(boolean isConsole) {
            return new JSONObject();
        }


        }

    @Test
    public void setApplicationContext() {
        manager.setApplicationContext(new ApplicationContext() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getApplicationName() {
                return null;
            }

            @Override
            public String getDisplayName() {
                return null;
            }

            @Override
            public long getStartupDate() {
                return 0;
            }

            @Override
            public ApplicationContext getParent() {
                return null;
            }

            @Override
            public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
                return null;
            }

            @Override
            public BeanFactory getParentBeanFactory() {
                return null;
            }

            @Override
            public boolean containsLocalBean(String s) {
                return false;
            }

            @Override
            public boolean containsBeanDefinition(String s) {
                return false;
            }

            @Override
            public int getBeanDefinitionCount() {
                return 0;
            }

            @Override
            public String[] getBeanDefinitionNames() {
                return new String[0];
            }

            @Override
            public <T> ObjectProvider<T> getBeanProvider(Class<T> aClass, boolean b) {
                return null;
            }

            @Override
            public <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType, boolean b) {
                return null;
            }

            @Override
            public String[] getBeanNamesForType(ResolvableType resolvableType) {
                return new String[0];
            }

            @Override
            public String[] getBeanNamesForType(ResolvableType resolvableType, boolean b, boolean b1) {
                return new String[0];
            }

            @Override
            public String[] getBeanNamesForType(Class<?> aClass) {
                return new String[0];
            }

            @Override
            public String[] getBeanNamesForType(Class<?> aClass, boolean b, boolean b1) {
                return new String[0];
            }

            @Override
            public <T> Map<String, T> getBeansOfType(Class<T> aClass) throws BeansException {
                return null;
            }

            @Override
            public <T> Map<String, T> getBeansOfType(Class<T> aClass, boolean b, boolean b1) throws BeansException {
                return null;
            }

            @Override
            public String[] getBeanNamesForAnnotation(Class<? extends Annotation> aClass) {
                return new String[0];
            }

            @Override
            public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> aClass) throws BeansException {
                return null;
            }

            @Override
            public <A extends Annotation> A findAnnotationOnBean(String s, Class<A> aClass) throws NoSuchBeanDefinitionException {
                return null;
            }

            @Override
            public <A extends Annotation> A findAnnotationOnBean(String s, Class<A> aClass, boolean b) throws NoSuchBeanDefinitionException {
                return null;
            }

            @Override
            public Object getBean(String s) throws BeansException {
                return null;
            }

            @Override
            public <T> T getBean(String s, Class<T> aClass) throws BeansException {
                return null;
            }

            @Override
            public Object getBean(String s, Object... objects) throws BeansException {
                return null;
            }

            @Override
            public <T> T getBean(Class<T> aClass) throws BeansException {
                return null;
            }

            @Override
            public <T> T getBean(Class<T> aClass, Object... objects) throws BeansException {
                return null;
            }

            @Override
            public <T> ObjectProvider<T> getBeanProvider(Class<T> aClass) {
                return null;
            }

            @Override
            public <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType) {
                return null;
            }

            @Override
            public boolean containsBean(String s) {
                return false;
            }

            @Override
            public boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
                return false;
            }

            @Override
            public boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
                return false;
            }

            @Override
            public boolean isTypeMatch(String s, ResolvableType resolvableType) throws NoSuchBeanDefinitionException {
                return false;
            }

            @Override
            public boolean isTypeMatch(String s, Class<?> aClass) throws NoSuchBeanDefinitionException {
                return false;
            }

            @Override
            public Class<?> getType(String s) throws NoSuchBeanDefinitionException {
                return null;
            }

            @Override
            public Class<?> getType(String s, boolean b) throws NoSuchBeanDefinitionException {
                return null;
            }

            @Override
            public String[] getAliases(String s) {
                return new String[0];
            }

            @Override
            public void publishEvent(Object event) {

            }

            @Override
            public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
                return null;
            }

            @Override
            public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
                return null;
            }

            @Override
            public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
                return null;
            }

            @Override
            public Environment getEnvironment() {
                return null;
            }

            @Override
            public Resource[] getResources(String locationPattern) throws IOException {
                return new Resource[0];
            }

            @Override
            public Resource getResource(String location) {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }
        });
    }

    @Test
    public void buildTaskPluginInfo() {
        manager.buildTaskPluginInfo(1L,1, EScheduleJobType.SYNC.getType(), "{}","flink",1L,1L,1L,"");
    }

    @Test
    public void testBuildTaskPluginInfo() {
        ParamAction paramAction = new ParamAction();
        paramAction.setEngineType("flink");
        PrivateAccessor.set(manager, "DEFAULT_PLUGIN_INFO_ADAPTER",  new AbstractPluginAdapter() {
            @Override
            public EComponentType getEComponentType() {
                return null;
            }

            @Override
            public JSONObject buildPluginInfo(boolean isConsole) {
                return new JSONObject();
            }
        });
        manager.buildTaskPluginInfo(paramAction);
    }

}