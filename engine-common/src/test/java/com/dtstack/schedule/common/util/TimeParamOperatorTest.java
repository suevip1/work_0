package com.dtstack.schedule.common.util;

import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.schedule.common.dto.SystemTimeParamQueryDTO;
import com.dtstack.schedule.common.dto.SystemTimeParamQueryDTO.SystemTimeParamQueryDTOBuilder;
import com.dtstack.schedule.common.enums.ESystemTimeSpecialParam;
import org.junit.Assert;

import org.junit.Test;

public class TimeParamOperatorTest {

    @Test
    public void testOffset() {
        System.out.println(TimeParamOperator.transform("yyyyMMdd-1", "2023092000000"));
        TimeParamOperator.Offset offset = new TimeParamOperator.Offset("-", 2, "test1");
        Assert.assertEquals("20230917", TimeParamOperator.transform("yyyyMMdd-1", "2023092000000", offset));
        TimeParamOperator.Offset offset3 = new TimeParamOperator.Offset("-", 1, "test1");
        Assert.assertEquals("20230918", TimeParamOperator.transform("yyyyMMdd-1", "2023092000000", offset3));
        TimeParamOperator.Offset offset4 = new TimeParamOperator.Offset("+", 1, "test1");
        Assert.assertEquals("20230920", TimeParamOperator.transform("yyyyMMdd-1", "2023092000000", offset4));
        TimeParamOperator.Offset offset2 = new TimeParamOperator.Offset("+", 2, "test1");
        Assert.assertEquals("20230921", TimeParamOperator.transform("yyyyMMdd-1", "2023092000000", offset2));
        TimeParamOperator.Offset offset5 = new TimeParamOperator.Offset("+", 0, "test1");
        Assert.assertEquals("20230919", TimeParamOperator.transform("yyyyMMdd-1", "2023092000000", offset5));
        TimeParamOperator.Offset offset6 = new TimeParamOperator.Offset("+", 20, "test1");
        Assert.assertEquals("20231009", TimeParamOperator.transform("yyyyMMdd-1", "2023092000000", offset6));
    }

    @Test
    public void transform() {
        TimeParamOperator.transform("yyyyMMddHHmmss", "20220727175100");
        TimeParamOperator.transform("yyyy-MM-dd,-1", "20220727175100");
        TimeParamOperator.transform("yyyyMMdd-1", "20220727175100");
    }

    @Test
    public void plusHour() {
        String result = TimeParamOperator.transform("yyyyMM - 1", "20171212010101");
        System.out.println(result);
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("yyyyMMddHHmmss", "20220727175100", new TimeParamOperator.Offset("-", 1, "")));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("yyyyMMddHHmmss", "20220727175100", new TimeParamOperator.Offset("+", 1, "")));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10,'-')]", "20180607010101", new TimeParamOperator.Offset("+", 1, "")));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10,'-')]", "20180607010101", new TimeParamOperator.Offset("-", 1, "")));


        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10,'-')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-6,'/')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-6,':')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyy,-10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyy,10)]", "20180607010101"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+7*1,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-7*1,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+10,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-10,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-4/24,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24/60,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-3/24/60,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMM,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHH,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmm,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmmss,':']", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24,':']", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24mm,':']", "20180607172233"));


        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,12*2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,-12*1)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,-2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+7*1]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-7*1]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+10]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-10]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-4/24]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24/60]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-3/24/60]", "20180607030000"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[mm]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[dd]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH]", "20180607031700"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[MM]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[ss]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMM]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHH]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmm]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmmss]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24mm]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd HH:mm:ss]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH:mm:ss]", "20180607172233"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[format(yyyyMMddHHmmss,'UnixTimestamp13')]", "20180607172233"));
        System.out.println(TimeParamOperator.transform("yyyyMMdd-0", "20171212010101"));
        System.out.println(TimeParamOperator.transform("yyyyMMdd", "20171212010101"));
        System.out.println(TimeParamOperator.transform("yyyyMMdd", "20171212010101", new TimeParamOperator.Offset("+", 1, "")));
        System.out.println(TimeParamOperator.transform("yyyyMMdd", "20171212010101", new TimeParamOperator.Offset("-", 1, "")));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd HH:mm:ss]", "20180607172233", new TimeParamOperator.Offset("-", 1, "")));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH:mm:ss]", "20180607172233", new TimeParamOperator.Offset("+", 1, "")));

    }

    @Test
    public void testAll() {
        for (ESystemTimeSpecialParam value : ESystemTimeSpecialParam.values()) {
            if (ESystemTimeSpecialParam.SYSTEM_CURRENTTIME.equals(value)) {
                continue;
            }
            Object realCommand = PrivateAccessor.get(value, "realCommand");
            SystemTimeParamQueryDTO systemTimeParamQueryDTO = SystemTimeParamQueryDTOBuilder.builder().cycTime("20230701000000")
                    .realCommand((String) realCommand).build();
            String parsedValue = value.getParsedValue(systemTimeParamQueryDTO);
            System.out.println(realCommand + "----" + parsedValue);
        }
        TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthend}", "20230701000000");
    }

    @Test
    public void testLastYearAndLastQuarterEnd() {
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preyrend}", "20171212010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preyrend}", "20171231010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20171212010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170912010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170612010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170312010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170112010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170331010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170423010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrend}", "20170701010101"));

        // 上年初
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preyrstart2}", "20171212010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preyrstart}", "20171212010101"));

        // 上月末
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthend2}", "20171212010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthend2}", "20170112010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthend}", "20171212010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthend}", "20170112010101"));

        // 上月初
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthstart}", "20170112010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.premonthstart2}", "20170112010101"));

        // 上季初
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrstart}", "20170112010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrstart}", "20170505010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("${bdp.system.preqrtrstart2}", "20171212010101"));
    }
}