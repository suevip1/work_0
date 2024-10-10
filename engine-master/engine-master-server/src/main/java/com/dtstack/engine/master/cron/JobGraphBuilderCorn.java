package com.dtstack.engine.master.cron;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.handler.AbstractMasterHandler;
import com.dtstack.engine.master.impl.JobGraphTriggerService;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobGraphBuilderTrigger;
import com.dtstack.engine.master.vo.AlertConfigVO;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/8/29 2:39 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobGraphBuilderCorn {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphBuilderCorn.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Autowired
    private ScheduleDictDao dictDao;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @EngineCron
    @Scheduled(cron = "${job.graph.builder:0 0/20 * * * ? }")
    public void handle() {
        LOGGER.info("{} do handle", this.getClass().getSimpleName());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            SimpleDateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date triggerDate = dateFormat.parse(dayFormat.format(new Date()) + " " + environmentContext.getJobGraphBuildCron());

            DateTime dateTime = new DateTime(triggerDate);

            String triggerDay = "";
            if (dateTime.plusHours(environmentContext.getGraphAlertHours()).isAfterNow()) {
                triggerDay = dateTime.toString("yyyy-MM-dd");
            } else {
                triggerDay = dateTime.plusDays(1).toString("yyyy-MM-dd");
            }

            String triggerTimeStr = triggerDay + " 00:00:00";
            Timestamp triggerTime = Timestamp.valueOf(triggerTimeStr);

            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);

            if (hasBuild) {
                return;
            }

            // 需要告警
            List<ScheduleDict> dicts = dictDao.listDictByType(DictType.ALERT_CONFIG.type);

            if (!CollectionUtils.isEmpty(dicts)) {
                AlertConfigVO vo = JSONObject.parseObject(dicts.get(0).getDictValue(), AlertConfigVO.class);
                if (vo.getEnabled() != null && vo.getEnabled()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    jobGraphBuilder.exceptionNotify(vo, format.format(new Date()) + "调度系统周期实例生成异常，请及时处理！");
                }
            }
        } catch (Exception e) {
            LOGGER.error("",e);
        }
    }
}
