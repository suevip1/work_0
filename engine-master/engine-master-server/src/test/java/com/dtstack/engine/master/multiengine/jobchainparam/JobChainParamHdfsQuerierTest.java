package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.loader.dto.HdfsQueryDTO;
import com.dtstack.engine.master.mockcontainer.impl.JobChainParamHdfsQuerierMock;
import org.junit.Assert;
import org.junit.Test;

@MockWith(JobChainParamHdfsQuerierMock.class)
public class JobChainParamHdfsQuerierTest {
    JobChainParamQuerier jobChainParamHdfsQuerier = new JobChainParamQuerier();

//    @Test
//    public void testGetForSql() throws Exception {
//        String result = jobChainParamHdfsQuerier.getForSql("engineType", "pluginInfo", "hdfsPath", 0, 0);
//        Assert.assertEquals("test", result);
//    }
//
//    @Test
//    public void testGetForSql2() throws Exception {
//        String result = jobChainParamHdfsQuerier.getForSql("engineType", "pluginInfo", "hdfsPath", 0);
//        Assert.assertEquals("(test)", result);
//    }
//
//    @Test
//    public void testGetForScript() throws Exception {
//        String result = jobChainParamHdfsQuerier.getForScript("engineType", "pluginInfo", "hdfsPath");
//        Assert.assertEquals("test", result);
//    }
//
//    @Test
//    public void testIsAnyOneTooLarge() throws Exception {
//        boolean result = jobChainParamHdfsQuerier.isAnyOneTooLarge(1L, "engineType", "pluginInfo", Collections.singletonList("String"));
//        Assert.assertTrue(result);
//    }
//
//    @Test
//    public void testExistHdfsPath() throws Exception {
//        boolean result = jobChainParamHdfsQuerier.existHdfsPath("engineType", "pluginInfo", "hdfsPath");
//        Assert.assertTrue(result);
//    }
//
//    @Test
//    public void testDeleteHdfsFiles() throws Exception {
//        jobChainParamHdfsQuerier.deleteHdfsFiles("engineType", "pluginInfo", Collections.singletonList("String"));
//    }
//
//    @Test
//    public void testDeleteHdfsFile() throws Exception {
//        boolean result = jobChainParamHdfsQuerier.deleteHdfsFile("engineType", "pluginInfo", "deleteHdfsFile");
//        Assert.assertTrue(result);
//    }


    @Test
    public void testBuildQueryDtoForSql2() throws Exception {
        // HdfsQueryDTO result = JobChainParamQuerier.buildQueryDtoForSql("hdfsPath", 0, 0);
        // Assert.assertNotNull(result);
    }
}