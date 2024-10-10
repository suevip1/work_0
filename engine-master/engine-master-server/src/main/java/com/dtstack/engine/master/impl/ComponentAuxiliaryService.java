package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.master.dto.ComponentProxyConfigDTO;
import com.dtstack.engine.master.enums.AuxiliaryTypeEnum;
import com.dtstack.engine.api.param.AuxiliaryQueryParam;
import com.dtstack.engine.api.param.AuxiliarySelectQueryParam;
import com.dtstack.engine.api.vo.auxiliary.AuxiliaryConfigVO;
import com.dtstack.engine.api.vo.auxiliary.AuxiliaryVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ConsoleComponentAuxiliaryConfigDao;
import com.dtstack.engine.dao.ConsoleComponentAuxiliaryDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.dto.ComponentAuxiliaryConfigDTO;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.plugininfo.PluginInfoCacheManager;
import com.dtstack.engine.master.utils.SM2Util;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleComponentAuxiliaryConfig;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-14 14:20
 */
@Service
public class ComponentAuxiliaryService {
    private static final String[] PROXY_CONFIGS = {
            "url",
            "user",
            "password"
    };
    private static final String PROXY_PASSWORD = "password";

    @Autowired
    private ConsoleComponentAuxiliaryConfigDao consoleComponentAuxiliaryConfigDao;

    @Autowired
    private ConsoleComponentAuxiliaryDao consoleComponentAuxiliaryDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleDictDao scheduleDictDao;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private PluginInfoCacheManager pluginInfoCacheManager;

    public List<ComponentConfig> loadAuxiliaryTemplate(ConsoleComponentAuxiliary auxiliary) {
        return loadAuxiliaryTemplate(auxiliary.getType());
    }

    public List<ComponentConfig> loadAuxiliaryTemplate(String auxiliaryType) {
        DictType dictType;
        try {
            dictType = Enum.valueOf(DictType.class, auxiliaryType);
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        ScheduleDict auxiliaryDict = scheduleDictDao.getByNameValue(dictType.type, dictType.name(), null, null);
        if (auxiliaryDict == null) {
            return Collections.emptyList();
        }
        return componentConfigDao.listByComponentId(Long.parseLong(auxiliaryDict.getDictValue()), true);
    }

    public List<ConsoleComponentAuxiliary> listAuxiliaries(ConsoleComponentAuxiliary auxiliary) {
        Long clusterId = auxiliary.getClusterId();
        Integer componentTypeCode = auxiliary.getComponentTypeCode();
        if (clusterId == null || componentTypeCode == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        ConsoleComponentAuxiliary auxiliaryFromDB = consoleComponentAuxiliaryDao.queryByClusterAndComponentAndType(clusterId, componentTypeCode);
        Integer open = null;
        if (auxiliaryFromDB == null || auxiliaryFromDB.getOpen().equals(GlobalConst.NONE)) {
            open = GlobalConst.NONE;
        } else {
            open = GlobalConst.YES;
        }

        List<ConsoleComponentAuxiliary> result = new ArrayList<>();
        for (AuxiliaryTypeEnum anEnum : AuxiliaryTypeEnum.values()) {
            ConsoleComponentAuxiliary oneResult = new ConsoleComponentAuxiliary();
            oneResult.setOpen(open);
            oneResult.setType(anEnum.name());
            result.add(oneResult);
        }
        return result;
    }

    public ComponentAuxiliaryConfigDTO queryAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary, boolean shouldEncrypt) {
        Long clusterId = auxiliary.getClusterId();
        Integer componentTypeCode = auxiliary.getComponentTypeCode();
        if (clusterId == null || componentTypeCode == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        ConsoleComponentAuxiliary auxiliaryFromDB = consoleComponentAuxiliaryDao.queryByClusterAndComponentAndType(clusterId, componentTypeCode);
        if (auxiliaryFromDB == null) {
            return null;
        }
        List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs = consoleComponentAuxiliaryConfigDao.listByAuxiliaryId(auxiliaryFromDB.getId());
        encryptAuxiliaryPwdIfNeeded(auxiliaryConfigs, shouldEncrypt);
        ComponentAuxiliaryConfigDTO auxiliaryConfigDTO = new ComponentAuxiliaryConfigDTO();
        auxiliaryConfigDTO.setAuxiliary(auxiliaryFromDB);
        auxiliaryConfigDTO.setAuxiliaryConfigs(auxiliaryConfigs);
        return auxiliaryConfigDTO;
    }

    /**
     * 新增或修改附属配置
     *
     * @param auxiliaryConfigDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAuxiliaryConfig(ComponentAuxiliaryConfigDTO auxiliaryConfigDTO) {
        this.processKnoxConfigIfNeeded(auxiliaryConfigDTO);

        ConsoleComponentAuxiliary auxiliary = auxiliaryConfigDTO.getAuxiliary();
        Long clusterId = auxiliary.getClusterId();
        Integer componentTypeCode = auxiliary.getComponentTypeCode();
        String type = auxiliary.getType();
        if (clusterId == null || componentTypeCode == null || StringUtils.isEmpty(type)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }

        AuxiliaryTypeEnum auxiliaryType = AuxiliaryTypeEnum.valueOfIgnoreCase(auxiliary.getType());
        if (Objects.isNull(auxiliaryType)) {
            throw new RdosDefineException(ErrorCode.AUXILIARY_TYPE_NOT_EXISTS);
        }

        consoleComponentAuxiliaryConfigDao.removeByClusterAndComponentAndType(clusterId, componentTypeCode, type);
        consoleComponentAuxiliaryDao.removeByClusterAndComponentAndType(clusterId, componentTypeCode, type);

        ConsoleComponentAuxiliary auxiliaryAddDomain = new ConsoleComponentAuxiliary();
        auxiliaryAddDomain.setComponentTypeCode(componentTypeCode);
        auxiliaryAddDomain.setClusterId(clusterId);
        auxiliaryAddDomain.setType(type);
        auxiliaryAddDomain.setOpen(GlobalConst.YES);
        consoleComponentAuxiliaryDao.insertSelective(auxiliaryAddDomain);

        List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs = auxiliaryConfigDTO.getAuxiliaryConfigs();
        List<ConsoleComponentAuxiliaryConfig> configs = auxiliaryConfigs.stream().map(s -> {
            ConsoleComponentAuxiliaryConfig target = new ConsoleComponentAuxiliaryConfig();
            target.setAuxiliaryId(auxiliaryAddDomain.getId());
            target.setKey(s.getKey());
            target.setValue(s.getValue());
            return target;
        }).collect(Collectors.toList());
        consoleComponentAuxiliaryConfigDao.batchSave(configs);
        pluginInfoCacheManager.clearPluginInfoClusterCache(clusterId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {
        Long clusterId = auxiliary.getClusterId();
        Integer componentTypeCode = auxiliary.getComponentTypeCode();
        if (clusterId == null || componentTypeCode == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        consoleComponentAuxiliaryConfigDao.removeByClusterAndComponentAndType(clusterId, componentTypeCode, auxiliary.getType());
        consoleComponentAuxiliaryDao.removeByClusterAndComponentAndType(clusterId, componentTypeCode, auxiliary.getType());
        pluginInfoCacheManager.clearPluginInfoClusterCache(clusterId);

    }

    @Transactional(rollbackFor = Exception.class)
    public void switchAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {
        Integer open = auxiliary.getOpen();
        if (!GlobalConst.YES.equals(open) && !GlobalConst.NONE.equals(open)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        Long clusterId = auxiliary.getClusterId();
        Integer componentTypeCode = auxiliary.getComponentTypeCode();
        String type = auxiliary.getType();
        if (clusterId == null || componentTypeCode == null || StringUtils.isEmpty(type)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        ConsoleComponentAuxiliary auxiliaryFromDB = consoleComponentAuxiliaryDao.queryByClusterAndComponentAndType(clusterId, componentTypeCode);
        if (auxiliaryFromDB == null) {
            if (GlobalConst.YES.equals(open)) {
                // 初始打开，新增一条记录
                ConsoleComponentAuxiliary auxiliaryAddDomain = new ConsoleComponentAuxiliary();
                auxiliaryAddDomain.setComponentTypeCode(componentTypeCode);
                auxiliaryAddDomain.setClusterId(clusterId);
                auxiliaryAddDomain.setType(auxiliary.getType());
                auxiliaryAddDomain.setOpen(GlobalConst.YES);
                consoleComponentAuxiliaryDao.insertSelective(auxiliaryAddDomain);
            }
            return;
        }
        consoleComponentAuxiliaryDao.switchAuxiliary(clusterId, componentTypeCode, type, auxiliary.getOpen());
        pluginInfoCacheManager.clearPluginInfoClusterCache(clusterId);
    }

    /**
     * 返回插件所需 json 信息
     * @param auxiliary
     * @return
     */
    public ComponentProxyConfigDTO queryOpenAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {
        Long clusterId = auxiliary.getClusterId();
        Integer componentTypeCode = auxiliary.getComponentTypeCode();
        ConsoleComponentAuxiliary auxiliaryFromDB = consoleComponentAuxiliaryDao.queryByClusterAndComponentAndTypeAndSwitch(clusterId, componentTypeCode,
                GlobalConst.YES, auxiliary.getType());
        if (auxiliaryFromDB == null) {
            return null;
        }
        List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs = consoleComponentAuxiliaryConfigDao.listByAuxiliaryId(auxiliaryFromDB.getId());
        if (CollectionUtils.isEmpty(auxiliaryConfigs)) {
            return null;
        }

        return transAuxiliaryConfig2ProxyConfig(auxiliaryFromDB, auxiliaryConfigs);
    }

        /**
         * 插件所需信息
         * @param auxiliary
         * @param auxiliaryConfigs
         * @return
         */
    private ComponentProxyConfigDTO transAuxiliaryConfig2ProxyConfig(ConsoleComponentAuxiliary auxiliary, List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs) {
        if (auxiliary == null || CollectionUtils.isEmpty(auxiliaryConfigs)) {
            return null;
        }

        ComponentProxyConfigDTO configInfo = new ComponentProxyConfigDTO();
        configInfo.setType(auxiliary.getType());
        JSONObject jsonObject = new JSONObject();
        for (ConsoleComponentAuxiliaryConfig auxiliaryConfig : auxiliaryConfigs) {
            jsonObject.put(auxiliaryConfig.getKey(), auxiliaryConfig.getValue());
        }
        configInfo.setConfig(jsonObject);
        return configInfo;
    }

    private void processKnoxConfigIfNeeded(ComponentAuxiliaryConfigDTO auxiliaryConfigDTO) {
        ConsoleComponentAuxiliary auxiliary = auxiliaryConfigDTO.getAuxiliary();
        List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs = auxiliaryConfigDTO.getAuxiliaryConfigs();
        if (auxiliary == null || CollectionUtils.isEmpty(auxiliaryConfigs) || StringUtils.isEmpty(auxiliary.getType())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        AuxiliaryTypeEnum auxiliaryType = AuxiliaryTypeEnum.valueOfIgnoreCase(auxiliary.getType());
        if (Objects.isNull(auxiliaryType)) {
            return;
        }
        for (ConsoleComponentAuxiliaryConfig auxiliaryConfig : auxiliaryConfigs) {
            String key = auxiliaryConfig.getKey();
            String value = auxiliaryConfig.getValue();
            if (!ArrayUtils.contains(auxiliaryType.getConfigParams(), key)) {
                throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS + ":" + key);
            }
            if (PROXY_PASSWORD.equals(key)) {
                // 后台存储的是解密后的密码
                auxiliaryConfig.setValue(SM2Util.decrypt(value, environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey()));
            }
        }
    }

    private void encryptAuxiliaryPwdIfNeeded(List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs, boolean shouldEncrypt) {
        if (!shouldEncrypt) {
            return;
        }
        if (CollectionUtils.isEmpty(auxiliaryConfigs)) {
            return;
        }
        for (ConsoleComponentAuxiliaryConfig auxiliaryConfig : auxiliaryConfigs) {
            String key = auxiliaryConfig.getKey();
            String value = auxiliaryConfig.getValue();
            if (PROXY_PASSWORD.equals(key) && StringUtils.isNotEmpty(value)) {
                // 返回给前台加密后的密码
                auxiliaryConfig.setValue(SM2Util.encrypt(value, environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey()));
            }
        }
    }

    /**
     * 查询某一组件的附属配置
     *
     * @param auxiliaryQueryParam 查询条件
     * @param shouldEncrypt       是否解密
     * @return 组件附属配置
     */
    public List<AuxiliaryConfigVO> queryConfig(AuxiliaryQueryParam auxiliaryQueryParam, boolean shouldEncrypt) {
        List<ConsoleComponentAuxiliary> auxiliaries = consoleComponentAuxiliaryDao.queryByClusterAndComponentCodeAndType(auxiliaryQueryParam.getClusterId(), auxiliaryQueryParam.getComponentTypeCode(), auxiliaryQueryParam.getTypes());
        if (CollectionUtils.isEmpty(auxiliaries)) {
            return Collections.emptyList();
        }
        List<AuxiliaryConfigVO> result = Lists.newArrayList();
        for (ConsoleComponentAuxiliary auxiliary : auxiliaries) {
            List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs = consoleComponentAuxiliaryConfigDao.listByAuxiliaryId(auxiliary.getId());
            encryptAuxiliaryPwdIfNeeded(auxiliaryConfigs, shouldEncrypt);
            AuxiliaryConfigVO configVO = new AuxiliaryConfigVO();
            configVO.setClusterId(auxiliary.getClusterId());
            configVO.setComponentTypeCode(auxiliary.getComponentTypeCode());
            configVO.setType(auxiliary.getType());
            List<AuxiliaryVO> Auxiliaries = Lists.newArrayList();
            for (ConsoleComponentAuxiliaryConfig auxiliaryConfig : auxiliaryConfigs) {
                AuxiliaryVO auxiliaryVO = new AuxiliaryVO();
                auxiliaryVO.setKey(auxiliaryConfig.getKey());
                auxiliaryVO.setValue(auxiliaryConfig.getValue());
                Auxiliaries.add(auxiliaryVO);
            }
            configVO.setAuxiliaries(Auxiliaries);
            result.add(configVO);
        }
        return result;
    }

    /**
     * 查询某一个附属配置执行 key 的 select 标签值
     *
     * @param auxiliarySelectQueryParam 查询条件
     * @return select 可选项
     */
    public List<String> queryAuxiliarySelect(AuxiliarySelectQueryParam auxiliarySelectQueryParam) {
        List<ComponentConfig> componentConfigs = loadAuxiliaryTemplate(auxiliarySelectQueryParam.getType());
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return Collections.emptyList();
        }
        for (ComponentConfig componentConfig : componentConfigs) {
            if (StringUtils.equals(componentConfig.getKey(), auxiliarySelectQueryParam.getSelectKey())
                    && StringUtils.isNotBlank(componentConfig.getValues())) {
                return Arrays.stream(componentConfig.getValues().split(",")).map(String::trim).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}