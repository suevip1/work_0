package com.dtstack.engine.master.bo;

import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:49 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataInfoBO {

    private List<FillDataChooseProjectVO> projectList;

    private List<FillDataChooseTaskVO> taskChooseList;

    private FillDataChooseTaskVO rootTaskId;

    private List<FillDataChooseTaskVO> whiteList;

    private List<FillDataChooseTaskVO> blackList;

    private Long dtuicTenantId;


    public FillDataInfoBO(List<FillDataChooseProjectVO> projectList,
                          List<FillDataChooseTaskVO> taskChooseList,
                          FillDataChooseTaskVO rootTaskId,
                          List<FillDataChooseTaskVO> whiteList,
                          List<FillDataChooseTaskVO> blackList,
                          Long dtuicTenantId) {
        this.projectList = projectList;
        this.taskChooseList = taskChooseList;
        this.whiteList = whiteList;
        this.blackList = blackList;
        this.rootTaskId = rootTaskId;
        this.dtuicTenantId = dtuicTenantId;
    }

    public List<FillDataChooseProjectVO> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<FillDataChooseProjectVO> projectList) {
        this.projectList = projectList;
    }

    public List<FillDataChooseTaskVO> getTaskChooseList() {
        return taskChooseList;
    }

    public void setTaskChooseList(List<FillDataChooseTaskVO> taskChooseList) {
        this.taskChooseList = taskChooseList;
    }

    public List<FillDataChooseTaskVO> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<FillDataChooseTaskVO> whiteList) {
        this.whiteList = whiteList;
    }

    public List<FillDataChooseTaskVO> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<FillDataChooseTaskVO> blackList) {
        this.blackList = blackList;
    }

    public FillDataChooseTaskVO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskVO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }
}
