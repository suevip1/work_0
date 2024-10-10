package com.dtstack.engine.master.sync.baseline;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.mockcontainer.sync.baseline.DAGMapBaselineJobBuildrMock;
import com.dtstack.engine.master.sync.DtApplicationContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 3:01 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@MockWith(DAGMapBaselineJobBuildrMock.class)
public class DAGMapBaselineJobBuildrTest {

    @Test
    public void testDAGMapBaselineJobBuildr() {
        DAGMapBaselineJobBuildr dagMapBaselineJobBuildr = new DAGMapBaselineJobBuildr(new DtApplicationContext());
        Set<String> noExecCache = Sets.newHashSet();
        BaselineTaskDTO baselineTaskDTO = getBaselineTaskDTO();
        dagMapBaselineJobBuildr.buildBaselineJob("2022-12-13 00:00:00",baselineTaskDTO,noExecCache,"");

    }

    private BaselineTaskDTO getBaselineTaskDTO() {
        BaselineTaskDTO baselineTaskDTO = new BaselineTaskDTO();
        baselineTaskDTO.setId(1L);
        baselineTaskDTO.setAppType(1);
        baselineTaskDTO.setName("1");
        baselineTaskDTO.setProjectId(1L);
        baselineTaskDTO.setTenantId(1L);
        baselineTaskDTO.setOpenStatus(1);
        baselineTaskDTO.setOwnerUserId(1L);
        baselineTaskDTO.setReplyTime("18:00");
        baselineTaskDTO.setEarlyWarnMargin(30);
        baselineTaskDTO.setOwnerUserId(1L);

        AlarmChooseTaskDTO alarmChooseTaskDTO = new AlarmChooseTaskDTO();
        alarmChooseTaskDTO.setTaskName("1");
        alarmChooseTaskDTO.setTaskId(1L);
        alarmChooseTaskDTO.setAppType(1);
        List<AlarmChooseTaskDTO> alarmChooseTaskDTOList = Lists.newArrayList();
        alarmChooseTaskDTOList.add(alarmChooseTaskDTO);
        baselineTaskDTO.setTaskVOS(alarmChooseTaskDTOList);
        return baselineTaskDTO;
    }


}