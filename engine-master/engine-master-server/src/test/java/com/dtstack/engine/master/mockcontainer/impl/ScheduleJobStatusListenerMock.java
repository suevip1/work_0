package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/6/26 8:30 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobStatusListenerMock {

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public List<ScheduleJob> listNoDeletedByJobIds(Collection<String> jobIds){
        return Lists.newArrayList(new ScheduleJob());
    }


}
