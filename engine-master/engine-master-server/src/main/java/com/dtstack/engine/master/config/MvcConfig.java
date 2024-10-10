

package com.dtstack.engine.master.config;

import com.dtstack.engine.master.router.DtArgumentParamOrHeaderResolver;
import com.dtstack.engine.master.router.login.AuthenticateHandlerInterceptor;
import com.dtstack.engine.master.router.login.LoginInterceptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/07/08
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final List<String> AUTHENTICATE_LIST;

    static {
        AUTHENTICATE_LIST = Lists.newArrayList(
                "/node/enterConsolePermission",
                // 集群接口
                "/node/cluster/pageQuery",
                // 资源组接口
                "/node/console/clusterResources",
                "/node/resource/pageQuery",
                "/node/resource/pageGrantedProjects",
                "/node/resource/reversalGrant",
                "/node/resource/grantToProject",
                "/node/tenant/listBoundedTenants",
                "/node/resource/addOrUpdate",
                "/node/resource/delete",

                // dtScriptAgent 资源管理
                "/node/resource/dtScriptAgent/grantToProject",

                // 绑定租户接口
                "/node/cluster/getClusterBindingTenants",
                "/node/cluster/getTenantClusterInfo",
                "/node/tenant/bindingTenantWithResource",
                "/node/tenant/unbindTenant",
                "/node/tenant/bindingResource",
                "/node/account/bindAccount",
                "/node/account/bindAccountList",
                "/node/account/updateBindAccount",
                "/node/account/unbindAccount",
                "/node/cluster/updateTenantClusterCommonConfig",
                "/node/cluster/getTenantClusterCommonConfig",
                "/node/upload/component/addOrUpdateComponent",
                "/node/cluster/deleteCluster",

                // 全局参数
                "/node/param/getParams",
                "/node/param/deleteParam",
                "/node/param/addOrUpdateParam",

                // 自定义调度日期
                "/node/calender/getCalenders",
                "/node/calender/listTaskByCalender",
                "/node/calender/getCalenderTime",
                "/node/calender/deleteCalender",
                "/node/upload/addOrUpdateCalenderExcel",

                // 生命周期
                "/node/dataLife/getDataLifeConfigPage",
                "/node/dataLife/updateDataLifeConfig",


                // 队列管理
                "/node/console/overview",
                "/node/console/groupDetail",
                "/node/console/stopAll ",
                "/node/console/stopJob",
                "/node/console/stopJobList",

                // 生成记录
                "/node/console/listJobGenerationRecord",
                "/node/console/saveAlertConfig",

                // 控制台调度时间控制
                "/node/console/job/restrict/add",
                "/node/console/job/restrict/pageQuery",
                "/node/console/job/restrict/close",
                "/node/console/job/restrict/open",
                "/node/console/job/restrict/remove",
                "/node/console/job/restrict/existWaitOrRunning"
        );
    }

    @Autowired
    private DtArgumentParamOrHeaderResolver dtArgumentParamOrHeaderResolver;

    /**
     * 允许跨域的域名，默认是所有
     */
    @Value("${cors_allowed_origins:*}")
    private String corsAllowedOrigins;

    /**
     * 允许跨域的调用类型，默认所有
     */
    @Value("${cors_allowed_method:GET, POST, PUT, OPTIONS, DELETE, PATCH}")
    private String[] corsAllowedMethod;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsAllowedOrigins)
                .allowedHeaders("*/*")
                .allowedMethods(corsAllowedMethod);
    }

    @Value("${engine.console.upload.path:${user.dir}/upload}")
    private String uploadPath;

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public AuthenticateHandlerInterceptor authenticateHandlerInterceptor() {
        return new AuthenticateHandlerInterceptor();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(dtArgumentParamOrHeaderResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/node/**").excludePathPatterns("/node/openapi/**");
        registry.addInterceptor(authenticateHandlerInterceptor()).addPathPatterns(AUTHENTICATE_LIST);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(
                "classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }

    public String getPluginPath(boolean isTmp,String gateSource) {
        String tmp = isTmp ? "/tmp" : "/normal";
        return uploadPath + tmp + "/" + gateSource;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        converters.add(0, mappingJackson2HttpMessageConverter);
    }
}

