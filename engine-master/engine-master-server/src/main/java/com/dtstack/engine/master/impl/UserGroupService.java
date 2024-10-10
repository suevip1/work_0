package com.dtstack.engine.master.impl;


import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.pubsvc.sdk.usercenter.client.UicGroupApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.GroupVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.GroupParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/16
 */
@Service
public class UserGroupService {

    private static Logger LOGGER = LoggerFactory.getLogger(UserGroupService.class);

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private UicGroupApiClient uicGroupApiClient;


    public List<GroupVO> queryUserGroupListByName(String userGroupName){

        GroupParam groupParam = new GroupParam();
        groupParam.setGroupName(userGroupName);
        ApiResponse<List<GroupVO>> response = uicGroupApiClient.getGroupsByGroupName(groupParam);
        if(response !=null && response.getData()!=null){
            return response.getData();
        }else{
            return ListUtils.EMPTY_LIST;
        }
    }

    public List<GroupVO> getUserGroupByGroupIds(List<Long> groupIds){

        GroupParam groupParam = new GroupParam();
        groupParam.setGroupIdList(groupIds);
        ApiResponse<List<GroupVO>> response = uicGroupApiClient.getGroupByGroupIds(groupParam);
        if(response !=null && response.getData()!=null){
            return response.getData();
        }else{
            return ListUtils.EMPTY_LIST;
        }
    }

    public List<GroupVO> getUserGroupListByTenantId(Long tenantId){

        GroupParam groupParam = new GroupParam();
        groupParam.setTenantId(tenantId);
        ApiResponse<List<GroupVO>> response = uicGroupApiClient.findTenantGroup(groupParam);
        if(response !=null && response.getData()!=null){
            return response.getData();
        }else{
            return ListUtils.EMPTY_LIST;
        }
    }

    /**
     * 根据用户组id查找绑定的ldap用户组列表
     * @param groupIds
     * @return
     */
    public List<String> getLdapUserGroupByGroupIds(List<Long> groupIds,Long tenantId){

        GroupParam groupParam = new GroupParam();
        groupParam.setGroupIdList(groupIds);
        groupParam.setTenantId(tenantId);
        ApiResponse<List<String>> response = uicGroupApiClient.selectByGroupsAndTenantId(groupParam);
        if(response !=null && response.getData()!=null){
            return response.getData();
        }else{
            return ListUtils.EMPTY_LIST;
        }
    }




}