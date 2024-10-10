package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleProjectParamDao;
import com.dtstack.engine.dao.ScheduleTaskProjectParamDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.mapstruct.ScheduleProjectParamStruct;
import com.dtstack.engine.po.ScheduleProjectParam;
import com.dtstack.engine.po.ScheduleTaskProjectParam;
import com.dtstack.schedule.common.enums.EParamType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目参数
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-05 09:34
 */
@Service
public class ScheduleProjectParamService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleProjectParamService.class);

    @Autowired
    private ScheduleProjectParamStruct projectParamStruct;

    @Autowired
    private ScheduleProjectParamDao scheduleProjectParamDao;

    @Autowired
    private ScheduleTaskProjectParamDao scheduleTaskProjectParamDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    /**
     * 保存项目参数
     * @param projectParamDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdate(ScheduleProjectParamDTO projectParamDTO) {
        this.validate(projectParamDTO);

        boolean isUpdate = (projectParamDTO.getId() != null && projectParamDTO.getId() > 0);
        ScheduleProjectParam scheduleProjectParam = projectParamStruct.toEntity(projectParamDTO);
        String paramName = projectParamDTO.getParamName();
        Long projectId = projectParamDTO.getProjectId();

        ScheduleProjectParam dbProjectParam = scheduleProjectParamDao.findByProjectAndName(projectId, paramName);
        if (isUpdate) {
            // 同一项目，参数名称需不同
            if (!dbProjectParam.getId().equals(projectParamDTO.getId())) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            scheduleProjectParamDao.modifyById(scheduleProjectParam);
        } else {
            if (dbProjectParam != null) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
            scheduleProjectParamDao.insert(scheduleProjectParam);
        }
        return scheduleProjectParam.getId();
    }

    /**
     * 删除项目参数
     * @param ids 项目参数 id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        List<Long> projectParamIds = scheduleTaskProjectParamDao.distinctExistProjectParamIds(ids);
        if (CollectionUtils.isNotEmpty(projectParamIds)) {
            List<String> projectParams = scheduleProjectParamDao.findByIds(projectParamIds).stream()
                    .map(ScheduleProjectParam::getParamName)
                    .collect(Collectors.toList());
            throw new RdosDefineException(String.format("项目参数 %s 被引用，不允许删除", projectParams));
        }
        return scheduleProjectParamDao.removeBatch(ids);
    }

    /**
     * 根据 id 查询项目参数
     * @param id
     * @return
     */
    public ScheduleProjectParamDTO findById(Long id) {
        ScheduleProjectParam scheduleProjectParam = scheduleProjectParamDao.selectByPrimaryKey(id);
        return projectParamStruct.toDTO(scheduleProjectParam);
    }

    /**
     * 分页查询项目参数
     * @param currentPage
     * @param pageSize
     * @param projectParamDTO
     * @return
     */
    public PageResult<List<ScheduleProjectParamDTO>> page(Integer currentPage, Integer pageSize, ScheduleProjectParamDTO projectParamDTO) {
        Long projectId = projectParamDTO.getProjectId();
        if (projectId == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        PageQuery query = new PageQuery(currentPage, pageSize, "gmt_create", Sort.DESC.name());
        List<ScheduleProjectParam> list = scheduleProjectParamDao.list(projectParamDTO);
        int count = scheduleProjectParamDao.count(projectParamDTO);
        return new PageResult<>(projectParamStruct.toDTOs(list), count, query);
    }

    /**
     * 查询项目下的项目参数
     * @param projectId
     * @return
     */
    public List<ScheduleProjectParamDTO> findByProjectId(Long projectId) {
        List<ScheduleProjectParam> result = scheduleProjectParamDao.findByProjectId(projectId);
        return projectParamStruct.toDTOs(result);
    }

    /**
     * 根据项目 id 查询项目参数名称
     * @param projectId
     * @return
     */
    public List<String> listParamNamesByProjectId(Long projectId) {
        return scheduleProjectParamDao.listParamNamesByProjectId(projectId);
    }

    /**
     * 根据项目 id 和项目参数名称查询项目参数
     * @param projectId
     * @param paramName
     * @return
     */
    public ScheduleProjectParamDTO findByProjectIdAndParamName(Long projectId, String paramName) {
        ScheduleProjectParam projectParam = scheduleProjectParamDao.findByProjectAndName(projectId, paramName);
        return projectParamStruct.toDTO(projectParam);
    }

    /**
     * 保存或更新任务绑定的项目参数
     * @param taskId
     * @param appType
     * @param projectParams 离线传入的项目参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTaskProjectParams(Long taskId, Integer appType, List<ScheduleTaskParamShade> projectParams) {
        if (CollectionUtils.isEmpty(projectParams)) {
            return;
        }
        ScheduleTaskShade oneTask = scheduleTaskShadeDao.getOne(taskId, appType);
        if (oneTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        Long projectId = oneTask.getProjectId();
        if (projectId == null || projectId.equals(0L)) {
            throw new RdosDefineException(ErrorCode.PROJECT_ID_CAN_NOT_NULL);
        }
        Map<String, Long> dbProjectParamNames = scheduleProjectParamDao.findByProjectId(projectId)
                .stream().collect(Collectors.toMap(ScheduleProjectParam::getParamName, ScheduleProjectParam::getId));

        List<String> projectParamNames = projectParams.stream().map(ScheduleTaskParamShade::getParamName).collect(Collectors.toList());
        if (!dbProjectParamNames.keySet().containsAll(projectParamNames)) {
            throw new RdosDefineException(String.format("含有未定义的项目参数 %s，正确的项目参数 %s, 请核实后重试", projectParamNames, dbProjectParamNames));
        }
        scheduleTaskProjectParamDao.removeByTask(taskId, appType);
        // 保存正确记录
        List<ScheduleTaskProjectParam> taskProjectParams = projectParams.stream().map(p -> {
            ScheduleTaskProjectParam tp = new ScheduleTaskProjectParam();
            tp.setTaskId(taskId);
            tp.setAppType(appType);
            tp.setProjectParamId(dbProjectParamNames.get(p.getParamName()));
            return tp;
        }).collect(Collectors.toList());
        scheduleTaskProjectParamDao.batchInsert(taskProjectParams);
    }

    /**
     * 查询任务绑定了哪些项目参数
     * @param taskId
     * @param appType
     * @return
     */
    public List<ScheduleProjectParamDTO> findTaskBindProjectParams(Long taskId, Integer appType) {
        List<ScheduleTaskProjectParam> taskBindProjectParams = scheduleTaskProjectParamDao.findTaskBindProjectParams(taskId, appType);
        if (CollectionUtils.isEmpty(taskBindProjectParams)) {
            return Collections.emptyList();
        }
        List<Long> projectParamIds = taskBindProjectParams.stream().map(ScheduleTaskProjectParam::getProjectParamId).collect(Collectors.toList());
        return projectParamStruct.toDTOs(scheduleProjectParamDao.findByIds(projectParamIds));
    }

    /**
     * 查询项目参数被哪些任务引用
     *
     * @param projectParamId
     * @return
     */
    public List<ScheduleTaskShade> findTasksByProjectParamId(Long projectParamId) {
        List<ScheduleTaskProjectParam> taskProjectParams = scheduleTaskProjectParamDao.findByProjectParamId(projectParamId);
        if (CollectionUtils.isEmpty(taskProjectParams)) {
            return Collections.emptyList();
        }
        Map<Integer, List<Long>> app2taskIds = taskProjectParams.stream().collect(
                Collectors.groupingBy(ScheduleTaskProjectParam::getAppType, Collectors.mapping(ScheduleTaskProjectParam::getTaskId, Collectors.toList()))
        );
        List<ScheduleTaskShade> result = new ArrayList<>(app2taskIds.values().size());
        app2taskIds.forEach((k, v) -> {
            List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listSimpleByTaskIds(v, Deleted.NORMAL.getStatus(), k);
            result.addAll(scheduleTaskShades);
        });
        return result;
    }

    public static ScheduleTaskParamShade transProjectParam2TaskParamShade(ScheduleProjectParamDTO projectParam) {
        if (projectParam == null) {
            return null;
        }
        ScheduleTaskParamShade taskParamShade = new ScheduleTaskParamShade();
        taskParamShade.setParamName(projectParam.getParamName());
        taskParamShade.setParamCommand(projectParam.getParamValue());
        // 从项目参数表中获取的参数，都转换为项目级参数
        taskParamShade.setType(EParamType.PROJECT.getType());
        return taskParamShade;
    }

    public static List<ScheduleTaskParamShade> transProjectParams2TaskParamShades(List<ScheduleProjectParamDTO> projectParams) {
        if (CollectionUtils.isEmpty(projectParams)) {
            return Collections.emptyList();
        }
        return projectParams.stream()
                .map(ScheduleProjectParamService::transProjectParam2TaskParamShade)
                .collect(Collectors.toList());
    }

    private void validate(ScheduleProjectParamDTO projectParamDTO) {
        String paramName = projectParamDTO.getParamName();
        String paramValue = projectParamDTO.getParamValue();
        if (StringUtils.isEmpty(paramName) || StringUtils.isEmpty(paramValue)) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }

        String paramDesc = projectParamDTO.getParamDesc();
        if (StringUtils.length(paramName) > 128
                || StringUtils.length(paramValue) > 128
                || StringUtils.length(paramDesc) > 128) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }

        Integer paramType = projectParamDTO.getParamType();
        if (EParamType.GLOBAL_PARAM_CONST.getType().equals(paramType)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }

        Long projectId = projectParamDTO.getProjectId();
        if (projectId == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
    }

    /**
     * 移除同名参数
     * @param taskId
     * @param appType
     * @param globalTaskParams
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeIdenticalProjectParam(Long taskId, Integer appType, List<ScheduleTaskParamShade> globalTaskParams) {
        // 找到没有偏移量的全局参数
        List<String> globalTaskParamsNotOffset = globalTaskParams.stream()
                .filter(p -> StringUtils.isEmpty(p.getOffset()))
                .map(ScheduleTaskParamShade::getParamName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(globalTaskParamsNotOffset)) {
            return;
        }
        scheduleTaskProjectParamDao.removeIdenticalProjectParam(taskId, appType, globalTaskParamsNotOffset);
    }
}