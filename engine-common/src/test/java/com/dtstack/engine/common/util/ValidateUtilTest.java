package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateUtilTest {

    @Test(expected = RdosDefineException.class)
    public void validateNotNull() {
        ValidateUtil.validateNotNull(null,"null");
    }

    @Test
    public void isRegularName() {
        Assert.assertFalse(ValidateUtil.isRegularName("123"));
    }
}