package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.engine.common.enums.EComponentType;
import org.springframework.stereotype.Component;

/**
 * oceanBase pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:17 2022/10/31
 * company: www.dtstack.com
 */
@Component
public class OceanBasePluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfigWithTask(JSONObject clusterConfigJson) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        ProjectVO projectVO = new ProjectVO(pluginInfoContext.getProjectId(), pluginInfoContext.getAppType());
        return JSONObject.parseObject(clusterService.oceanBaseInfo(pluginInfoContext.getDtUicTenantId(), pluginInfoContext.getDtUicUserId(), pluginInfoContext.getComponentVersionMap(), projectVO));
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.OCEANBASE;
    }
}
