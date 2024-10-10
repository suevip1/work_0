package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sdk.core.common.ApiResponse;

import java.net.URLClassLoader;

public class SyncDataSourceMock extends BaseMock {

    @MockInvoke(targetClass = DataSourceAPIClient.class)
    ApiResponse<DsServiceInfoDTO> getDsInfoById(Long dataInfoId) {
        DsServiceInfoDTO dto = new DsServiceInfoDTO();
        dto.setDtuicTenantId(1L);
        JSONObject object = new JSONObject();
        object.put("jdbcurl", "jdbc:hive2://127.0.0.1:10000/test_xxxxa");
        dto.setDataJson(object.toJSONString());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(dto);
        return apiResponse;
    }

    @MockInvoke(targetClass = URLClassLoader.class)
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName("com.dtstack.engine.master.mockcontainer.impl.TestExecutor");
    }


}
