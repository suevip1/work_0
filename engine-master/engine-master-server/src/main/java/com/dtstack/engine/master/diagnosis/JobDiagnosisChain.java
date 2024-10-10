package com.dtstack.engine.master.diagnosis;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.JsonUtils;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.diagnosis.JobDiagnosisInformationVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisEnum;
import com.dtstack.engine.master.diagnosis.enums.DiagnosisResultEnum;
import com.dtstack.engine.master.diagnosis.filter.CycTimeJobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.JobSubmitDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.ParentDependJobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.ResourceMathDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.RunningJobDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.StatusDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.TenantResourceDiagnosisFilter;
import com.dtstack.engine.master.diagnosis.filter.ValidJobDiagnosisFilter;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.mapstruct.DiagnosisStruct;
import com.dtstack.engine.po.ScheduleJobGanttChart;
import com.dtstack.engine.po.ScheduleJobExpand;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/7/14
 */
@Service
public class JobDiagnosisChain implements ApplicationContextAware {

    @Autowired
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    @Autowired
    private DiagnosisStruct diagnosisStruct;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    private List<JobDiagnosisFilter> filters;

    public List<JobDiagnosisInformationVO> diagnosisByRunNum(String jobId, Integer runNum) {
        if (Objects.isNull(runNum)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, null);
        if (null == scheduleJob) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        // 如果不是最近一次运行, 需要从数据库中获取
        ScheduleJobExpand jobExpand = scheduleJobExpandDao.getExpandByJobId(jobId);
        if (!runNum.equals(jobExpand.getRunNum())) {
            // 需要从数据库中获取
            String jobDiagnosisInfo = scheduleJobExpandDao.getJobDiagnosisInfo(jobId, runNum);
            if (StringUtils.isBlank(jobDiagnosisInfo)) {
                return Collections.emptyList();
            }
            try {
                return JSONObject.parseArray(jobDiagnosisInfo, JobDiagnosisInformationVO.class);
            } catch (Exception e) {
                throw new DtCenterDefException("get job diagnosisInfo error", e);
            }
        }
        return diagnosis(scheduleJob);
    }

    public List<JobDiagnosisInformationVO> diagnosis(String jobId) {
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId, null);
        if (null == scheduleJob) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        return diagnosis(scheduleJob);
    }

    /**
     * @param job
     * @return
     * @see CycTimeJobDiagnosisFilter
     * @see TenantResourceDiagnosisFilter
     * @see StatusDiagnosisFilter
     * @see ParentDependJobDiagnosisFilter
     * @see JobSubmitDiagnosisFilter
     * @see ResourceMathDiagnosisFilter
     * @see RunningJobDiagnosisFilter
     * @see ValidJobDiagnosisFilter
     */
    public List<JobDiagnosisInformationVO> diagnosis(ScheduleJob job) {
        List<JobDiagnosisInformation> informations = new ArrayList<>(filters.size());
        // 上一个流程的检查结果
        DiagnosisResultEnum preResult = null;
        for (JobDiagnosisFilter filter : filters) {
            JobDiagnosisInformation diagnosisInformation = filter.diagnosis(job, preResult);
            if (null == diagnosisInformation) {
                continue;
            }
            if (DiagnosisResultEnum.NOT_CONTAINS.getVal().equals(diagnosisInformation.getDiagnosisResult())) {
                continue;
            }
            preResult = DiagnosisResultEnum.getByVal(diagnosisInformation.getDiagnosisResult());
            diagnosisInformation.setDiagnosisType(filter.order().getVal());
            informations.add(diagnosisInformation);
        }
        ScheduleJobGanttChart jobGanttChart = scheduleJobGanttTimeService.select(job.getJobId());
        return informations.stream().map(jobDiagnosisInformation -> {
            JobDiagnosisInformationVO vo = diagnosisStruct.toInformationVO(jobDiagnosisInformation);
            if (null != jobGanttChart) {
                Date ganttTime = getGanttTime(vo.getDiagnosisType(), jobGanttChart);
                if (null != ganttTime) {
                    vo.setDiagnosisTime(Timestamp.from(ganttTime.toInstant()));
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, JobDiagnosisFilter> beansOfRunner = applicationContext.getBeansOfType(JobDiagnosisFilter.class);
        filters = beansOfRunner.values()
                .stream()
                .sorted(Comparator.comparing(JobDiagnosisFilter::order))
                .collect(Collectors.toList());
    }

    public Date getGanttTime(Integer type, ScheduleJobGanttChart jobGanttChart) {
        DiagnosisEnum diagnosisEnum = DiagnosisEnum.getByVal(type);
        if (null == diagnosisEnum) {
            return null;
        }
        switch (diagnosisEnum) {
            case CYC_TIME_JOB_DIAGNOSIS:
                return jobGanttChart.getCycTime();
            case STATUS_DIAGNOSIS:
                return jobGanttChart.getStatusTime();
            case PARENT_DEPEND_JOB_DIAGNOSIS:
                return jobGanttChart.getParentDependTime();
            case TENANT_RESOURCE_DIAGNOSIS:
                return jobGanttChart.getTenantResourceTime();
            case JOB_SUBMIT_DIAGNOSIS:
                return jobGanttChart.getJobSubmitTime();
            case RESOURCE_MATH_DIAGNOSIS:
                return jobGanttChart.getResourceMatchTime();
            case VALID_JOB_DIAGNOSIS:
                return jobGanttChart.getValidJobTime();
            case RUNNING_JOB_DIAGNOSIS:
                return jobGanttChart.getRunJobTime();
        }
        return null;
    }
}
