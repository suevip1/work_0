package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.master.mockcontainer.impl.ElasticsearchServiceMock;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHost;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@MockWith(ElasticsearchServiceMock.class)
public class ElasticsearchServiceTest {

    ElasticsearchService elasticsearchService = new ElasticsearchService();

    @Test
    public void testAfterPropertiesSet() throws Exception {
        elasticsearchService.afterPropertiesSet();
    }

    @Test
    public void testSearchWithJobId() throws Exception {
        PrivateAccessor.set(elasticsearchService,"index","index");
        PrivateAccessor.set(elasticsearchService,"username","index");
        PrivateAccessor.set(elasticsearchService,"password","index");
        PrivateAccessor.set(elasticsearchService,"fetchSize",100);
        PrivateAccessor.set(elasticsearchService,"keepAlive",100L);
        String result = elasticsearchService.searchWithJobId("fileName", "jobId");
    }

    @Test
    public void testParseContent() throws Exception {
        Pair<String, String> result = elasticsearchService.parseContent("{}");
    }

    @Test
    public void testDestroy() throws Exception {
        elasticsearchService.destroy();
    }
}