package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ConsoleJobRestrictDao;
import com.dtstack.engine.dto.ConsoleJobRestrictQueryDTO;
import com.dtstack.engine.master.component.ConsoleJobRestrictDelayMsgQueue;
import com.dtstack.engine.master.component.DtRedissonLock;
import com.dtstack.engine.master.enums.JobRestrictStatusEnum;
import com.dtstack.engine.master.mapstruct.ConsoleJobRestrictStruct;
import com.dtstack.engine.master.vo.ConsoleJobRestrictVO;
import com.dtstack.engine.po.ConsoleJobRestrict;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 实例限制提交配置
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:17
 */
@Service
public class ConsoleJobRestrictService implements InitializingBean, ApplicationListener<ApplicationStartedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsoleJobRestrictService.class);

    public static final String RESTRICT_TIP = "当前时间段限制提交, 命中规则:%s, 时间段:(%s, %s)";

    /**
     * 分布式锁 Key
     */
    private final static String CONSOLE_JOB_RESTRICT_LOCK = "CONSOLE_JOB_RESTRICT_LOCK";

    @Autowired
    private ConsoleJobRestrictDao consoleJobRestrictDao;

    @Autowired
    private ConsoleJobRestrictStruct consoleJobRestrictStruct;

    @Autowired
    private DtRedissonLock dtRedissonLock;

    @Autowired
    private ConsoleJobRestrictDelayMsgQueue consoleJobRestrictDelayMsgQueue;

    /**
     * 默认每隔 5 分钟扫描一次过期任务
     */
    @Value("${console.job.restrict.outdate.compensate.in.minute:5}")
    private Integer jobRestrictOutdateCompensateInMinute;

    /**
     * 默认每隔 2 小时扫描一次过期任务
     */
    @Value("${console.job.restrict.outdate.delay.queue.compensate.in.hour:2}")
    private Integer jobRestrictOutdateDelayQueueCompensateInMinute;

    private ScheduledExecutorService compensateSchedulerForJVM;

    private ScheduledExecutorService compensateSchedulerForRedisQueue;

    @Autowired
    private UserService userService;

    /**
     * 新增管控规则，内部使用分布式锁，保证同时只有一条规则生效
     *
     * @param restrictQueryDTO
     * @return
     */
    public Long add(ConsoleJobRestrictQueryDTO restrictQueryDTO) {
        return dtRedissonLock.execWithLock(CONSOLE_JOB_RESTRICT_LOCK, 10, 10, TimeUnit.SECONDS,
                restrictQueryDTO,
                queryDTO -> {
                    closeWaitOrRunningRestricts();
                    ConsoleJobRestrict jobRestrict = new ConsoleJobRestrict();
                    jobRestrict.setRestrictStartTime(DateUtil.strictParseDate(queryDTO.getRestrictStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                    jobRestrict.setRestrictEndTime(DateUtil.strictParseDate(queryDTO.getRestrictEndTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                    jobRestrict.setStatus(JobRestrictStatusEnum.WAIT.getStatus());
                    jobRestrict.setCreateUserId(queryDTO.getCreateUserId());
                    // 新增规则
                    consoleJobRestrictDao.add(jobRestrict);

                    addIntoDelayQueue(jobRestrict.getId(), Duration.ofMillis(jobRestrict.getRestrictEndTime().getTime() - (stripMillSec(new Date())).getTime()));
                    return jobRestrict.getId();
                }, e -> {
                    LOGGER.error("addRestrict error: {}", restrictQueryDTO, e);
                    throw new RdosDefineException("新增管控规则失败，请稍后重试", ExceptionUtil.getErrorMessage(e));
                }, () -> {
                    throw new RdosDefineException("其他人正在新增管控规则, 请稍后重试");
                }, true);
    }

    /**
     * 分页查询管控规则
     *
     * @param jobRestrictQueryDTO
     * @return
     */
    public PageResult<List<ConsoleJobRestrictVO>> pageQuery(ConsoleJobRestrictQueryDTO jobRestrictQueryDTO) {
        PageQuery<ConsoleJobRestrictQueryDTO> pageQuery = new PageQuery<>(jobRestrictQueryDTO.getCurrentPage(), jobRestrictQueryDTO.getPageSize(),
                "gmt_create", Sort.DESC.name());
        int count = consoleJobRestrictDao.generalCount(jobRestrictQueryDTO);
        List<ConsoleJobRestrictVO> result = Collections.emptyList();
        if (count > 0) {
            pageQuery.setModel(jobRestrictQueryDTO);
            List<ConsoleJobRestrict> consoleJobRestrictList = consoleJobRestrictDao.generalQuery(pageQuery);
            result = consoleJobRestrictStruct.toVOList(consoleJobRestrictList);
            try {
                this.fillUserName(result);
            } catch (Exception e) {
                LOGGER.error("fillUserName error", e);
            }
        }
        return new PageResult<>(result, count, pageQuery);
    }

    /**
     * 关闭规则: 关闭没到「计划结束时间」的等待执行、执行中的规则
     *
     * @param id 规则 id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean close(Long id) {
        Date now = stripMillSec(new Date());
        boolean close = consoleJobRestrictDao.close(id, now);
        if (!close) {
            throw new RdosDefineException("关闭失败, id:" + id);
        }
        return close;
    }

    /**
     * 删除规则: 删除「无效」和没有实际生效的「关闭」状态的规则
     *
     * @param id 规则 id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long id) {
        boolean remove = consoleJobRestrictDao.remove(id);
        if (!remove) {
            throw new RdosDefineException("删除失败, id:" + id);
        }
        return remove;
    }

    /**
     * 开启规则，内部使用分布式锁，保证跟新增管控规则互斥，只有一条规则生效
     *
     * @param id 规则 id
     * @return
     */
    public boolean open(Long id) {
        return dtRedissonLock.execWithLock(CONSOLE_JOB_RESTRICT_LOCK, 10, 10, TimeUnit.SECONDS,
                id,
                restrictId -> {
                    closeWaitOrRunningRestricts();
                    Boolean open = consoleJobRestrictDao.open(restrictId, stripMillSec(new Date()));
                    if (!Boolean.TRUE.equals(open)) {
                        throw new RdosDefineException("开启管控规则失败，id:" + restrictId);
                    }
                    return open;
                }, e -> {
                    LOGGER.error("open error: {}", id, e);
                    throw new RdosDefineException("开启管控规则失败，请稍后重试，id:" + id, ExceptionUtil.getErrorMessage(e));
                }, () -> {
                    throw new RdosDefineException("其他人正在新增或开启管控规则, 请稍后重试");
                }, true);
    }

    /**
     * 查询单个规则
     *
     * @param id
     * @return
     */
    public ConsoleJobRestrict getById(Long id) {
        return consoleJobRestrictDao.selectById(id);
    }

    /**
     * 是否存在等待或运行中的规则
     * @return true 是
     */
    public Boolean existWaitOrRunning() {
        return BooleanUtils.toBoolean(consoleJobRestrictDao.existWaitOrRunning());
    }

    /**
     * 加入延迟队列，用于过期处理
     *
     * @param jobRestrictId
     * @param duration
     */
    public void addIntoDelayQueue(Long jobRestrictId, Duration duration) {
        try {
            consoleJobRestrictDelayMsgQueue.add(jobRestrictId, duration);
            LOGGER.info("addIntoDelayQueue ok, jobRestrictId:{}, duration:{}", jobRestrictId, duration.getSeconds());
        } catch (Exception e) {
            LOGGER.error("addIntoDelayQueue error, jobRestrictId:{}, duration:{}", jobRestrictId, duration.getSeconds(), e);
            throw e;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        compensateSchedulerForJVM = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ConsoleJobRestrictCompensateSchedulerForJVM"));
        compensateSchedulerForRedisQueue = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("ConsoleJobRestrictCompensateSchedulerForRedisQueue"));
    }

    /**
     * 系统启动时，触发消费
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            // 系统启动时，触发消费
            consoleJobRestrictDelayMsgQueue.triggerDelayedQueueConsumer();
            LOGGER.info("triggerDelayedQueueConsumer ok");
        } catch (Exception e) {
            LOGGER.error("triggerDelayedQueueConsumer error", e);
        }

        // 用于补偿过期机制的定时器
        compensateSchedulerForJVM.scheduleWithFixedDelay(
                this::changeOverEndTimeStatus,
                0,
                jobRestrictOutdateCompensateInMinute,
                TimeUnit.MINUTES);

        // 防止 redis 宕机丢失数据或者数据被异常清理的补偿定时器
        // 系统重启时也会捞一把过期的数据，防止 redis 的数据被人为清理
        compensateSchedulerForRedisQueue.scheduleWithFixedDelay(
                this::addBatchIntoDelayQueue,
                0,
                jobRestrictOutdateDelayQueueCompensateInMinute,
                TimeUnit.HOURS);
    }

    /**
     * 定时器补偿：默认每隔 5 分钟扫描一次过期规则
     */
    public void changeOverEndTimeStatus() {
        Date now = stripMillSec(new Date());
        List<ConsoleJobRestrict> allOverEndTimeRecords = consoleJobRestrictDao.listAllOverEndTimeRecords(now);
        if (CollectionUtils.isEmpty(allOverEndTimeRecords)) {
            return;
        }
        consoleJobRestrictDao.changeOverEndTimeStatusByIds(allOverEndTimeRecords.stream().map(ConsoleJobRestrict::getId).collect(Collectors.toList()));
    }

    /**
     * 定时器补偿：默认每隔 2 小时扫描一次，将要超期但未超期的规则放入延时队列
     */
    public void addBatchIntoDelayQueue() {
        Date now = stripMillSec(new Date());
        List<ConsoleJobRestrict> toOverEndTimeRecords = consoleJobRestrictDao.listToOverEndTimeRecords(now);
        if (CollectionUtils.isEmpty(toOverEndTimeRecords)) {
            return;
        }
        long nowMillTime = now.getTime();
        for (ConsoleJobRestrict toOverEndTimeRecord : toOverEndTimeRecords) {
            try {
                if (consoleJobRestrictDelayMsgQueue.contains(toOverEndTimeRecord.getId())) {
                    continue;
                }
                addIntoDelayQueue(toOverEndTimeRecord.getId(), Duration.ofMillis(toOverEndTimeRecord.getRestrictEndTime().getTime() - nowMillTime));
            } catch (Exception e) {
                LOGGER.error("addBatchIntoDelayQueue error, currentId:{}", toOverEndTimeRecord.getId(), e);
            }
        }
    }

    /**
     * 距离当前最近的开启的规则
     *
     * @return
     */
    public ConsoleJobRestrict findLatestWaitAndRunRestrict() {
        Date now = stripMillSec(new Date());
        return consoleJobRestrictDao.findLatestWaitAndRunRestrict(now);
    }

    /**
     * 更新生效时间
     *
     * @return
     */
    public boolean updateEffectiveTime(Long id) {
        Date now = new Date();
        return consoleJobRestrictDao.updateEffectiveTime(id, now);
    }

    public static String generateRestrictTip(ConsoleJobRestrict consoleJobRestrict) {
        if (consoleJobRestrict == null) {
            return RESTRICT_TIP;
        }
        return String.format(RESTRICT_TIP, consoleJobRestrict.getId(),
                DateUtil.getDate(consoleJobRestrict.getRestrictStartTime(), DateUtil.STANDARD_DATETIME_FORMAT),
                DateUtil.getDate(consoleJobRestrict.getRestrictEndTime(), DateUtil.STANDARD_DATETIME_FORMAT)
        );
    }

    /**
     * 将「等待执行」或者「执行中」的规则关闭
     */
    private void closeWaitOrRunningRestricts() {
        List<ConsoleJobRestrict> waitOrRunRestricts = consoleJobRestrictDao.listByStatus(Lists.newArrayList(JobRestrictStatusEnum.WAIT.getStatus(), JobRestrictStatusEnum.RUNNING.getStatus()));
        if (CollectionUtils.isNotEmpty(waitOrRunRestricts)) {
            consoleJobRestrictDao.changeStatus(waitOrRunRestricts.stream().map(ConsoleJobRestrict::getId).collect(Collectors.toList()),
                    JobRestrictStatusEnum.CLOSED.getStatus(),
                    Lists.newArrayList(JobRestrictStatusEnum.WAIT.getStatus(), JobRestrictStatusEnum.RUNNING.getStatus()));
        }
    }

    /**
     * 填充用户名
     *
     * @param result
     */
    private void fillUserName(List<ConsoleJobRestrictVO> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        Set<Long> createUserIds = result.stream()
                .map(ConsoleJobRestrictVO::getCreateUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(createUserIds)) {
            return;
        }
        Map<Long, User> userMapping = userService.findUserWithFill(createUserIds).stream()
                .collect(Collectors.groupingBy(User::getDtuicUserId,
                        Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));
        if (userMapping == null || userMapping.isEmpty()) {
            return;
        }
        Long createUserId;
        for (ConsoleJobRestrictVO vo : result) {
            createUserId = vo.getCreateUserId();
            if (Objects.isNull(createUserId)) {
                continue;
            }
            User user = userMapping.get(createUserId);
            if (Objects.isNull(user)) {
                continue;
            }
            vo.setCreateUserName(user.getUserName());
        }
    }

    /**
     * 消除毫秒带来的影响，尽可能让消费时间精确
     * @param date
     * @return
     */
    public static Date stripMillSec(Date date) {
        if (date == null) {
            return null;
        }
        String standardDateStr = DateUtil.getDate(date, DateUtil.STANDARD_DATETIME_FORMAT);
        return DateUtil.parseDate(standardDateStr, DateUtil.STANDARD_DATETIME_FORMAT);
    }
}