package com.dtstack.engine.master.component;

import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ConsoleJobRestrictDao;
import com.dtstack.engine.master.impl.ConsoleJobRestrictService;
import com.dtstack.engine.po.ConsoleJobRestrict;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class ConsoleJobRestrictDelayMsgQueue extends BaseDelayQueue<Long, Boolean> {
    private static final String DELAY_QUEUE_CONSOLE_JOB_RESTRICT = "delay:queue:consoleJobRestrict";

    @Autowired
    private ConsoleJobRestrictDao consoleJobRestrictDao;

    @Override
    protected String queueName() {
        return DELAY_QUEUE_CONSOLE_JOB_RESTRICT;
    }

    @Override
    protected Boolean onMessage(Long jobRestrictId) throws Exception {
        log.info("consume on message {} start，id:{}", queueName(), jobRestrictId);
        Date now = ConsoleJobRestrictService.stripMillSec(new Date());
        ConsoleJobRestrict jobRestrict = consoleJobRestrictDao.selectById(jobRestrictId);
        if (jobRestrict == null) {
            log.info("consume on message {} over，record not exist，id：{}", queueName(), jobRestrictId);
            return false;
        }
        Boolean result = consoleJobRestrictDao.changeOverEndTimeStatusByIdsAndNow(Lists.newArrayList(jobRestrict.getId()), now) > 0;

        log.info("consume ok, queue:{}，id:{}, expect:{}，actual:{}，delay:{} s，result:{}",
                queueName(),
                jobRestrictId,
                DateUtil.getDate(jobRestrict.getRestrictEndTime(), DateUtil.STANDARD_DATETIME_FORMAT),
                DateUtil.getDate(now, DateUtil.STANDARD_DATETIME_FORMAT),
                Duration.between(LocalDateTime.ofInstant(jobRestrict.getRestrictEndTime().toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault())).getSeconds(),
                result);
        return result;
    }
}