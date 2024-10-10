package com.dtstack.engine.master.mockcontainer.strategy;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.dao.BaselineJobJobDao;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.impl.BaselineTaskService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.QueryAuthProjectParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/14 10:55 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AbstractTemplateReplaceStrategyMock extends BaseMock {

    @MockInvoke(targetClass = BaselineTaskService.class)
    public BaselineTaskDTO getBaselineTaskDTO(Long id) {
        BaselineTaskDTO baselineTaskDTO = new BaselineTaskDTO();
        List<AlarmChooseTaskDTO> alarmChooseTaskDTOList = Lists.newArrayList();
        AlarmChooseTaskDTO alarmChooseTaskDTO = new AlarmChooseTaskDTO();
        alarmChooseTaskDTO.setTaskId(1L);
        alarmChooseTaskDTO.setAppType(1);
        alarmChooseTaskDTOList.add(alarmChooseTaskDTO);
        baselineTaskDTO.setTaskVOS(alarmChooseTaskDTOList);
        return baselineTaskDTO;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(1L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("1");
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = BaselineJobJobDao.class)
    List<String> selectJobIdByBaselineJobId(Long baselineJobId,
                                            Integer baselineTaskType){
        return Lists.newArrayList("1","2");
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJob> listByJobIds(List<String> jobIds) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("1");
        scheduleJob.setStatus(8);
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        scheduleJob.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = AuthCenterAPIClient.class)
    public ApiResponse<AuthProjectVO> findProject(QueryAuthProjectParam var1) {
        ApiResponse<AuthProjectVO> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        AuthProjectVO authProjectVO = new AuthProjectVO();
        apiResponse.setData(authProjectVO);
        return apiResponse;
    }

    @MockInvoke(targetClass = UicTenantApiClient.class)
    public ApiResponse<UICTenantVO> findTenantById(Long var1){
        ApiResponse<UICTenantVO> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        UICTenantVO vo = new UICTenantVO();
        vo.setTenantId(1L);
        vo.setTenantName("1L");
        vo.setTenantAdmin(true);
        vo.setTenantCreator(true);
        apiResponse.setData(vo);
        return apiResponse;
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    public ApiResponse<List<UICUserVO>> getByUserIds( List<Long> var1){
        ApiResponse<List<UICUserVO>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        UICUserVO vo = new UICUserVO();
        vo.setTenantId(1L);
        vo.setTenantName("1L");
        vo.setTenantAdmin(true);
        vo.setTenantCreator(true);
        List<UICUserVO> uicUserVOS = Lists.newArrayList(vo);
        apiResponse.setData(uicUserVOS);
        return apiResponse;
    }
}
