package com.dtstack.engine.master.config;

import com.dtstack.dtcenter.common.convert.load.SourceLoaderService;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.pubsvc.sdk.alert.channel.service.AlertApiClient;
import com.dtstack.pubsvc.sdk.alert.channel.service.AlertNotifyApiClient;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.ranger.PubRangerServiceClient;
import com.dtstack.pubsvc.sdk.usercenter.client.*;
import com.dtstack.sdk.core.common.DtInsightApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author yuebai
 * @date 2021-04-06
 */
@Configuration
public class SdkConfig {

    @Autowired
    private EnvironmentContext context;


    @Bean(name = "dataSourceApi")
    public DtInsightApi getDataSourceApi() {
        String urlNodes = context.getPublicServiceNode();
        return getDtInsightApiByUrlNodes(urlNodes);
    }

    /**
     * 数据源中心
     *
     * @param dataSourceApi
     * @return
     */
    @Bean
    public DataSourceAPIClient getDataSourceClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(DataSourceAPIClient.class);
    }


    @Bean
    public AuthCenterAPIClient getAuthCenterAPIClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(AuthCenterAPIClient.class);
    }

    @Bean
    public UicTenantApiClient getUicTenantApi(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UicTenantApiClient.class);
    }

    @Bean
    public PubRangerServiceClient getPubRangerClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(PubRangerServiceClient.class);
    }

    @Bean
    public UicUserApiClient getUicUserApi(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UicUserApiClient.class);
    }

    @Bean
    public UIcUserTenantRelApiClient getUIcUserTenantRelApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UIcUserTenantRelApiClient.class);
    }

    @Bean
    public UicKerberosApiClient getUicKerberosApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi){
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UicKerberosApiClient.class);
    }

    @Bean
    public AlertNotifyApiClient getAlertNotifyApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(AlertNotifyApiClient.class);
    }

    @Bean
    public AlertApiClient getAlertApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(AlertApiClient.class);
    }

    @Bean
    public UicSubProductModuleApiClient getUicSubProductModuleApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UicSubProductModuleApiClient.class);
    }

    @Bean
    public UicGroupApiClient getUicGroupApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UicGroupApiClient.class);
    }

    @Bean
    public UicLicenseApiClient getUicLicenseApiClient(@Qualifier("dataSourceApi")DtInsightApi dataSourceApi) {
        if (null == dataSourceApi) {
            return null;
        }
        return dataSourceApi.getSlbApiClient(UicLicenseApiClient.class);
    }

    @Bean
    public SourceLoaderService getSourceLoadService(DataSourceAPIClient dataSourceAPIClient, UIcUserTenantRelApiClient userTenantRelApiClient){
        return new SourceLoaderService(dataSourceAPIClient,userTenantRelApiClient);
    }


    private DtInsightApi getDtInsightApiByUrlNodes(String urlNodes) {
        String[] nodeUrls = Arrays.stream(urlNodes.split(",")).map(node -> {
            if (!node.startsWith("http://") && !node.startsWith("https://")) {
                node = "http://" + node;
            }
            return node;
        }).toArray(String[]::new);
        DtInsightApi.ApiBuilder builder = new DtInsightApi.ApiBuilder()
                .setServerUrls(nodeUrls)
                .setToken(context.getSdkToken().trim());
        return builder.buildApi();
    }

}

