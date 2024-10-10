package com.dtstack.engine.master.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapUtilTest {

    @Test
    public void paramToMap() {
        MapUtil.paramToMap("sds=pp;sds=sds");
    }

    @Test
    public void lineToHump() {
        MapUtil.lineToHump("sds_Sdsd");
    }
}