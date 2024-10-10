package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.dtstack.engine.common.constrant.ConfigConstant.TYPE_NAME;

/**
 * spark-thrift pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:57 2022/10/17
 * company: www.dtstack.com
 */
@Component
public class SparkThriftPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected void afterProcessSelfConfig(JSONObject selfConfig) {
        // sparkThrift 将 %s 替换成默认值
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
        return EComponentType.SPARK_THRIFT;
    }
}
