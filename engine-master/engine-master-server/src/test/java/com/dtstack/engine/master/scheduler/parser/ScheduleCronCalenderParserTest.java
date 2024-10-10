package com.dtstack.engine.master.scheduler.parser;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.scheduler.parser.ScheduleCronCalenderParserMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Collections;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-28 16:27
 */
@MockWith(ScheduleCronCalenderParserMock.class)
public class ScheduleCronCalenderParserTest {
    ScheduleCronCalenderParser sccp;
    @Before
    public void before() {
        sccp = new ScheduleCronCalenderParser(null, 1L, 1);
    }

    @Test
    public void parse() {
        sccp.parse(Collections.emptyMap());
    }

    @Test
    public void getTriggerTime() throws ParseException {
        System.out.println(sccp.getTriggerTime("2022-07-28"));
    }

    @Test
    public void getNearestTime() {
        System.out.println(sccp.getNearestTime(DateTime.now(), true));
    }

    @Test
    public void checkSpecifyDayCanExe() throws ParseException {
        sccp.checkSpecifyDayCanExe("20220728000000");
    }

    @Test
    public void testToString() {
        sccp.toString();
    }
}