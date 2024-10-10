package com.dtstack.engine.master.router.callback;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiResultTest {

    private static final ApiResult apiResult = new ApiResult();

    @Test
    public void test() {
        new ApiResult<>(1,"");
        new ApiResult<>(1,"",null);
    }


    @Test
    public void createErrorResult() {
        ApiResult.createErrorResult("error mock",1);
    }

    @Test
    public void getCode() {
        apiResult.getCode();
    }

    @Test
    public void setCode() {
        apiResult.setCode(1);
    }

    @Test
    public void getMessage() {
        apiResult.getMessage();
    }

    @Test
    public void setMessage() {
        apiResult.setMessage("error");
    }

    @Test
    public void getData() {
        apiResult.getData();
    }

    @Test
    public void setData() {
        apiResult.setData(null);
    }

    @Test
    public void getVersion() {
        apiResult.getVersion();
    }

    @Test
    public void setVersion() {
        apiResult.setVersion("1");
    }

    @Test
    public void getSpace() {
        apiResult.getSpace();
    }

    @Test
    public void setSpace() {
        apiResult.setSpace(1);
    }
}