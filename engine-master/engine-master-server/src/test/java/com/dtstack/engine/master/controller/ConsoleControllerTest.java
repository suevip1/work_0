package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.vo.AlertConfigVO;
import com.dtstack.engine.master.vo.GroupOverviewVO;
import com.dtstack.engine.master.vo.JobGenerationVO;
import com.dtstack.engine.master.vo.ProductNameVO;
import com.dtstack.engine.master.vo.StopJobResult;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.vo.UserNameVO;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MockWith(ConsoleControllerTest.Mock.class)
public class ConsoleControllerTest {
    
    private ConsoleController controller = new ConsoleController();
    
    public static class Mock  {
        @MockInvoke(targetClass=ConsoleService.class)
        public List<String> nodeAddress() {
           return new ArrayList<>();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public ConsoleJobVO searchJob(String jobName) {
            return new ConsoleJobVO();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<String> listNames(String jobName) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<String> jobResources() {
            return new ArrayList<>();
        }


        @MockInvoke(targetClass=ConsoleService.class)
        public List<GroupOverviewVO> overview(String nodeAddress, String clusterName, Long tenantId) {
            return new ArrayList<>();
        }

            @MockInvoke(targetClass=ConsoleService.class)
        public PageResult groupDetail(String jobResource, String nodeAddress, Integer stage, Integer pageSize, Integer currentPage, String dtToken, Long currentLoginTenantId) {
           return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public Boolean jobStick(String jobId) {
            return true;
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public void stopJob(String jobId, Integer isForce) {

        }

        @MockInvoke(targetClass=ConsoleService.class)
        public void stopJob(String jobId) throws Exception {

        }


        @MockInvoke(targetClass=ConsoleService.class)
        public StopJobResult stopAll(String jobResource,
                                     String nodeAddress,
                                     UserDTO user) throws Exception {

            return new StopJobResult();

        }

        @MockInvoke(targetClass=ConsoleService.class)
        public void stopJobList(String jobResource, String nodeAddress, Integer stage, List<String> jobIdList, Integer isForce) {

        }

        @MockInvoke(targetClass=ConsoleService.class)
        public StopJobResult stopJobList( String jobResource,
                                          String nodeAddress,
                                          Integer stage,
                                          List<String> jobIdList,
                                          UserDTO user) throws Exception {
            return new StopJobResult();
        }




        @MockInvoke(targetClass=ConsoleService.class)
        public ClusterResource clusterResources(String clusterName, Map<Integer, String> componentVersionMap, Long dtuicTenantId) {
           return new ClusterResource();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<TaskTypeResourceTemplateVO> getTaskResourceTemplate() {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<JobGenerationVO> listJobGenerationRecord(Integer status) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<UserNameVO> getUser(Long dtUicTenantId, List<Long> userIds) {
            return new ArrayList<>();        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<UserNameVO> getUser(String userName, List<Long> userIds) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public AlertConfigVO getAlertConfig() {
            return new AlertConfigVO();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public AlertConfigVO saveAlertConfig(AlertConfigVO vo) {
            return new AlertConfigVO();
        }

        @MockInvoke(targetClass=ConsoleService.class)
        public List<ProductNameVO> getProducts() {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass= SessionUtil.class)
        public <T> T getUser(String token, Class<T> clazz) {
            return (T)new UserDTO();
        }

        @MockInvoke(targetClass = EnvironmentContext.class)
        public boolean getOpenAdminKillPermission() {
            return true;
        }

        }

    @Test
    public void nodeAddress() {
        controller.nodeAddress();
    }

    @Test
    public void searchJob() {
        controller.searchJob("");
    }

    @Test
    public void listNames() {
        controller.listNames("");
    }

    @Test
    public void jobResources() {
        controller.jobResources();
    }

    @Test
    public void overview() {
        controller.overview("","",1L,null,false);
    }

    @Test
    public void groupDetail() {
        controller.groupDetail("sds","127.0.0.1",1,1,1,"","",1L,true);
    }

    @Test
    public void jobStick() {
        controller.jobStick("ds");
    }

    @Test
    public void stopJob() throws Exception {
        controller.stopJob("sdsd","",true);
    }

    @Test
    public void stopAll() throws Exception {
        controller.stopAll("sd","127.0.0.1","1L",true);
    }

    @Test
    public void stopJobList() throws Exception {
        controller.stopJobList("dsd","127.0.0.1",new ArrayList<>(),"",true);
    }

    @Test
    public void clusterResources() {
        controller.clusterResources("default",1L,"",true);
    }

    @Test
    public void getTaskResourceTemplate() {
        controller.getTaskResourceTemplate(1L);
    }

    @Test
    public void listJobGenerationRecord() {
        controller.listJobGenerationRecord(1);
    }

    @Test
    public void getAlertConfig() {
        controller.getAlertConfig();
    }

    @Test
    public void saveAlertConfig() {
        controller.saveAlertConfig(new AlertConfigVO());
    }


    @Test
    public void getReceivers() {
        controller.getReceivers(1L,"", Lists.newArrayList());
    }

    @Test
    public void getUsers() {
        controller.getUsers(1L,"",Lists.newArrayList());
    }

    @Test
    public void getProducts() {
        controller.getProducts();
    }
}