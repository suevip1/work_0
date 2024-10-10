package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;

public class TestExecutor {
    public String execute(String test, String param) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",test);
        return jsonObject.toJSONString();
    }
}