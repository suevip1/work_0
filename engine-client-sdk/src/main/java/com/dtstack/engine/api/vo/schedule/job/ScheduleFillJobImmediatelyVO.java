package com.dtstack.engine.api.vo.schedule.job;

import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.task.TaskCustomParamVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023-10-10 11:45
 * @Email: dazhi@dtstack.com
 * @Description: ScheduleFillJobImmediatelyVO
 */
public class ScheduleFillJobImmediatelyVO {


    /**
     * 触发补数据事件的所在工程id
     * 必填
     */
    @NotNull(message = "projectId is not null")
    private Long projectId;

    /**
     * 触发补数据事件的用户uicId
     * 必填
     */
    @NotNull(message = "userId is not null")
    private Long userId;

    /**
     * 触发补数据事件的应用类型
     * 必填
     */
    @NotNull(message = "appType is not null")
    private Integer appType;

    /**
     * uic租户id
     * 必填
     */
    @NotNull(message = "dtuicTenantId is not null")
    private Long dtuicTenantId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 任务 Id
     */
    private Long taskId;


    private List<TaskCustomParamVO> taskCustomParamVOList;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<TaskCustomParamVO> getTaskCustomParamVOList() {
        return taskCustomParamVOList;
    }

    public void setTaskCustomParamVOList(List<TaskCustomParamVO> taskCustomParamVOList) {
        this.taskCustomParamVOList = taskCustomParamVOList;
    }
}
