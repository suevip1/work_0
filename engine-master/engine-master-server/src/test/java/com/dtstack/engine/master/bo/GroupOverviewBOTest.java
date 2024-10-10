package com.dtstack.engine.master.bo;

import org.junit.Test;

import static org.junit.Assert.*;

public class GroupOverviewBOTest {

    private GroupOverviewBO bo = new GroupOverviewBO();

    @Test
    public void getJobResource() {
        bo.getJobResource();
        bo.setJobResource("");
        bo.getGroupOverviewDTOs();
        bo.setGroupOverviewDTOs(null);
    }
}