package com.dtstack.engine.master.cron;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobGraphDao;
import com.dtstack.engine.master.handler.AbstractMasterHandler;
import com.dtstack.engine.po.ScheduleJobGraph;
import com.dtstack.engine.po.TodayJobGraph;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.ListQueryProParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName ScheduleJobGraphCron
 * @date 2022/8/2 7:39 PM
 */
@Component
public class ScheduleJobGraphCron {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobGraphCron.class);

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter timeFormatterDay = DateTimeFormat.forPattern("yyyyMMdd");

    @Autowired
    private ScheduleJobGraphDao scheduleJobGraphDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private AuthCenterAPIClient authCenterAPIClient;

    @EngineCron
    @Scheduled(cron = "${job.graph.cron:0 0 0 * * ? } ")
    public void handle() {
        LOGGER.info("{} do handle", this.getClass().getSimpleName());

        DateTime dateTime = DateTime.now()
                .plusDays(-1)
                .withTime(0, 0, 0, 0);
        String today = dateTime.toString(timeFormatter);
        String date = dateTime.toString(timeFormatterDay);

        List<Integer> statusList = new ArrayList<>(4);
        List<Integer> finishedList = RdosTaskStatus.getCollectionStatus(RdosTaskStatus.FINISHED.getStatus());
        List<Integer> failedList = RdosTaskStatus.getCollectionStatus(RdosTaskStatus.FAILED.getStatus());
        statusList.addAll(finishedList);
        statusList.addAll(failedList);


        List<Long> uicTenantIds = clusterTenantDao.listAllDtUicTenantId();

        for (Long uicTenantId : uicTenantIds) {
            ListQueryProParam listQueryProParam = new ListQueryProParam();
            listQueryProParam.setAppType(AppType.RDOS.getType());
            listQueryProParam.setUicTenantId(uicTenantId);

            ApiResponse<List<AuthProjectVO>> listApiResponse = authCenterAPIClient.listProByAppTypeAndTenantId(listQueryProParam);

            if (listApiResponse.success) {
                List<AuthProjectVO> data = listApiResponse.getData();

                for (AuthProjectVO projectVO : data) {
                    List<TodayJobGraph> todayJobGraphs = scheduleJobDao.listTodayJobGraph(today, statusList, EScheduleType.NORMAL_SCHEDULE.getType(),
                            projectVO.getProjectId(), uicTenantId, AppType.RDOS.getType());

                    Map<Integer, List<TodayJobGraph>> listMap = todayJobGraphs.stream().collect(Collectors.groupingBy(TodayJobGraph::getHour));

                    List<ScheduleJobGraph> scheduleJobGraphs = Lists.newArrayList();
                    for(int i = 0;i<24;i++){
                        ScheduleJobGraph scheduleJobGraph = new ScheduleJobGraph();

                        scheduleJobGraph.setDate(date);
                        scheduleJobGraph.setHour(i);
                        List<TodayJobGraph> todayJobGraphList = listMap.get(i);

                        if (CollectionUtils.isNotEmpty(todayJobGraphList)) {
                            int sum = todayJobGraphList.stream().filter(todayJobGraph -> todayJobGraph.getHour() != null && todayJobGraph.getCount() != null).mapToInt(TodayJobGraph::getCount).sum();
                            scheduleJobGraph.setCount(sum);
                        } else {
                            scheduleJobGraph.setCount(0);
                        }
                        scheduleJobGraph.setAppType(AppType.RDOS.getType());
                        scheduleJobGraph.setUicTenantId(uicTenantId);
                        scheduleJobGraph.setProjectId(projectVO.getProjectId());
                        scheduleJobGraphs.add(scheduleJobGraph);
                    }

                    scheduleJobGraphDao.batchInsert(scheduleJobGraphs);
                }
            }
        }
    }
}
