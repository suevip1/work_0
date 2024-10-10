package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.apache.http.Header;
import org.checkerframework.checker.units.qual.C;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.internal.InternalSearchResponse;

import java.io.IOException;

public class ElasticsearchServiceMock extends BaseMock {

    @MockInvoke(targetClass = RestHighLevelClient.class)
    public final ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest, Header... headers) throws IOException {
        return new ClearScrollResponse(true, 1);
    }

    @MockInvoke(targetClass = RestHighLevelClient.class)
    public final SearchResponse search(SearchRequest searchRequest, Header... headers) throws IOException {
        SearchResponse searchResponse = new SearchResponse();
        PrivateAccessor.set(searchResponse, "internalResponse", InternalSearchResponse.empty());
        return searchResponse;
    }
}
