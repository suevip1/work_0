package com.dtstack.engine.master.cron;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobGraphDao;
import com.dtstack.engine.po.ScheduleJobGraph;
import com.dtstack.engine.po.TodayJobGraph;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListQueryProParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduleJobGraphCronTest {
    ScheduleJobGraphCron cron = new ScheduleJobGraphCron();

    @Test
    public void test() {
        AtomicBoolean isMaster = new AtomicBoolean(true);
        cron.handle();
    }


    public static class Mock {
        @MockInvoke(
                targetClass = ClusterTenantDao.class,
                targetMethod = "listAllDtUicTenantId"
        )
        public List<Long> listAllDtUicTenantId() {
            return Lists.newArrayList(1L);
        }

        @MockInvoke(
                targetClass = AuthCenterAPIClient.class,
                targetMethod = "listProByAppTypeAndTenantId"
        )
        public ApiResponse<List<AuthProjectVO>> listProByAppTypeAndTenantId(ListQueryProParam var1) {
            AuthProjectVO authProjectVO = new AuthProjectVO();
            authProjectVO.setAppType(1);
            authProjectVO.setProjectId(1L);
            ApiResponse<List<AuthProjectVO>> response = new ApiResponse<>();
            response.setData(Lists.newArrayList(authProjectVO));
            response.setSuccess(true);
            return response;
        }


        @MockInvoke(
                targetClass = ScheduleJobDao.class,
                targetMethod = "listTodayJobGraph"
        )
        public List<TodayJobGraph> listTodayJobGraph(String today, List<Integer> statusList,
                                                     Integer type, Long projectId, Long tenantId, Integer appType) {
            TodayJobGraph todayJobGraph = new TodayJobGraph();
            todayJobGraph.setCount(1);
            todayJobGraph.setHour(1);
            return Lists.newArrayList(todayJobGraph);
        }

        @MockInvoke(
                targetClass = ScheduleJobGraphDao.class,
                targetMethod = "batchInsert"
        )
        public void batchInsert(List<ScheduleJobGraph> scheduleJobGraphs) {
        }
    }
}
