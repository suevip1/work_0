package com.dtstack.engine.master.config;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleTaskBlackDao;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.multiengine.UnnecessaryPreprocessJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/26
 */
@Configuration
public class MasterServerBeanConfig {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ScheduleTaskBlackDao scheduleTaskBlackDao;

    @Bean
    public SftpFileManage sftpFileManage() {
        return SftpFileManage.getInstance();
    }

    @PostConstruct
    public void init() {
        UnnecessaryPreprocessJobService.init(scheduleJobDao, scheduleJobExpandDao, scheduleJobGanttTimeService,taskParamsService,environmentContext,scheduleTaskBlackDao);
    }


}
