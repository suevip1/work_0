package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.utils.MapUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

/**
 * spark pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/10/10
 * company: www.dtstack.com
 */
@Component
public class SparkPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfig(JSONObject clusterConfigJson, boolean isConsole) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        com.dtstack.engine.api.domain.Component component = pluginInfoContext.getComponent();
        // spark 没有 session
        EDeployMode deploy = EDeployMode.PERJOB;
        JSONObject confConfig = clusterConfigJson.getJSONObject(EComponentType.getByCode(component.getComponentTypeCode()).getConfName());
        if (MapUtils.isEmpty(confConfig)) {
            throw new RdosDefineException("Spark configuration information is empty");
        }
        JSONObject selfConfig = confConfig.getJSONObject(deploy.getMode());
        if (MapUtils.isEmpty(selfConfig)) {
            throw new RdosDefineException(String.format("Corresponding mode [%s] no information is configured", deploy.name()));
        }
        JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        if (Objects.nonNull(sftpConfig)) {
            String confHdfsPath = sftpConfig.getString("path") + File.separator + componentService.buildConfRemoteDir(component.getClusterId());
            selfConfig.put("confHdfsPath", confHdfsPath);
        }
        return selfConfig;
    }

    @Override
    protected void afterProcessPluginInfo(JSONObject pluginInfo) {
        // ignore
        MapUtil.removeBlankValue(pluginInfo);
    }

    @Override
    protected String getSelfConfName() {
        return EComponentType.SPARK.getConfName();
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.SPARK;
    }
}
