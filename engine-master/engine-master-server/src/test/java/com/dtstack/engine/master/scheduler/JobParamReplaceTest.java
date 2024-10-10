package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.schedule.common.enums.EParamType;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 9:45 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobParamReplaceTest {

    JobParamReplace jobParamReplace = new JobParamReplace();


    @Test
    public void testStopGraphBuildIsMaster() throws Exception {
        // sql 为"" 时
        String sql = "create table kudu.\"${hxb_pub}\".table";
        List<ScheduleTaskParamShade> taskParamsToReplace = new ArrayList<>();
        ScheduleTaskParamShade scheduleTaskParamShade = new ScheduleTaskParamShade();
        scheduleTaskParamShade.setType(EParamType.GLOBAL_PARAM_CONST.getType());
        scheduleTaskParamShade.setParamName("hxb_pub");
        scheduleTaskParamShade.setParamCommand("test");
        taskParamsToReplace.add(scheduleTaskParamShade);
        String cycTime = "20201116000000";
        String r1 = jobParamReplace.paramReplace(sql, taskParamsToReplace, cycTime, 0, null);
        Assert.assertNotNull(r1);
        System.out.println(r1);

        String infoJosn = "{\n" +
                "  \"info\": \"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"gmtCreate\\\\\\\":1605509510000,\\\\\\\"gmtModified\\\\\\\":1605509510000,\\\\\\\"id\\\\\\\":303,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"yyyyMMdd-1\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.bizdate\\\\\\\",\\\\\\\"taskId\\\\\\\":1619,\\\\\\\"type\\\\\\\":0},{\\\\\\\"gmtCreate\\\\\\\":1605509510000,\\\\\\\"gmtModified\\\\\\\":1605509510000,\\\\\\\"id\\\\\\\":305,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"${bdp.system.currenttime}\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.runtime\\\\\\\",\\\\\\\"taskId\\\\\\\":1619,\\\\\\\"type\\\\\\\":0},{\\\\\\\"gmtCreate\\\\\\\":1605509510000,\\\\\\\"gmtModified\\\\\\\":1605509510000,\\\\\\\"id\\\\\\\":307,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"1234\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"dd\\\\\\\",\\\\\\\"taskId\\\\\\\":1619,\\\\\\\"type\\\\\\\":1}]\\\",\\\"sqlText\\\":\\\"use dev2;\\\\nSELECT ${bdp.system.bizdate},${dd},${bdp.system.runtime};\\\\n\\\",\\\"computeType\\\":1,\\\"engineType\\\":\\\"spark\\\",\\\"taskParams\\\":\\\"## Driver程序使用的CPU核数,默认为1\\\\r\\\\n# driver.cores=1\\\\r\\\\n\\\\n## Driver程序使用内存大小,默认512m\\\\r\\\\n# driver.memory=512m\\\\r\\\\n\\\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\\\r\\\\n# driver.maxResultSize=1g\\\\r\\\\n\\\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\\\r\\\\n# logConf=false\\\\r\\\\n\\\\n## 启动的executor的数量，默认为1\\\\r\\\\nexecutor.instances=1\\\\r\\\\n\\\\n## 每个executor使用的CPU核数，默认为1\\\\r\\\\nexecutor.cores=1\\\\r\\\\n\\\\n## 每个executor内存大小,默认512m\\\\r\\\\n# executor.memory=512m\\\\r\\\\n\\\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\\\r\\\\njob.priority=10\\\\r\\\\n\\\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\\\r\\\\n# logLevel = INFO\\\\r\\\\n\\\\n## spark中所有网络交互的最大超时时间\\\\r\\\\n# spark.network.timeout=120s\\\\r\\\\n\\\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\\\r\\\\n# spark.yarn.executor.memoryOverhead\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":0,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"test\\\",\\\"tenantId\\\":1,\\\"taskId\\\":1619}\"\n" +
                "}";
        JSONObject extObject = JSONObject.parseObject(infoJosn);
        JSONObject info = extObject.getJSONObject(TaskConstant.INFO);
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        String r2 = jobParamReplace.paramReplace(sql, taskParamsToReplace, cycTime, 0, null);
        Assert.assertNotNull(r2);

        sql = (String) actionParam.getOrDefault("sqlText", "");
        String r3 = jobParamReplace.paramReplace(sql, taskParamsToReplace, cycTime, 0, null);
        Assert.assertNotNull(r3);
    }

    @Test
    public void testParseTaskParam() throws IOException {
        String infoJosn = "{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"gmtCreate\\\\\\\":1647315432000,\\\\\\\"gmtModified\\\\\\\":1647315432000,\\\\\\\"id\\\\\\\":5907,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"dog\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"key1\\\\\\\",\\\\\\\"taskId\\\\\\\":36009,\\\\\\\"type\\\\\\\":1},{\\\\\\\"gmtCreate\\\\\\\":1647315432000,\\\\\\\"gmtModified\\\\\\\":1647315432000,\\\\\\\"id\\\\\\\":5909,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"${yyyy-MM-dd}\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"key2\\\\\\\",\\\\\\\"taskId\\\\\\\":36009,\\\\\\\"type\\\\\\\":1}]\\\",\\\"sqlText\\\":\\\"use 112session;\\\\nset hive.default.fileformat.managed = parquet;\\\\nset hive.default.fileformat = parquet;\\\\nselect 1,'${key1}', '${key2}';\\\\ncreate table fuck_01 as select 1;\\\\nselect * from fuck_01;\\\\n\\\",\\\"computeType\\\":1,\\\"engineType\\\":\\\"spark\\\",\\\"taskParams\\\":\\\"## Driver程序使用的CPU核数,默认为1\\\\n# driver.cores=1\\\\n\\\\n## Driver程序使用内存大小,默认512m\\\\n# driver.memory=512m\\\\n\\\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\\\n# driver.maxResultSize=1g\\\\n\\\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\\\n# logConf=false\\\\n\\\\n## 启动的executor的数量，默认为1\\\\nexecutor.instances=1\\\\n\\\\n## 每个executor使用的CPU核数，默认为1\\\\nexecutor.cores=1\\\\n\\\\n## 每个executor内存大小,默认512m\\\\n# executor.memory=512m\\\\n\\\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\\\njob.priority=10\\\\n\\\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\\\n# logLevel = INFO\\\\n\\\\n## spark中所有网络交互的最大超时时间\\\\n# spark.network.timeout=120s\\\\n\\\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\\\n# spark.yarn.executor.memoryOverhead\\\\n\\\\n#spark.sql.adaptive.enabled=true\\\\n#spark.sql.shuffle.partitions=10\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"taskType\\\":0,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"den4\\\",\\\"tenantId\\\":1,\\\"taskId\\\":36009}\"}";
        JSONObject extObject = JSONObject.parseObject(infoJosn);
        JSONObject info = extObject.getJSONObject(TaskConstant.INFO);
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        Map<String, String> nameToValue = jobParamReplace.parseTaskParam(taskParamsToReplace, "20211207000000", 1, 1L);
        System.out.println(nameToValue);
    }

    private List<ScheduleTaskParamShade> getScheduleTaskParamShade() {
        List<ScheduleTaskParamShade> scheduleTaskParamShades = Lists.newArrayList();
        String sysTypeParam1 = "{\"paramCommand\":\"yyyyMMddHHmmss\",\"paramName\":\"bdp.system.cyctime1\",,\"type\":0}";
        ScheduleTaskParamShade sysTypeParamTask1 = JSON.parseObject(sysTypeParam1, ScheduleTaskParamShade.class);

        String sysTypeParam2 = "{\"paramCommand\":\"yyyyMMdd\",\"paramName\":\"bdp.system.cyctime2\",,\"type\":0}";
        ScheduleTaskParamShade sysTypeParamTask2 = JSON.parseObject(sysTypeParam2, ScheduleTaskParamShade.class);

        String sysTypeParam3 = "{\"paramCommand\":\"yyyyMMdd-1\",\"paramName\":\"bdp.system.cyctime3\",,\"type\":0}";
        ScheduleTaskParamShade sysTypeParamTask3 = JSON.parseObject(sysTypeParam3, ScheduleTaskParamShade.class);

        scheduleTaskParamShades.add(sysTypeParamTask1);
        scheduleTaskParamShades.add(sysTypeParamTask2);
        scheduleTaskParamShades.add(sysTypeParamTask3);

        String customizeType = "{\"paramCommand\":\"$[format(yyyyMMddHHmmss,'UnixTimestamp13')]\",\"paramName\":\"bdp.customize.time.format\",,\"type\":1}";
        ScheduleTaskParamShade sysTypeParamTask4 = JSON.parseObject(customizeType, ScheduleTaskParamShade.class);
        scheduleTaskParamShades.add(sysTypeParamTask4);

        String component = "{\"paramCommand\":\"123123\",\"paramName\":\"abc\",,\"type\":2}";
        ScheduleTaskParamShade sysTypeParamTask5 = JSON.parseObject(component, ScheduleTaskParamShade.class);
        scheduleTaskParamShades.add(sysTypeParamTask5);

        return scheduleTaskParamShades;
    }


}
