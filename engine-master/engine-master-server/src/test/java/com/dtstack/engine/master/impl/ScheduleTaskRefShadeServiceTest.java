package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.common.util.PublicUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ScheduleTaskRefShadeServiceTest {

    private ScheduleTaskRefShadeService scheduleTaskRefShadeService = new ScheduleTaskRefShadeService();

    @Test
    public void processTaskParamForShipFiles() throws IOException {
        List<JobResourceFile> resourceFiles = new ArrayList<>();
        JobResourceFile jobResourceFile0 = new JobResourceFile();
        jobResourceFile0.setPath("hdfs://ns1/rdos/batch/leon.zip");
        resourceFiles.add(jobResourceFile0);
        JobResourceFile jobResourceFile1 = new JobResourceFile();
        resourceFiles.add(jobResourceFile1);
        String result = scheduleTaskRefShadeService.processTaskParamForShipFiles("## 每个worker所占内存，比如512m\n" +
                "# dtscript.worker.memory=512m\n" +
                "\n" +
                "## 每个worker所占的cpu核的数量\n" +
                "# dtscript.worker.cores=1\n" +
                "\n" +
                "## worker数量\n" +
                "# dtscript.worker.num=1\n" +
                "\n" +
                "## 是否独占机器节点\n" +
                "# dtscript.worker.exclusive=false\n" +
                "\n" +
                "## 任务优先级, 值越小，优先级越高，范围:1-1000\n" +
                "job.priority=10\n" +
                "\n" +
                "## 指定work运行节点，需要注意不要写ip应填写对应的hostname\n" +
                "# dtscript.worker.nodes=\n" +
                "\n" +
                "## 指定work运行机架\n" +
                "# dtscript.worker.racks=\n" +
                "\n" +
                "## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n" +
                "logLevel=INFO\n"
                +
                "\n" +
                "dtscript.ship-files=hdfs://ns1/rdos/batch/4351_fk_package_fk_package.zip", resourceFiles);
        Assert.assertNotNull(result);
        Properties properties = PublicUtil.stringToProperties(result);
    }
}