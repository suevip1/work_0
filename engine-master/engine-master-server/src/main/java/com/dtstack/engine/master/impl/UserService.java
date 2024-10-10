package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.master.mapstruct.UserStruct;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UicUserApiClient uicUserApiClient;

    @Autowired
    private UserStruct userStruct;

    /**
     * 封装user
     *
     * @param vos
     */
    public void fillUser(List<ScheduleTaskVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }

        List<Long> userIds = vos.stream().map(ScheduleTaskVO::getOwnerUserId).collect(Collectors.toList());
        List<Long> createUserIds = vos.stream().map(ScheduleTaskVO::getCreateUserId).collect(Collectors.toList());
        List<Long> modifyUserIds = vos.stream().map(ScheduleTaskVO::getModifyUserId).collect(Collectors.toList());

        Set<Long> userSetIds = new HashSet<>();
        userSetIds.addAll(createUserIds);
        userSetIds.addAll(modifyUserIds);
        userSetIds.addAll(userIds);

        if (CollectionUtils.isNotEmpty(userSetIds)) {
            Map<Long, List<UserDTO>> userMaps = getDtCenterUser(userSetIds, uicUserVO -> userStruct.toDTO(uicUserVO));
            vos.forEach(vo -> {
                fillProperties(userMaps, vo.getOwnerUserId(), (vo::setOwnerUser));
                fillProperties(userMaps, vo.getCreateUserId(), (vo::setCreateUser));
                fillProperties(userMaps, vo.getModifyUserId(), (vo::setModifyUser));
            });
        }

    }


    public <R,M> void fillProperties(Map<M,List<R>> map, M key, Consumer<R> consumer){
        Optional.ofNullable(map.get(key))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .findFirst()
                .ifPresent(consumer);
    }


    private <R> Map<Long, List<R>> getDtCenterUser(Set<Long> userSetIds, Function<UICUserVO, R> function) {
        List<Long> ids = null;
        if(CollectionUtils.isNotEmpty(userSetIds)){
            ids = userSetIds.stream().filter(id -> id != null && id > 0).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(ids)){
            return new HashMap<>();
        }
        ApiResponse<List<UICUserVO>> users = uicUserApiClient.getByUserIds(new ArrayList<>(ids));
        List<UICUserVO> usersData = users.getData();
        if(CollectionUtils.isEmpty(usersData)){
            return new HashMap<>();
        }
        return usersData.stream().collect(Collectors.groupingBy(UICUserVO::getUserId, Collectors.mapping(function, Collectors.toList())));
    }

    public User findByUser(Long userId){
        if(null == userId){
            return null;
        }
        HashSet<Long> userIds = new HashSet<>();
        userIds.add(userId);
        Map<Long, List<User>> dtCenterUser = getDtCenterUser(userIds, (uicUserVO -> userStruct.toUser(uicUserVO)));
        if(dtCenterUser.containsKey(userId)){
            return dtCenterUser.get(userId).get(0);
        }
        return null;
    }

    public List<User> findUserWithFill(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        Map<Long, List<User>> dtCenterUser = getDtCenterUser(userIds, (uicUserVO -> userStruct.toUser(uicUserVO)));
        return dtCenterUser.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void fillFillDataJobUserName(List<ScheduleFillDataJobPreViewVO> resultContent) {
        Set<Long> userId = resultContent.stream().map(ScheduleFillDataJobPreViewVO::getDutyUserId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(userId)) {
            Map<Long, List<User>> userMaps = getDtCenterUser(userId, (uicUserVO -> userStruct.toUser(uicUserVO)));
            resultContent.forEach(viewVO -> fillProperties(userMaps, viewVO.getDutyUserId(), (user -> viewVO.setDutyUserName(user.getUserName()))));
        }
    }

    public void fillTopOrderJobUserName(List<JobTopOrderVO> jobTopOrderVOS) {
        if (CollectionUtils.isEmpty(jobTopOrderVOS)) {
            return;
        }
        Set<Long> ownerUserIds = jobTopOrderVOS.stream()
                .map(JobTopOrderVO::getOwnerUserId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(ownerUserIds)) {
            return;
        }
        Map<Long, List<User>> userMaps = getDtCenterUser(ownerUserIds, (uicUserVO -> userStruct.toUser(uicUserVO)));
        jobTopOrderVOS.forEach(jobTopOrderVO -> fillProperties(userMaps,jobTopOrderVO.getOwnerUserId(),user -> jobTopOrderVO.setOwnerUserName(user.getUserName())));
    }

    public void fillScheduleTaskForFillDataDTO(List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS) {
        try {
            if (CollectionUtils.isEmpty(scheduleTaskForFillDataDTOS)) {
                return;
            }

            List<Long> userIds = scheduleTaskForFillDataDTOS.stream().map(ScheduleTaskForFillDataDTO::getOwnerUserId).collect(Collectors.toList());
            List<Long> createUserIds = scheduleTaskForFillDataDTOS.stream().map(ScheduleTaskForFillDataDTO::getCreateUserId).collect(Collectors.toList());

            Set<Long> userSetIds = new HashSet<>();
            userSetIds.addAll(createUserIds);
            userSetIds.addAll(userIds);

            if (CollectionUtils.isNotEmpty(userIds)) {
                Map<Long, List<UserDTO>> userMaps = getDtCenterUser(userSetIds, (uicUserVO -> userStruct.toDTO(uicUserVO)));
                scheduleTaskForFillDataDTOS.forEach(vo -> {
                    fillProperties(userMaps, vo.getCreateUserId(), (vo::setCreateUser));
                    fillProperties(userMaps, vo.getOwnerUserId(), (vo::setOwnerUser));
                });
            }

        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void fillScheduleJobVO(List<ScheduleJobVO> jobVOS) {
        try {
            if (CollectionUtils.isEmpty(jobVOS)) {
                return;
            }
            List<ScheduleTaskVO> taskVOS = jobVOS.stream().map(ScheduleJobVO::getBatchTask).collect(Collectors.toList());
            fillUser(taskVOS);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}