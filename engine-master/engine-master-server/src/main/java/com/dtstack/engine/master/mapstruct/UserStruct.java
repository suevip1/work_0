package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICUserListResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface UserStruct {

    @Mapping(source = "userId", target = "dtuicUserId")
    @Mapping(source = "phone", target = "phoneNumber")
    UserDTO toDTO(UICUserVO uicUserVO);

    @Mapping(source = "userId", target = "dtuicUserId")
    User toUser(UICUserVO uicUserVO);

    UicUserVo toUicUserVo(UICUserVO uicUserVO);

    List<UicUserVo> toUicUserVO(List<UICUserListResult> uicUserListResult);

}
