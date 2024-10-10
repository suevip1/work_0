package com.dtstack.engine.master.model.cluster.exception;

import com.dtstack.engine.common.enums.EComponentType;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-30 20:07
 */
public class InvalidComponentExceptionTest {

    @Test
    public void getErrorMsg() {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        BaseComponentException e = new InvalidComponentException(EComponentType.YARN, "xxx");
        assertEquals("组件 YARN 错误 xxx", e.getMessage());
        LocaleContextHolder.setLocale(Locale.US);
        assertEquals("Error on component of type YARN. xxx", e.getMessage());
    }
}