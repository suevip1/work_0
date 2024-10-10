package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.OmniConstructor;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.QueueParam;
import com.dtstack.engine.api.param.ResourceGroupGrantPageParam;
import com.dtstack.engine.api.param.ResourceGroupPageParam;
import com.dtstack.engine.api.param.ResourceGroupParam;
import com.dtstack.engine.api.vo.GrantedProjectVO;
import com.dtstack.engine.api.vo.ResourceGroupDropDownVO;
import com.dtstack.engine.api.vo.ResourceGroupListVO;
import com.dtstack.engine.api.vo.ReversalGrantCheckResultVO;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


@MockWith(ResourceGroupControllerTest.ResourceGroupControllerTestMock.class)
public class ResourceGroupControllerTest {

    private ResourceGroupController controller  = new ResourceGroupController();


    static class ResourceGroupControllerTestMock {

        @MockInvoke(targetClass = ResourceGroupService.class)
        public PageResult<List<ResourceGroupListVO>> pageQuery(Long clusterId, int currentPage, int pageSize) {
           return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ResourceGroupService.class)
        public void addOrUpdate(ResourceGroupParam param) {}

        @MockInvoke(targetClass = ResourceGroupService.class)
        public void delete(Long id) {}

        @MockInvoke(targetClass = ResourceGroupService.class)
        public List<String> getNames(Long clusterId, String queuePath) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ResourceGroupService.class)
        public List<ResourceGroupDropDownVO> getDropDownList(Long clusterId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ResourceGroupService.class)
        public PageResult<List<GrantedProjectVO>> pageGrantedProjects(ResourceGroupGrantPageParam param) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ResourceGroupService.class)
        public void grantToProject(Long resourceId, Long dtUicTenantId, AppType productType, Long projectId) {

        }

        @MockInvoke(targetClass = ResourceGroupService.class)
        public ReversalGrantCheckResultVO checkReversalGrant(Long resourceId, Long projectId, Integer appType) {
            return new ReversalGrantCheckResultVO();
        }


        @MockInvoke(targetClass = ResourceGroupService.class)
        public void reversalGrant(Long resourceId, Long projectId, Long handOver,Integer appType) {

        }



        }



    @Test
    public void pageQuery() {
        controller.pageQuery(OmniConstructor.newInstance(ResourceGroupPageParam.class),"",true);
    }

    @Test
    public void addOrUpdate() {
        controller.addOrUpdate(OmniConstructor.newInstance(ResourceGroupParam.class));
    }

    @Test
    public void delete() {
        controller.delete(1L);
    }

    @Test
    public void getNames() {
        controller.getNames(OmniConstructor.newInstance(QueueParam.class));
    }

    @Test
    public void getDropDownList() {
        controller.getDropDownList(1L);
    }

    @Test
    public void pageGrantedProjects() {
        controller.pageGrantedProjects(OmniConstructor.newInstance(ResourceGroupGrantPageParam.class));
    }

    @Test
    public void grantToProject() {
        controller.grantToProject(1L,1L,"RDOS", Lists.newArrayList(1L),"",true);
    }

    @Test
    public void checkReversalGrant() {
        controller.checkReversalGrant(1L, 1L, 1, null, "", true);
    }

    @Test
    public void reversalGrant() {
        controller.reversalGrant(1L,1L,1L,1, null);
    }
}