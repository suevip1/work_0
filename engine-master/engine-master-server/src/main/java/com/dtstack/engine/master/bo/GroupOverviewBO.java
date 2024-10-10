package com.dtstack.engine.master.bo;

import com.dtstack.engine.dto.GroupOverviewDTO;

import java.util.List;

/**
 * @author leon
 * @date 2022-09-09 16:44
 **/
public class GroupOverviewBO {

    String jobResource;

    List<GroupOverviewDTO> groupOverviewDTOs;

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public List<GroupOverviewDTO> getGroupOverviewDTOs() {
        return groupOverviewDTOs;
    }

    public void setGroupOverviewDTOs(List<GroupOverviewDTO> groupOverviewDTOs) {
        this.groupOverviewDTOs = groupOverviewDTOs;
    }


}
