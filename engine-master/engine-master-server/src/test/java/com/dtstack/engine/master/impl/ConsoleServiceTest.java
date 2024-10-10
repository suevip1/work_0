package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.master.mockcontainer.impl.ConsoleServiceMock;
import com.dtstack.engine.master.vo.AlertConfigVO;
import com.dtstack.engine.master.vo.JobGenerationVO;
import com.dtstack.engine.master.vo.ProductNameVO;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.vo.UserNameVO;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-05-26 16:53
 */
@MockWith(value = ConsoleServiceMock.class)
@EnablePrivateAccess(srcClass = ConsoleService.class)
public class ConsoleServiceTest {
    ConsoleService consoleService = new ConsoleService();

    /**
     * 测试存活的 brokers
     *
     * @throws Exception
     */
    @Test
    public void testNodeAddress() {
        Assert.assertEquals(1, consoleService.nodeAddress().size());
    }

    @Test
    public void testSearchJob() {
        Assert.assertNotNull(consoleService.searchJob("jobName"));
        try {
            consoleService.searchJob(null);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testListNames() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(consoleService.listNames("jobName")));
    }

    @Test
    public void testJobResources() {
        List<String> result = consoleService.jobResources();
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));
    }

//    @Test
//    public void testOverview() {
//        Collection<Map<String, Object>> result = consoleService.overview("127.0.0.1", "clusterName", -1L);
//        Assert.assertTrue(CollectionUtils.isNotEmpty(result));
//    }

    @Test
    public void testGroupDetail() {
        PageResult pr = consoleService.groupDetail("j1", "127.0.0.1", EJobCacheStage.PRIORITY.getStage(),
                1, 5, null, "task",-1L,null);
        Assert.assertNotNull(pr);

        Object data = pr.getData();
        Assert.assertNotNull(data);
    }

    @Test
    public void testJobStick() {
        Boolean stick = consoleService.jobStick("jobId");
        Assert.assertTrue(stick);
    }

    @Test
    public void testStopJob() throws Exception {
        consoleService.stopJob("job1");
        consoleService.stopJob("job1", ForceCancelFlag.YES.getFlag());
    }

    @Test
    public void testStopAll() throws Exception {
        UserDTO userDTO = new UserDTO();
        consoleService.stopAll("jobResource", "127.0.0.1", userDTO);
    }

    @Test
    public void testStopJobList() throws Exception {
        consoleService.stopJobList("jobResource", "127.0.0.1", EJobCacheStage.DB.getStage(), Collections.singletonList("j1"),Lists.newArrayList(), ForceCancelFlag.YES.getFlag(),null);
        consoleService.stopJobList("jobResource", "127.0.0.1", EJobCacheStage.PRIORITY.getStage(), Collections.singletonList("j1"),Lists.newArrayList(), ForceCancelFlag.YES.getFlag(),null);

        UserDTO userDTO = new UserDTO();
        consoleService.stopJobList("jobResource", "127.0.0.1", EJobCacheStage.LACKING.getStage(), Collections.singletonList("j1"), userDTO);
    }

    @Test
    public void testClusterResources() {
        ClusterResource result = consoleService.clusterResources("default", Maps.newHashMap(), -1L);
    }

    @Test
    public void testGetTaskResourceTemplate() {
        List<TaskTypeResourceTemplateVO> result = consoleService.getTaskResourceTemplate();
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void testListJobGenerationRecord() {
        List<JobGenerationVO> result = consoleService.listJobGenerationRecord(Integer.valueOf(0));
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void testGetUser() {
        List<UserNameVO> result = consoleService.getUser(-1L, Lists.newArrayList(-1L));
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));

        List<UserNameVO> result2 = consoleService.getUser("-1L", Lists.newArrayList(-1L));
        Assert.assertTrue(CollectionUtils.isNotEmpty(result2));
    }

    @Test
    public void testGetAlertConfig() {
        AlertConfigVO result = consoleService.getAlertConfig();
        Assert.assertNotNull(result);
    }

    @Test
    public void testSaveAlertConfig() {
        AlertConfigVO result = consoleService.saveAlertConfig(new AlertConfigVO());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetProducts(){
        List<ProductNameVO> result = consoleService.getProducts();
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));
    }
}