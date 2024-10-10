package com.dtstack.engine.master.model.cluster.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.system.Context;
import dt.insight.plat.lang.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 存储组件(依赖的资源调度组件不带版本，如 k8s)
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-24 20:34
 */
public class DependNoVersionStoragePart extends StoragePart {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependNoVersionStoragePart.class);

    public DependNoVersionStoragePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource);
    }

    /**
     * 解析出存储组件
     * {
     *   "KUBERNETES": "kubernetes",
     *   "S3": {
     *     "CSP": {
     *       "FLINK": [
     *         {
     *           "1.12": "k8s-s3-flink112"
     *         }
     *       ],
     *       "S3": "s3"
     *     }
     *   },
     *   "NFS": {
     *     "": {
     *       "FLINK": [
     *         {
     *           "1.10": "k8s-nfs-flink110"
     *         }
     *       ],
     *       "NFS": "nfs"
     *     }
     *   },
     *   "HDFS": {
     *     "hdfs2": {
     *       "FLINK": [
     *         {
     *           "1.10": "k8s-hdfs2-flink110"
     *         },
     *         {
     *           "1.12": "k8s-hdfs2-flink112"
     *         }
     *       ],
     *       "SPARK": [
     *         {
     *           "2.4": "k8s-hdfs2-spark240"
     *         }
     *       ],
     *       "DT_SCRIPT": "k8s-hdfs2-dtscript",
     *       "HDFS": "k8s-hdfs2-hadoop2"
     *     },
     *     "hdfs3": {
     *       "FLINK": [
     *         {
     *           "1.10": "k8s-hdfs3-flink110"
     *         },
     *         {
     *           "1.12": "k8s-hdfs3-flink112"
     *         }
     *       ],
     *       "SPARK": [
     *         {
     *           "2.4": "k8s-hdfs3-spark240"
     *         }
     *       ],
     *       "DT_SCRIPT": "k8s-hdfs3-dtscript",
     *       "HDFS": "k8s-hdfs3-hadoop3"
     *     }
     *   }
     * }
     * @return
     */
    @Override
    public String getPluginName() {
        Component resourceComponent = tryGetResourceComponent();
        if (resourceComponent == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelConfig = context.getModelConfig(resourceType, resourceVersion);
        JSONObject storageModelConfig = resourceModelConfig.map(res -> res.getJSONObject(type.name())).orElseThrow(() ->
                new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(), resourceComponent.getComponentName(), type.name(), versionName))
        );
        String specifyVersionName;
        if (type == EComponentType.HDFS) {
            // 针对 HDFS，需要借助 versionValue 归类出 specifyStorageVersionName
            String versionValue = super.getVersionValue();
            if (StringUtils.isEmpty(versionValue) || versionValue.length() < 1) {
                throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_ERROR);
            }
            LOGGER.info("DependNoVersionStoragePart, type:HDFS, versionValue:{}", versionValue);
            // 根据 「3.1.1」 推导出 hdfs3
            specifyVersionName = super.hdfsPrefix + versionValue.substring(0, 1);
        } else {
            specifyVersionName = this.versionName;
        }
        String versionObjStr = storageModelConfig.getString(specifyVersionName);
        if (StringUtils.isEmpty(versionObjStr)) {
            throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_NOT_FOUND);
        }
        // 获取存储组件版本对应的配置信息
        JSONObject versionObj = JSONObject.parseObject(versionObjStr);
        if (versionObj == null) {
            throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_NOT_FOUND);
        }
        // 从配置信息中获取当前存储组件的名称
        return versionObj.getString(type.name());
    }

    /**
     * 解析存储组件的自定义参数
     * {
     *   "HDFS": {
     *     "hdfs2": {
     *       "SPARK": [
     *         {
     *           "2.1": "-1003",
     *           "2.4": "-1003"
     *         }
     *       ],
     *       "DT_SCRIPT": "-1005",
     *       "HDFS": -101
     *     },
     *     "hdfs3": {
     *       "SPARK": [
     *         {
     *           "2.1": "-1004",
     *           "2.4": "-1004"
     *         }
     *       ],
     *       "DT_SCRIPT": "-1006",
     *       "HDFS": -103
     *     }
     *   },
     *   "S3":{
     *     "CSP":{
     *       "S3":-102
     *     }
     *
     *   },
     *   "NFS":{
     *     "":{
     *       "NFS":-101
     *     }
     *   }
     * }
     * @return
     */
    @Override
    public Long getExtraVersionParameters() {
        Component resourceComponent = tryGetResourceComponent();
        if (resourceComponent == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());
        Optional<JSONObject> resourceModelExtraVersionParameters = context.getModelExtraVersionParameters(resourceType, resourceVersion);
        if (!resourceModelExtraVersionParameters.isPresent()) {
            return null;
        }
        JSONObject storageJsonObj = resourceModelExtraVersionParameters.map(res -> res.getJSONObject(type.name())).orElse(null);
        if (storageJsonObj == null) {
            return null;
        }
        String specifyVersionName;
        if (type == EComponentType.HDFS) {
            // 针对 HDFS，需要借助 versionValue 归类出 specifyStorageVersionName
            String versionValue = super.getVersionValue();
            if (StringUtils.isEmpty(versionValue) || versionValue.length() < 1) {
                throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_ERROR);
            }
            LOGGER.info("DependNoVersionStoragePart, type:HDFS, versionValue:{}", versionValue);
            // 根据 「3.1.1」 推导出 hadoop3
            specifyVersionName = super.hdfsPrefix + versionValue.substring(0, 1);
        } else {
            specifyVersionName = this.versionName;
        }

        String versionObjStr = storageJsonObj.getString(specifyVersionName);
        if (StringUtils.isEmpty(versionObjStr)) {
            return null;
        }
        // 获取存储组件版本对应的配置信息
        JSONObject versionObj = JSONObject.parseObject(versionObjStr);
        if (versionObj == null) {
            throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_NOT_FOUND);
        }
        // 从配置信息中获取当前存储组件的自定义参数模板 id
        return versionObj.getLong(type.name());
    }
}