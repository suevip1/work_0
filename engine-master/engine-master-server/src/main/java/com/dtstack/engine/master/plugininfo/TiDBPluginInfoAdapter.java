package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * tidb pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:17 2022/10/31
 * company: www.dtstack.com
 */
@Component
public class TiDBPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfigWithTask(JSONObject clusterConfigJson) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        ProjectVO projectVO = new ProjectVO(pluginInfoContext.getProjectId(), pluginInfoContext.getAppType());
        return JSONObject.parseObject(clusterService.tiDBInfo(pluginInfoContext.getDtUicTenantId(), pluginInfoContext.getDtUicUserId(), pluginInfoContext.getComponentVersionMap(), projectVO));
    }

    @Override
    protected void afterProcessSelfConfig(JSONObject selfConfig) {
        String jdbcUrl = selfConfig.getString(ConfigConstant.JDBCURL);
        JSONObject paramsJson = getParamsJson(selfConfig);

        String currentSchema = paramsJson.getString("currentSchema");
        // 任务执行需要判断 currentSchema
        if (StringUtils.isBlank(currentSchema) && MapUtils.isNotEmpty(paramsJson)) {
            throw new RdosDefineException("tidb currentSchema Not allowed to be empty");
        }

        jdbcUrl = jdbcUrl.replace("/%s","/");

        if (StringUtils.isNotBlank(currentSchema)) {
            if (jdbcUrl.split("/", -1).length < 4) {
                jdbcUrl = jdbcUrl + "/" + currentSchema;
            } else if (jdbcUrl.endsWith("/")) {
                jdbcUrl = jdbcUrl + currentSchema;
            }
        }

        selfConfig.put("jdbcUrl", jdbcUrl);

    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.TIDB_SQL;
    }
}
