package com.dtstack.engine.master.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CsvUtilTest {

    @Test
    public void readFromCsv() {
        CsvUtil.readFromCsv(this.getClass().getClassLoader().getResource("csv/calendar.csv").getPath(),1,2);
    }

    @Test
    public void countLine() {
        CsvUtil.countLine(this.getClass().getClassLoader().getResource("csv/calendar.csv").getPath());
    }

    @Test
    public void checkSizeAndLine() {
        CsvUtil.checkSizeAndLine(this.getClass().getClassLoader().getResource("csv/calendar.csv").getPath());
    }

    @Test
    public void testCheckSizeAndLine() {
        CsvUtil.checkSizeAndLine(this.getClass().getClassLoader().getResource("csv/calendar.csv").getPath());
    }
}