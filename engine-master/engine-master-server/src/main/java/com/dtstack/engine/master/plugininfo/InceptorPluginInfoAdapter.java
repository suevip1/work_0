package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * inceptor pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:17 2022/10/31
 * company: www.dtstack.com
 */
@Component
public class InceptorPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfigWithTask(JSONObject clusterConfigJson) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        ProjectVO projectVO = new ProjectVO(pluginInfoContext.getProjectId(), pluginInfoContext.getAppType());
        return JSONObject.parseObject(clusterService.inceptorSqlInfo(pluginInfoContext.getDtUicTenantId(), pluginInfoContext.getDtUicUserId(), projectVO));
    }

    @Override
    protected void afterProcessSelfConfig(JSONObject selfConfig) {
        //  inceptor 将 %s 替换成默认值
        replaceDefaultSchema(selfConfig);
        dealUrlParam(selfConfig);
    }

    /**
     * 替换默认 schema
     *
     * @param selfConfig 自身组件配置信息
     */
    public void replaceDefaultSchema(JSONObject selfConfig) {
        String jdbcUrl = selfConfig.getString(ConfigConstant.JDBCURL);
        if (StringUtils.isNotBlank(jdbcUrl)) {
            // %s替换成默认的 供插件使用
            jdbcUrl = jdbcUrl.replace("/%s", environmentContext.getComponentJdbcToReplace());
            selfConfig.put(ConfigConstant.JDBCURL, jdbcUrl);
        }
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.INCEPTOR_SQL;
    }
}
