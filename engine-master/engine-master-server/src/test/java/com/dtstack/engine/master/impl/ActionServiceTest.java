package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.param.CatalogParam;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.AppTypeVO;
import com.dtstack.engine.api.vo.JobIdAndStatusVo;
import com.dtstack.engine.api.vo.JobLogVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.master.mockcontainer.impl.ActionServiceMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@MockWith(ActionServiceMock.class)
public class ActionServiceTest {
    ActionService actionService = new ActionService();

    @Test
    public void testStart() throws Exception {
        String param = "{  \"taskId\" : \"4j66umhct4q0\",  \"name\" : \"assets_real_time_3503_engineId_4j66umhct4q0\",  \"taskType\" : 2,  \"engineType\" : \"flink\",  \"computeType\" : 0,  \"sqlText\" : \"eyJqb2IiOnsiY29udGVudCI6W3sicmVhZGVyIjp7InBhcmFtZXRlciI6eyJkYkxpc3QiOltdLCJwYXNzd29yZCI6IiIsIm1ldGFTdG9yZVVyaXMiOiJ0aHJpZnQ6Ly9zdHJlYW0wMDE6OTA4Myx0aHJpZnQ6Ly9zdHJlYW0wMDI6OTA4MyIsImhhZG9vcENvbmZpZyI6eyJwcmluY2lwYWwiOiJoaXZlL3N0cmVhbTAwMUBEVFNUQUNLLkNPTSIsInJlbW90ZURpciI6Ii9kYXRhL3NmdHAvRHNDZW50ZXJfMjM3MjEiLCJwcmluY2lwYWxGaWxlIjoiaGl2ZS5rZXl0YWIiLCJoaXZlLm1ldGFzdG9yZS51cmlzIjoidGhyaWZ0Oi8vc3RyZWFtMDAxOjkwODMsdGhyaWZ0Oi8vc3RyZWFtMDAyOjkwODMiLCJ1c2VMb2NhbEZpbGUiOiJmYWxzZSIsInNmdHBDb25mIjp7InBhdGgiOiIvZGF0YS9zZnRwIiwicGFzc3dvcmQiOiJkdEBzei5jb20iLCJwb3J0IjoiMjIiLCJhdXRoIjoiMSIsImhvc3QiOiIxNzIuMTYuODIuMTQyIiwidXNlcm5hbWUiOiJyb290In0sImphdmEuc2VjdXJpdHkua3JiNS5jb25mIjoia3JiNS5jb25mIn0sImpkYmNVcmwiOiJqZGJjOmhpdmUyOi8vc3RyZWFtMDAxOjEwMDAwL2RlZmF1bHQ7cHJpbmNpcGFsPWhpdmUvc3RyZWFtMDAxQERUU1RBQ0suQ09NIiwic291cmNlIjoiaGl2ZSBzZXJ2ZXIyIiwidmVyc2lvbiI6ImFwYWNoZTIiLCJ1c2VybmFtZSI6IiJ9LCJuYW1lIjoibWV0YWRhdGFoaXZlMmNkY3JlYWRlciJ9LCJ3cml0ZXIiOnsicGFyYW1ldGVyIjp7Im1ldGhvZCI6InBvc3QiLCJjb2x1bW4iOltdLCJiYXRjaEludGVydmFsIjoxLCJoZWFkZXIiOnt9LCJib2R5Ijp7fSwicGFyYW1zIjp7InNvdXJjZUlkIjoxMTU1LCJqb2JJZCI6IiR7am9iSWR9IiwidGVuYW50SWQiOjEsInRhc2tJZCI6MzUwM30sImJhdGNoU2l6ZSI6MSwidXJsIjoiaHR0cDovLzE3Mi4xNi44Mi4xNDI6ODg3NS9kbWV0YWRhdGEvdjEvc3luY0pvYi9zeW5jQ2FsbEJhY2sifSwibmFtZSI6InJlc3RhcGl3cml0ZXIifX1dLCJzZXR0aW5nIjp7InJlc3RvcmUiOnsiaXNTdHJlYW0iOnRydWUsImlzUmVzdG9yZSI6dHJ1ZX0sImVycm9yTGltaXQiOnsicmVjb3JkIjoxMDB9LCJzcGVlZCI6eyJieXRlcyI6MTA0ODU3NiwiY2hhbm5lbCI6MX19fX0=\",  \"taskParams\" : \"{\\\"execution.checkpointing.interval\\\":\\\"1800000\\\"}\",  \"exeArgs\" : \"-job %7B%22job%22%3A%7B%22content%22%3A%5B%7B%22reader%22%3A%7B%22parameter%22%3A%7B%22dbList%22%3A%5B%5D%2C%22password%22%3A%22%22%2C%22metaStoreUris%22%3A%22thrift%3A%2F%2Fstream001%3A9083%2Cthrift%3A%2F%2Fstream002%3A9083%22%2C%22hadoopConfig%22%3A%7B%22principal%22%3A%22hive%2Fstream001%40DTSTACK.COM%22%2C%22remoteDir%22%3A%22%2Fdata%2Fsftp%2FDsCenter_23721%22%2C%22principalFile%22%3A%22hive.keytab%22%2C%22hive.metastore.uris%22%3A%22thrift%3A%2F%2Fstream001%3A9083%2Cthrift%3A%2F%2Fstream002%3A9083%22%2C%22useLocalFile%22%3A%22false%22%2C%22sftpConf%22%3A%7B%22path%22%3A%22%2Fdata%2Fsftp%22%2C%22password%22%3A%22dt%40sz.com%22%2C%22port%22%3A%2222%22%2C%22auth%22%3A%221%22%2C%22host%22%3A%22172.16.82.142%22%2C%22username%22%3A%22root%22%7D%2C%22java.security.krb5.conf%22%3A%22krb5.conf%22%7D%2C%22jdbcUrl%22%3A%22jdbc%3Ahive2%3A%2F%2Fstream001%3A10000%2Fdefault%3Bprincipal%3Dhive%2Fstream001%40DTSTACK.COM%22%2C%22source%22%3A%22hive+server2%22%2C%22version%22%3A%22apache2%22%2C%22username%22%3A%22%22%7D%2C%22name%22%3A%22metadatahive2cdcreader%22%7D%2C%22writer%22%3A%7B%22parameter%22%3A%7B%22method%22%3A%22post%22%2C%22column%22%3A%5B%5D%2C%22batchInterval%22%3A1%2C%22header%22%3A%7B%7D%2C%22body%22%3A%7B%7D%2C%22params%22%3A%7B%22sourceId%22%3A1155%2C%22jobId%22%3A%22%24%7BjobId%7D%22%2C%22tenantId%22%3A1%2C%22taskId%22%3A3503%7D%2C%22batchSize%22%3A1%2C%22url%22%3A%22http%3A%2F%2F172.16.82.142%3A8875%2Fdmetadata%2Fv1%2FsyncJob%2FsyncCallBack%22%7D%2C%22name%22%3A%22restapiwriter%22%7D%7D%5D%2C%22setting%22%3A%7B%22restore%22%3A%7B%22isStream%22%3Atrue%2C%22isRestore%22%3Atrue%7D%2C%22errorLimit%22%3A%7B%22record%22%3A100%7D%2C%22speed%22%3A%7B%22bytes%22%3A1048576%2C%22channel%22%3A1%7D%7D%7D%7D -confProp %7B%22execution.checkpointing.interval%22%3A%221800000%22%7D\",  \"requestStart\" : 0,  \"priority\" : 0,  \"generateTime\" : 1657260446673,  \"stopJobId\" : 0,  \"lackingCount\" : 0,  \"tenantId\" : 1,  \"deployMode\" : \"2\",  \"appType\" : 13,  \"submitExpiredTime\" : 0,  \"projectId\" : -1}";
        ParamActionExt paramActionExt = JSONObject.parseObject(param, ParamActionExt.class);
        Boolean result = actionService.start(paramActionExt);
        Assert.assertEquals(Boolean.FALSE, result);

        //test fail
        paramActionExt.setTaskType(null);
        result = actionService.start(paramActionExt);
        Assert.assertFalse(result);

        actionService.containerInfos(paramActionExt);

        paramActionExt.setTaskId("4j66umhct4q1");
        paramActionExt.setTaskType(EScheduleJobType.FLINK_SQL.getType());
        result = actionService.refreshStatus(paramActionExt);
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testStartJob() throws Exception {
        String param = "{\n" +
                "        \"id\":-1,\n" +
                "        \"isDeleted\":0,\n" +
                "        \"tenantId\":1,\n" +
                "        \"projectId\":1361,\n" +
                "        \"dtuicTenantId\":1,\n" +
                "        \"appType\":4,\n" +
                "        \"name\":\"xj0613_TAGSQL_临时任务\",\n" +
                "        \"taskType\":0,\n" +
                "        \"computeType\":1,\n" +
                "        \"engineType\":30,\n" +
                "        \"sqlText\":\"CREATE TABLE temp_2207_1705_TAGSQL_20220708135204 AS select user_id, sex as sex1 from dl_user_new_partition limit 100\",\n" +
                "        \"taskParams\":\"\",\n" +
                "        \"scheduleConf\":\"{\\\"beginDate\\\":\\\"2020-01-01\\\",\\\"endDate\\\":\\\"2220-01-01\\\",\\\"hour\\\":\\\"0\\\",\\\"isExpire\\\":false,\\\"isFailRetry\\\":false,\\\"maxRetryNum\\\":0,\\\"min\\\":\\\"00\\\",\\\"periodType\\\":2,\\\"selfReliance\\\":false}\",\n" +
                "        \"scheduleStatus\":1,\n" +
                "        \"submitStatus\":1,\n" +
                "        \"ownerUserId\":1,\n" +
                "        \"taskDesc\":\"sql加工标签的临时任务\",\n" +
                "        \"mainClass\":\"\",\n" +
                "        \"exeArgs\":\"1\",\n" +
                "        \"isExpire\":0,\n" +
                "        \"businessType\":\"0\",\n" +
                "        \"extraInfo\":\"{\\\"isFailRetry\\\":false,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"CREATE TABLE temp_2207_1705_TAGSQL_20220708135204 AS select user_id, sex as sex1 from dl_user_new_partition limit 100\\\",\\\"computeType\\\":1,\\\"pluginInfo\\\":{\\\"typeName\\\":\\\"trino\\\",\\\"dtDataSourceId\\\":24019},\\\"engineType\\\":\\\"trino\\\",\\\"maxRetryNum\\\":0,\\\"taskParams\\\":\\\"\\\",\\\"userId\\\":null,\\\"taskType\\\":0,\\\"multiEngineType\\\":7,\\\"name\\\":\\\"xj0613_TAGSQL_临时任务\\\",\\\"tenantId\\\":1,\\\"taskId\\\":-1}\",\n" +
                "        \"taskId\":-1,\n" +
                "        \"versionId\":0,\n" +
                "        \"pageSize\":10,\n" +
                "        \"pageIndex\":1,\n" +
                "        \"sort\":\"desc\",\n" +
                "        \"version\":0\n" +
                "    }";

        ScheduleTaskShade scheduleTaskShade = JSONObject.parseObject(param, ScheduleTaskShade.class);
        Boolean result = actionService.startJob(scheduleTaskShade, "jobId", "flowJobId");
    }


    @Test
    public void testStop() throws Exception {
        Boolean result = actionService.stop(Collections.singletonList("String"), 1L);
        Assert.assertEquals(Boolean.TRUE, result);
    }


    @Test
    public void testStatus() throws Exception {
        Integer result = actionService.status("test");
        Assert.assertEquals(Integer.valueOf(0), result);
    }

    @Test
    public void testStatusByJobIds() throws Exception {
        Map<String, Integer> result = actionService.statusByJobIds(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testStatusByJobIdsToVo() throws Exception {
        List<JobIdAndStatusVo> result = actionService.statusByJobIdsToVo(Collections.singletonList("String"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testStartTime() throws Exception {
        actionService.startTime("jobId");
    }

    @Test
    public void testLog() throws Exception {
        ActionLogVO result = actionService.log("jobId", 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testLogUnite() throws Exception {
        JobLogVO result = actionService.logUnite("test", 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testLogFromEs() throws Exception {
        String result = actionService.logFromEs("jobId");
        Assert.assertNotNull(result);
    }


    @Test
    public void testEntitys() throws Exception {
        List<ActionJobEntityVO> result = actionService.entitys(Arrays.<String>asList("String"));
        Assert.assertNotNull(result);
    }


    @Test
    public void testGenerateUniqueSign() throws Exception {
        String result = actionService.generateUniqueSign();
        Assert.assertNotNull(result);
    }

    @Test
    public void testResetTaskStatus() throws Exception {
        String result = actionService.resetTaskStatus("test");
        Assert.assertNotNull(result);
    }

    @Test
    public void testListJobStatus() throws Exception {
        actionService.listJobStatus(1L, 0);
    }

    @Test
    public void testListJobStatusScheduleJob() throws Exception {
        List<ScheduleJob> result = actionService.listJobStatusScheduleJob(System.currentTimeMillis(), 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testListJobStatusByJobIds() throws Exception {
        List<ActionJobStatusVO> result = actionService.listJobStatusByJobIds(Arrays.asList("test"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAllAppType() throws Exception {
        List<AppTypeVO> result = actionService.getAllAppType();
        Assert.assertNotNull(result);
    }


    @Test
    public void testExecuteCatalog() throws Exception {
       actionService.executeCatalog(new CatalogParam());
    }
}