package com.dtstack.engine.master.mockcontainer.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.console.SecurityResult;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.engine.api.vo.role.RoleVO;
import com.dtstack.engine.master.mapstruct.RoleStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthAllPermissionParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthPermissionParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthRoleUserParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.DefaultRoleQueryParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListQueryPmByRoleIdParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListRUByRoleIdAndPIdParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.RemoveAuthRoleUserParam;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.RoleByIdQueryParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthPermissionVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleUserVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.assertj.core.util.Lists;

import java.util.List;

public class RoleServiceMock extends BaseMock {

    @MockInvoke(targetClass = UicUserApiClient.class)
    ApiResponse<List<UICUserVO>> getByUserIds(List<Long> var1){
        UICUserVO uicUserVo = new UICUserVO();
        uicUserVo.setUserId(1l);
        ApiResponse<List<UICUserVO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Lists.newArrayList(uicUserVo));
        return apiResponse;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<SecurityResult<Integer>> removeRoleUserByParam(RemoveAuthRoleUserParam param) {
        return new ApiResponse<>();
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<Boolean> addRoleUser(AuthRoleUserParam authRoleUserParam) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setData(true);
        return apiResponse;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthPermissionVO>> findPermissionListByUserId(AuthPermissionParam param){
        AuthPermissionVO authRoleVO = new AuthPermissionVO();
        authRoleVO.setType(1);
        authRoleVO.setCode("test");
        authRoleVO.setParentId(1L);
        authRoleVO.setId(-1L);
        ApiResponse<List<AuthPermissionVO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Lists.newArrayList(authRoleVO));
        return apiResponse;
    }


    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthRoleUserVO>> listByRoleIdsAndProId(ListRUByRoleIdAndPIdParam listRUByRoleIdAndPIdParam) {
        AuthRoleUserVO authRoleUserVO = new AuthRoleUserVO();
        authRoleUserVO.setAppType(1);
        authRoleUserVO.setRoleId(1L);
        authRoleUserVO.setDtuicUserId(1L);
        authRoleUserVO.setDtuicTenantId(1L);
        authRoleUserVO.setGmtCreate(DateTime.now());
        ApiResponse<List<AuthRoleUserVO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Lists.newArrayList(authRoleUserVO));
        return apiResponse;

    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthPermissionVO>> getPermissionListByRoleId(ListQueryPmByRoleIdParam listQueryPmByRoleIdParam) {
        AuthPermissionVO authRoleVO = new AuthPermissionVO();
        authRoleVO.setType(1);
        authRoleVO.setCode("test");
        authRoleVO.setParentId(1L);
        authRoleVO.setId(-1L);
        ApiResponse<List<AuthPermissionVO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Lists.newArrayList(authRoleVO));
        return apiResponse;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<List<AuthPermissionVO>> findPermissionListByAppType(AuthAllPermissionParam param) {
        AuthPermissionVO authRoleVO = new AuthPermissionVO();
        authRoleVO.setType(1);
        authRoleVO.setCode("test");
        authRoleVO.setParentId(1L);
        authRoleVO.setId(-1L);
        ApiResponse<List<AuthPermissionVO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Lists.newArrayList(authRoleVO));
        return apiResponse;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<AuthRoleVO> getRoleById(RoleByIdQueryParam roleByIdQueryParam) {
        AuthRoleVO authRoleVO = new AuthRoleVO();
        authRoleVO.setAppType(1);
        authRoleVO.setProjectId(1L);
        authRoleVO.setRoleName("test");
        authRoleVO.setRoleValue(1);
        ApiResponse<AuthRoleVO> apiResponse = new ApiResponse<>();
        apiResponse.setData(authRoleVO);
        return apiResponse;
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    ApiResponse<PageResult<List<AuthRoleVO>>> pageByRoleParam(DefaultRoleQueryParam roleQuery) {
        AuthRoleVO authRoleVO = new AuthRoleVO();
        authRoleVO.setAppType(1);
        authRoleVO.setProjectId(1L);
        authRoleVO.setRoleName("test");
        authRoleVO.setRoleValue(1);
        ApiResponse<PageResult<List<AuthRoleVO>>> apiResponse = new ApiResponse<>();
        apiResponse.setData(new PageResult<>(Lists.newArrayList(authRoleVO), 1, new PageQuery()));
        return apiResponse;
    }


    @MockInvoke(targetClass = RoleStruct.class)
    RoleVO toRoleVO(AuthRoleVO roleVO) {
        RoleVO vo = new RoleVO();
        vo.setRoleName(roleVO.getRoleName());
        vo.setRoleValue(roleVO.getRoleValue());
        return vo;
    }
}
