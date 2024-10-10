package com.dtstack.engine.common.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExitExceptionTest {

    @Test
    public void test() {
        new ExitException(1);
    }

}