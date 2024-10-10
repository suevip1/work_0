package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.springframework.stereotype.Component;

/**
 * trino pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:17 2022/10/31
 * company: www.dtstack.com
 */
@Component
public class TrinoPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfigWithTask(JSONObject clusterConfigJson) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        ProjectVO projectVO = new ProjectVO(pluginInfoContext.getProjectId(), pluginInfoContext.getAppType());
        return JSONObject.parseObject(clusterService.trinoSqlInfo(pluginInfoContext.getDtUicTenantId(), pluginInfoContext.getDtUicUserId(), pluginInfoContext.getComponentVersionMap(), projectVO));
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.TRINO_SQL;
    }
}
