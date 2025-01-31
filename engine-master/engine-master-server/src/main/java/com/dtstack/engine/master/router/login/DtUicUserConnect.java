package com.dtstack.engine.master.router.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.UserIdVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import com.dtstack.engine.master.router.login.domain.DtUser;
import com.dtstack.engine.master.router.login.domain.UserTenant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/18
 */
@Component
public class DtUicUserConnect {

    private static Logger LOGGER = LoggerFactory.getLogger(DtUicUserConnect.class);

    private static final String LONGIN_TEMPLATE = "%s/api/publicService/userCenter/sdk/user/get-info?dtToken=%s";

    private static final String LONGIN_OUT_TEMPLATE = "%s/api/publicService/userCenter/logout";

    private static final String GET_IS_ROOT_UIC_USER_TEMPLATE = "%s/api/publicService/userCenter/sdk/user/isRootUser?userId=%s&dtToken=%s";

    private static final String GET_FULL_TENANTS = "%s/api/publicService/userCenter/account/user/get-full-tenants-by-name?tenantName=%s&productCode=%s";

    private static final String GET_TENANT_INFO = "%s/api/publicService/userCenter/tenant/detail/%s";

    private static final String GET_ALL_UIC_USER_TEMPLATE = "%s/api/publicService/userCenter/sdk/user/find-all-users?tenantId=%s&productCode=%s&dtToken=%s";

    private static final String GET_TENANT_BY_ID = "%s/api/publicService/userCenter/sdk/tenant/get-by-tenant-id?tenantId=%s";

    private static final String GET_USERS_BY_USERIDS="%s/api/publicService/userCenter/sdk/user/get-users-by-userIds?dtToken=%s";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String getDatasourceRandomNode(String url) {
        if (StringUtils.isNotBlank(url)) {
            String[] nodeUrls = Arrays.stream(url.split(",")).map(node -> {
                if (!node.startsWith("http://") && !node.startsWith("https://")) {
                    node = "http://" + node;
                }
                return node;
            }).toArray(String[]::new);
            Random random = new Random();
            int i = random.nextInt(nodeUrls.length);
            return nodeUrls[i];
        }
        return url;
    }


    public List<User> getUserByUserIds(String token, String url, List<Long> userIds) {
        List<User> users = Lists.newArrayList();
        try {
            url = getDatasourceRandomNode(url);
            List<UserIdVO> userIdVOS = Lists.newArrayList();

            for (Long userId : userIds) {
                UserIdVO userIdVO = new UserIdVO();
                userIdVO.setUserId(userId);
                userIdVOS.add(userIdVO);
            }

            String result = PoolHttpClient.post(String.format(GET_USERS_BY_USERIDS, url, token), userIdVOS);
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                List<DtUser> dataList = JSON.parseArray(JSON.toJSONString(mResult.get("data")), DtUser.class);

                for (DtUser dtUicUser : dataList) {
                    User user = new User();
                    user.setUserName(dtUicUser.getUserName());
                    user.setPhoneNumber(dtUicUser.getPhone());
                    user.setEmail(dtUicUser.getUserName());
                    user.setDtuicUserId(dtUicUser.getId());
                    user.setStatus(0);
                    users.add(user);
                }
            }


        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable tr) {
            LOGGER.error("", tr);
        }
        return users;
    }


    public void getInfo(String token, String url, Consumer<DtUicUser> resultHandler) {
        try {
            url = getDatasourceRandomNode(url);
            String result = PoolHttpClient.get(String.format(LONGIN_TEMPLATE, url, token),null);
            if (StringUtils.isBlank(result)) {
                LOGGER.error("uic access exception,please check...");
                resultHandler.accept(null);
                return;
            }

            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                Map<String, Object> data = (Map<String, Object>) mResult.get("data");
                DtUicUser dtUicUser = PublicUtil.mapToObject(data, DtUicUser.class);
                String isRootRes = PoolHttpClient.get(String.format(GET_IS_ROOT_UIC_USER_TEMPLATE, url, dtUicUser.getUserId(), token));
                dtUicUser.setOwnerOnly(dtUicUser.getTenantOwner());
                if (StringUtils.isNotBlank(isRootRes)) {
                    Boolean isRoot = JSONObject.parseObject(isRootRes).getBoolean("data");
                    if (isRoot) {
                        dtUicUser.setTenantOwner(isRoot);
                    }
                    dtUicUser.setRootOnly(isRoot);
                }
                resultHandler.accept(dtUicUser);
            }
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable tr) {
            LOGGER.error("", tr);
            resultHandler.accept(null);
        }

    }

    public boolean removeUicInfo(String token, String url) {
        Map<String, Object> cookies = Maps.newHashMap();
        cookies.put("dt_token", token);
        url = getDatasourceRandomNode(url);
        String result = PoolHttpClient.post(String.format(LONGIN_OUT_TEMPLATE, url), null, cookies);
        if (StringUtils.isBlank(result)) {
            LOGGER.error("uic loginout exception,please check...");
            return false;
        }
        try {
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("uic loginout exception,please check...", e);
        }
        return false;
    }

    public List<UserTenant> getUserTenants(String url, String token, String tenantName) {
        url = getDatasourceRandomNode(url);
        Map<String, Object> cookies = Maps.newHashMap();
        cookies.put("dt_token", token);
        List<UserTenant> userTenantList = Lists.newArrayList();
        try {
            String result = PoolHttpClient.get(String.format(GET_FULL_TENANTS, url, tenantName,"RDOS"), cookies);
            if (StringUtils.isBlank(result)) {
                LOGGER.warn("uic api returns null.");
                return Lists.newArrayList();
            }
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                List<UserTenant> dataList = JSON.parseArray(JSON.toJSONString(mResult.get("data")), UserTenant.class);
                if (!CollectionUtils.isEmpty(dataList)) {
                    userTenantList.addAll(dataList);
                }
            }
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable tr) {
            LOGGER.error("{}", tr);
        }
        return userTenantList;
    }


    public List<Map<String, Object>> getAllUicUsers(String url, String productCode, Long tenantId, String dtToken) {
        try {
            url = getDatasourceRandomNode(url);
            String result = PoolHttpClient.get(String.format(GET_ALL_UIC_USER_TEMPLATE, new Object[]{url, tenantId, productCode, dtToken}), null);
            if (StringUtils.isBlank(result)) {
                LOGGER.warn("uic api returns null.");
                return Lists.newArrayList();
            }
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                return (List<Map<String, Object>>) mResult.get("data");
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return Lists.newArrayList();
    }


    public UserTenant getTenantByTenantId(String url,Long dtUicTenantId,String token){
        Map<String, Object> cookies = Maps.newHashMap();
        cookies.put("dt_token", token);

        try {
            url = getDatasourceRandomNode(url);
            String result = PoolHttpClient.get(String.format(GET_TENANT_BY_ID, url, dtUicTenantId), cookies);
            if (StringUtils.isBlank(result)) {
                LOGGER.warn("uic api returns null.");
                return null;
            }
            Map<String, Object> mResult = OBJECT_MAPPER.readValue(result, Map.class);
            if ((Boolean) mResult.get("success")) {
                UserTenant data = JSON.parseObject(JSON.toJSONString(mResult.get("data")), UserTenant.class);
                if (data != null){
                    return data;
                }
            }
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable tr) {
            LOGGER.error("{}", tr);
        }
        return null;
    }

    public void registerEvent(String uicUrl, PlatformEventType eventType, String callbackUrl, boolean active) {
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("eventCode", eventType.name());
        dataMap.put("productCode", "RDOS");
        dataMap.put("callbackUrl", callbackUrl);
        dataMap.put("active", active);
        dataMap.put("additionKey", "DAGScheduleX");

        try {
            String event = PoolHttpClient.post(String.format("%s/api/publicService/userCenter/sdk/platform/register-event", uicUrl), dataMap, (Map) null);
            LOGGER.info("register event {}",event);
        } catch (Exception e) {
            LOGGER.error("registerEvent {}",eventType.getComment(), e);
        }

    }

    public String getLdapUserName(Long dtUicUserId,String dtToken,String uicUrl){
        try {
            uicUrl = getDatasourceRandomNode(uicUrl);
            String data = PoolHttpClient.get(String.format("%s/api/user/get-info-by-id?userId=%s&dtToken=%s", uicUrl,dtUicUserId,dtToken),(Map) null);
            JSONObject jsonObject = JSONObject.parseObject(data);
            JSONObject dataObj = null;
            if(null != jsonObject){
                dataObj = jsonObject.getJSONObject("data");
            }
            if(null != dataObj && dataObj.getBooleanValue("ldapUser")){
                return dataObj.getString("userName");
            }
        } catch (Exception e) {
            LOGGER.error("getLdapUserName userId {} ",dtUicUserId,e);
        }
        return "";
    }
}
