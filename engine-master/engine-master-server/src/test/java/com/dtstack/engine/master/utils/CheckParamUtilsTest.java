package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckParamUtilsTest {

    @Test
    public void checkAppType() {
        CheckParamUtils.checkAppType(1);
    }

    @Test(expected = RdosDefineException.class)
    public void checkAppType2() {
        CheckParamUtils.checkAppType(null);
    }

    @Test(expected = RdosDefineException.class)
    public void checkAppType3() {
        CheckParamUtils.checkAppType(999);
    }


    @Test(expected = RdosDefineException.class)
    public void checkDtUicTenantId() {
        CheckParamUtils.checkDtUicTenantId(null);
        CheckParamUtils.checkDtUicTenantId(-1L);
    }

    @Test(expected = RdosDefineException.class)
    public void checkPageSize() {
        CheckParamUtils.checkPageSize(null,null);
    }
}