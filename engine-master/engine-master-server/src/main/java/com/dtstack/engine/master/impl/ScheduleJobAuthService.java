package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.loader.engine.SourceConstant;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.api.param.ScheduleJobAuthParam;
import com.dtstack.engine.api.vo.job.ScheduleJobAuthVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.ESubmitAuthType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobAuthDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.mapstruct.ScheduleJobAuthStruct;
import com.dtstack.engine.po.ScheduleJobAuth;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-07-28 10:28
 */
@Service
public class ScheduleJobAuthService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobAuthService.class);

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private ScheduleJobAuthDao scheduleJobAuthDao;

    @Autowired
    private ScheduleJobAuthStruct scheduleJobAuthStruct;

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * 保存提交任务的认证信息
     * proxy > kerberos/tbds > hadoopUserName
     *
     * @param jobClient 任务相关信息
     */
    public void saveSubmitAuthInfo(JobClient jobClient) {
        JSONObject pluginInfo = JSONObject.parseObject(jobClient.getPluginInfo());
        if (pluginInfo == null) {
            return;
        }

        ScheduleJobAuthParam authParam = new ScheduleJobAuthParam();
        authParam.setJobId(jobClient.getTaskId());
        ScheduleJobAuthVO oldJobAuth = getJobAuth(authParam);

        JSONObject securityConfJson = (JSONObject) JSONPath.eval(pluginInfo, "$.securityConf");
        JSONObject selfConfConfJson = (JSONObject) JSONPath.eval(pluginInfo, "$.selfConf");
        JSONObject commonConfJson = (JSONObject) JSONPath.eval(pluginInfo, "$.commonConf");

        SubmitAuth submitAuth = this.findAuth(securityConfJson, commonConfJson, selfConfConfJson, jobClient.getEngineType());
        if (submitAuth == null) {
            if (oldJobAuth != null) {
                LOGGER.info("clearSubmitAuthInfo, jobId:{}, old authId:{}", jobClient.getTaskId(), oldJobAuth.getId());
                scheduleJobExpandDao.updateAuthId(null, jobClient.getTaskId());
            }
            return;
        }

        ESubmitAuthType eSubmitAuthType = submitAuth.getSubmitAuthType();
        // 提交用户
        String submitUserName = submitAuth.getSubmitUserName();
        // 无需认证且无提交用户的情况，跳过后续流程
        if (ESubmitAuthType.NONE == eSubmitAuthType && StringUtils.isEmpty(submitUserName)) {
            if (oldJobAuth != null) {
                LOGGER.info("clearSubmitAuthInfo, jobId:{}, old authId:{}", jobClient.getTaskId(), oldJobAuth.getId());
                scheduleJobExpandDao.updateAuthId(null, jobClient.getTaskId());
            }
            return;
        }

        JSONObject submitAuthJson = submitAuth.getSubmitAuthJson();
        String authBizName = eSubmitAuthType.findAuthBizName(submitAuthJson, submitUserName);
        ScheduleJobAuth jobAuthFromDB = scheduleJobAuthDao.findByUnikey(jobClient.getUserId(), jobClient.getTenantId(),
                eSubmitAuthType.name(), authBizName);
        Long updateAuthId;
        if (jobAuthFromDB == null) {
            ScheduleJobAuth addAuth = new ScheduleJobAuth();
            addAuth.setUicUserId(jobClient.getUserId());
            addAuth.setDtuicTenantId(jobClient.getTenantId());
            addAuth.setAuthType(eSubmitAuthType.name());
            addAuth.setAuthBizName(authBizName);
            addAuth.setSubmitUserName(submitUserName);
            Optional.ofNullable(submitAuthJson)
                    .ifPresent(s -> addAuth.setAuthInfo(JSONObject.toJSONString(submitAuthJson)));
            scheduleJobAuthDao.insertIgnore(addAuth);
            updateAuthId = addAuth.getId();
        } else {
            scheduleJobAuthDao.updateByUniKey(jobClient.getUserId(), jobClient.getTenantId(),
                    eSubmitAuthType.name(), authBizName, submitUserName,
                    Optional.ofNullable(submitAuthJson).map(s -> JSONObject.toJSONString(s)).orElse(null));
            updateAuthId = jobAuthFromDB.getId();
        }

        // 旧的认证方式不存在，或者发生了变更，则需要更新
        if (oldJobAuth == null || (!oldJobAuth.getId().equals(updateAuthId))) {
            LOGGER.info("saveSubmitAuthInfo, jobId:{}, authId:{}", jobClient.getTaskId(), updateAuthId);
            scheduleJobExpandDao.updateAuthId(updateAuthId, jobClient.getTaskId());
        }
    }

    public void saveSubmitAuthInfoIgnoreThrow(JobClient jobClient) {
        try {
            saveSubmitAuthInfo(jobClient);
        } catch (Exception e) {
            LOGGER.error("saveSubmitAuthInfo error, jobId:{}", jobClient.getTaskId(), e);
        }
    }

    /**
     * 获取认证信息
     *
     * @param scheduleJobAuthParam
     * @return
     */
    public ScheduleJobAuthVO getJobAuth(ScheduleJobAuthParam scheduleJobAuthParam) {
        ScheduleJobExpand jobExpand = null;
        Long authId = null;
        // 运行次数为空，则获取最新一条
        boolean latest = Objects.isNull(scheduleJobAuthParam.getNum());
        if (latest) {
            jobExpand = scheduleJobExpandDao.getExpandByJobId(scheduleJobAuthParam.getJobId());
            if (Objects.isNull(jobExpand) || Objects.isNull(jobExpand.getAuthId())) {
                return null;
            } else {
                authId = jobExpand.getAuthId();
                return scheduleJobAuthStruct.toVO(scheduleJobAuthDao.selectById(authId));
            }
        } else {
            authId = scheduleJobExpandDao.getAuthId(scheduleJobAuthParam.getJobId(), scheduleJobAuthParam.getNum());
            if (Objects.isNull(authId)) {
                return null;
            } else {
                return scheduleJobAuthStruct.toVO(scheduleJobAuthDao.selectById(authId));
            }
        }
    }

    /**
     * 获取认证信息，proxy > kerberos/tbds > hadoopUserName
     * @param securityConfJson
     * @param commonConfJson
     * @param selfConfConfJson
     * @param engineType
     * @return
     */
    private SubmitAuth findAuth(JSONObject securityConfJson, JSONObject commonConfJson, JSONObject selfConfConfJson, String engineType) {
        if (securityConfJson == null) {
            if (commonConfJson == null) {
                return null;
            } else {
                String submitUserName = commonConfJson.getString(PluginInfoConst.HADOOP_USER_NAME_DOT);
                return StringUtils.isBlank(submitUserName) ? null : new SubmitAuth(ESubmitAuthType.NONE, submitUserName, null);
            }
        }

        /****** securityConfJson 不为空，走下面的逻辑 ******/
        Set<Integer> yarnSupportComponent = environmentContext.getLdapOnYarnSupportComponent();
        Set<Integer> rdbSupportComponent = environmentContext.getLdapOnRdbSupportComponent();
        EngineTypeComponentType engineTypeComponentType = EngineTypeComponentType.getByEngineName(engineType);
        Integer typeCode = engineTypeComponentType.getComponentType().getTypeCode();
        JSONObject kerberosConfJson = securityConfJson.getJSONObject(ESubmitAuthType.KERBEROS.getConfName());

        // proxy
        JSONObject proxyConfJson = securityConfJson.getJSONObject(ESubmitAuthType.PROXY.getConfName());
        if (proxyConfJson != null) {
            boolean hadoopProxyEnable = Optional.ofNullable(commonConfJson)
                    .map(s -> s.getBooleanValue(PluginInfoConst.HADOOP_PROXY_ENABLE))
                    .orElse(false);
            boolean hiveProxyEnable = Optional.ofNullable(selfConfConfJson)
                    .map(s -> s.getBooleanValue(SourceConstant.HIVE_PROXY_ENABLE))
                    .orElse(false);

            if (hadoopProxyEnable || hiveProxyEnable) {
                if (yarnSupportComponent.contains(typeCode) && kerberosConfJson != null) {
                    return new SubmitAuth(ESubmitAuthType.PROXY, proxyConfJson.getString(ConfigConstant.LDAP_USER_NAME), proxyConfJson);
                } else if (rdbSupportComponent.contains(typeCode)) {
                    return new SubmitAuth(ESubmitAuthType.PROXY, proxyConfJson.getString(ConfigConstant.LDAP_USER_NAME), proxyConfJson);
                }
            }
        }

        // ldap, ref: com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO.fillRdbmsSourceDTO
        JSONObject ldapConfJson = securityConfJson.getJSONObject(ESubmitAuthType.LDAP.getConfName());
        if (rdbSupportComponent.contains(typeCode) && ldapConfJson != null) {
            boolean hiveProxyEnable = Optional.ofNullable(selfConfConfJson)
                    .map(s -> s.getBooleanValue(SourceConstant.HIVE_PROXY_ENABLE))
                    .orElse(false);
            if (!hiveProxyEnable) {
                return new SubmitAuth(ESubmitAuthType.LDAP, ldapConfJson.getString(SourceConstant.USERNAME), ldapConfJson);
            }
        }

        // tbds
        JSONObject tbdsConfJson = securityConfJson.getJSONObject(ESubmitAuthType.TBDS.getConfName());
        if (tbdsConfJson != null) {
            String tbdsName = StringUtils.isNotBlank(tbdsConfJson.getString(SourceConstant.TBDS_USERNAME)) ?
                    tbdsConfJson.getString(SourceConstant.TBDS_USERNAME) : tbdsConfJson.getString(ConfigConstant.TBDS_USER_NAME);

            return new SubmitAuth(ESubmitAuthType.TBDS,
                    tbdsName, tbdsConfJson);
        }

        // kerberos
        if (kerberosConfJson != null) {
            // primary/instance@realm 或者 primary@realm
            String withoutRealm = StringUtils.substringBefore(kerberosConfJson.getString("principal"), "@");
            String submitUserName = StringUtils.substringBefore(withoutRealm, "/");
            return new SubmitAuth(ESubmitAuthType.KERBEROS, submitUserName, kerberosConfJson);
        }

        if (commonConfJson != null) {
            String submitUserName = commonConfJson.getString(PluginInfoConst.HADOOP_USER_NAME_DOT);
            return StringUtils.isBlank(submitUserName) ? null : new SubmitAuth(ESubmitAuthType.NONE, submitUserName, null);
        }
        return null;
    }

    private static class SubmitAuth {
        private ESubmitAuthType submitAuthType;
        private String submitUserName;
        private JSONObject submitAuthJson;

        public SubmitAuth(ESubmitAuthType submitAuthType, String submitUserName, JSONObject submitAuthJson) {
            this.submitAuthType = submitAuthType;
            this.submitUserName = submitUserName;
            this.submitAuthJson = submitAuthJson;
        }

        public ESubmitAuthType getSubmitAuthType() {
            return submitAuthType;
        }

        public void setSubmitAuthType(ESubmitAuthType submitAuthType) {
            this.submitAuthType = submitAuthType;
        }

        public String getSubmitUserName() {
            return submitUserName;
        }

        public void setSubmitUserName(String submitUserName) {
            this.submitUserName = submitUserName;
        }

        public JSONObject getSubmitAuthJson() {
            return submitAuthJson;
        }

        public void setSubmitAuthJson(JSONObject submitAuthJson) {
            this.submitAuthJson = submitAuthJson;
        }
    }
}