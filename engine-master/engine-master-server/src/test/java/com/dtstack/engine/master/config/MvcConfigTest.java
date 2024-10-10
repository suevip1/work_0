package com.dtstack.engine.master.config;

import cn.hutool.core.lang.Assert;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.master.router.DtArgumentParamOrHeaderResolver;
import org.junit.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.ArrayList;
import java.util.List;

public class MvcConfigTest {

    private static final MvcConfig mvcConfig = new MvcConfig();

    static {
        PrivateAccessor.set(mvcConfig,"corsAllowedOrigins","*");
        PrivateAccessor.set(mvcConfig,"corsAllowedMethod",new String[]{"GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"});
        PrivateAccessor.set(mvcConfig,"dtArgumentParamOrHeaderResolver",new DtArgumentParamOrHeaderResolver());
    }


    @Test
    public void addCorsMappings() {
        mvcConfig.addCorsMappings(new CorsRegistry());
    }

    @Test
    public void loginInterceptor() {
        mvcConfig.loginInterceptor();
    }

    @Test
    public void authenticateHandlerInterceptor() {
        mvcConfig.authenticateHandlerInterceptor();
    }

//    @Test
//    public void configureMessageConverters() {
//        List<HttpMessageConverter<?>> converters = new ArrayList<>();
//        mvcConfig.configureMessageConverters(converters);
//    }

    @Test
    public void addArgumentResolvers() {
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
        mvcConfig.addArgumentResolvers(argumentResolvers);
    }

    @Test
    public void addInterceptors() {
        mvcConfig.addInterceptors(new InterceptorRegistry());
    }

    @Test
    public void getPluginPath() {
        Assert.notNull(mvcConfig.getPluginPath(true, "/"));
    }
}