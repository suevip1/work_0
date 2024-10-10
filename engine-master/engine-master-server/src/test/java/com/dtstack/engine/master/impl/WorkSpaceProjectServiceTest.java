package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.vo.project.NotDeleteProjectVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.master.mockcontainer.impl.MockSpaceProjectServiceMock;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.google.common.collect.Table;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@MockWith(MockSpaceProjectServiceMock.class)
public class WorkSpaceProjectServiceTest {

    WorkSpaceProjectService workSpaceProjectService = new WorkSpaceProjectService();

    @Test
    public void testUpdateSchedule() throws Exception {
        workSpaceProjectService.updateSchedule(1L, 0, 0);
    }

    @Test
    public void testListProjects() throws Exception {
        List<ProjectNameVO> result = workSpaceProjectService.listProjects(AppType.RDOS, 1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFinProject() throws Exception {
        AuthProjectVO result = workSpaceProjectService.finProject(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetNotDeleteTaskByProjectId() throws Exception {
        List<NotDeleteProjectVO> result = workSpaceProjectService.getNotDeleteTaskByProjectId(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindProjects() throws Exception {
        List<AuthProjectVO> result = workSpaceProjectService.findProjects(0, Collections.singletonList(1L));
        Assert.assertNotNull(result);
    }

    @Test
    public void testFullProjectName() throws Exception {
        List<AuthProjectVO> result = workSpaceProjectService.fullProjectName(1L, "projectName", 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetProjectGroupAppType() throws Exception {
        Table<Integer, Long, AuthProjectVO> result = workSpaceProjectService.getProjectGroupAppType(new HashMap<Integer, List<Long>>() {{
            put(0, Arrays.asList(1L));
        }});
        Assert.assertNotNull(result);
    }
}