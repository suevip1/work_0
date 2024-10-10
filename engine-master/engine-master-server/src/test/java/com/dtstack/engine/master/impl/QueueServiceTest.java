package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.master.mockcontainer.impl.ElasticsearchServiceMock;
import com.dtstack.engine.master.mockcontainer.impl.QueueServiceMock;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-29 17:54
 */
@MockWith(QueueServiceMock.class)
public class QueueServiceTest {
    QueueService queueService = new QueueService();

    @Test
    public void testEncode() {
        String sql = "sh /opt/dtstack/DTDatasourceX/DatasourceX/script/datasourcex_launcher.sh start metadata_collect eyJqb2IiOnsiY29udGVudCI6W3sicmVhZGVyIjp7Im5hbWUiOiJtZXRhZGF0YXNhcGhhbmFyZWFkZXIiLCJwYXJhbWV0ZXIiOnsiZGJMaXN0IjpbeyJkYk5hbWUiOiJTWVMiLCJ0YWJsZUxpc3QiOltdfV0sImpkYmNVcmwiOiJqZGJjOnNhcDovLzE3Mi4xNi4yMi4xNTE6MzkwMTUiLCJwYXNzd29yZCI6IkFiYyFAIzU3OSIsInNvdXJjZSI6ImhpdmUgc2VydmVyMiIsInVzZXJuYW1lIjoiU1lTVEVNIiwidmVyc2lvbiI6ImFwYWNoZTIifX0sIndyaXRlciI6eyJuYW1lIjoicmVzdGFwaXdyaXRlciIsInBhcmFtZXRlciI6eyJiYXRjaEludGVydmFsIjoxMDAsIm1ldGhvZCI6InBvc3QiLCJwYXJhbXMiOnsiam9iSWQiOiIke2pvYklkfSIsInNvdXJjZUlkIjoyMzUsInRhc2tJZCI6MzczLCJ0ZW5hbnRJZCI6MTA0OTl9LCJ1cmwiOiJodHRwOi8vMTkyLjE2OC45Ny4xMTU6ODg3NS9kbWV0YWRhdGEvdjEvc3luY0pvYi9zeW5jQ2FsbEJhY2sifX19XSwic2V0dGluZyI6eyJlcnJvckxpbWl0Ijp7InJlY29yZCI6MTAwfSwic3BlZWQiOnsiYnl0ZXMiOjEwNDg1NzYsImNoYW5uZWwiOjF9fX19";
        String[] commands = sql.split("\\s+");
        String originJson = commands[commands.length - 1];
        String afterJson = Base64Util.baseDecode(originJson).replace(TaskConstant.JOB_ID, "qiuyun");
        String result = sql.replace(originJson, Base64Util.baseEncode(afterJson));
        System.out.println(result);
    }

    @Test
    public void updateQueue() {
        ComponentTestResult.QueueDescription child = new ComponentTestResult.QueueDescription();
        child.setChildQueues(null);
        child.setQueueName("child");
        child.setQueuePath("child");
        List<ComponentTestResult.QueueDescription> descriptions = Lists.newArrayList(child);
        ComponentTestResult.ClusterResourceDescription description = new ComponentTestResult.ClusterResourceDescription(1, 1, 1, descriptions);

        queueService.updateQueue(-1L, description);
        queueService.updateQueue(1L, description);
    }

    @Test
    public void addNamespaces() {
        queueService.addNamespaces(-1L, "namespace");
    }

    @Test
    public void getQueueByPath() {
        // queueService.getQueueByPath(-1L, "default");
    }
}