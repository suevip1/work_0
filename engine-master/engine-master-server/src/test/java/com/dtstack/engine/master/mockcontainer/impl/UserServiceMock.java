package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.mapstruct.UserStruct;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.feign.Param;
import com.google.common.collect.Lists;

import java.util.List;

public class UserServiceMock {

    @MockInvoke(targetClass = UserStruct.class)
    UserDTO toDTO(UICUserVO uicUserVO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(uicUserVO.getUserName());
        userDTO.setDtuicUserId(uicUserVO.getUserId());
        return userDTO;
    }

    @MockInvoke(targetClass = UserStruct.class)
    User toUser(UICUserVO uicUserVO) {
        User userDTO = new User();
        userDTO.setUserName(uicUserVO.getUserName());
        userDTO.setDtuicUserId(uicUserVO.getUserId());
        return userDTO;
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    ApiResponse<List<UICUserVO>> getByUserIds(@Param("userIds") List<Long> var1) {
        UICUserVO uicUserVO = new UICUserVO();
        uicUserVO.setUserName("test");
        uicUserVO.setUserId(1L);
        ApiResponse<List<UICUserVO>> response = new ApiResponse<>();
        response.setData(Lists.newArrayList(uicUserVO));
        return response;
    }
}
