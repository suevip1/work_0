package com.dtstack.engine.master.impl;

import com.dtstack.engine.po.ScheduleJobOperate;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.OperatorParam;
import com.dtstack.engine.api.vo.ScheduleJobOperateVO;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobOperateDao;
import com.dtstack.engine.master.mapstruct.OperateStruct;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 1:42 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobOperateService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobOperateService.class);

    @Autowired
    private ScheduleJobOperateDao scheduleJobOperateDao;

    @Autowired
    private OperateStruct operateStruct;

    public Boolean addScheduleJobOperate(String jobId, Integer operateType, String operateContent, Long operateId) {
        try {
            if (operateId == null) {
                operateId = -1L;
            }
            ScheduleJobOperate scheduleJobOperate = new ScheduleJobOperate();
            scheduleJobOperate.setJobId(jobId);
            scheduleJobOperate.setOperateId(operateId);
            scheduleJobOperate.setOperateContent(operateContent);
            scheduleJobOperate.setOperateType(operateType);
            scheduleJobOperate.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            scheduleJobOperate.setGmtModified(new Timestamp(System.currentTimeMillis()));
            scheduleJobOperate.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            scheduleJobOperateDao.insert(scheduleJobOperate);
            return Boolean.TRUE;
        } catch (Exception e) {
            LOGGER.error("insert scheduleJobOperate fail:{}", e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    public void addScheduleJobOperates(List<ScheduleJobOperate> scheduleJobOperates) {
        if (CollectionUtils.isNotEmpty(scheduleJobOperates)) {
            try {
                scheduleJobOperateDao.insertBatch(scheduleJobOperates);
            } catch (Exception e) {
                LOGGER.error("batch insert scheduleJobOperate error:{}", e.getMessage(), e);
            }
        }
    }

    public PageResult<List<ScheduleJobOperateVO>> page(OperatorParam pageParam) {
        Integer count = scheduleJobOperateDao.countPage(pageParam.getJobId());

        if (count > 0) {
            List<ScheduleJobOperate> scheduleJobOperate = scheduleJobOperateDao.page(pageParam);
            List<ScheduleJobOperateVO> scheduleJobOperateVOS = operateStruct.toScheduleJobOperateVOs(scheduleJobOperate);
            return new PageResult<>(pageParam.getCurrentPage(), pageParam.getPageSize(), count, scheduleJobOperateVOS);
        }
        return new PageResult<>(pageParam.getCurrentPage(), pageParam.getPageSize(), 0, Lists.newArrayList());
    }

    public Integer count(String jobId) {
        if (StringUtils.isNotBlank(jobId)) {
            return scheduleJobOperateDao.countPage(jobId);
        }
        return 0;
    }
}
