package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.console.param.ConsoleCalenderTimeShowVO;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.master.mockcontainer.impl.ParamServiceMock;
import com.dtstack.engine.po.ScheduleTaskParam;
import com.dtstack.schedule.common.enums.EParamType;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

@MockWith(ParamServiceMock.class)
public class ParamServiceTest {

    ParamService paramService = new ParamService();

    @Test
    public void testAddOrUpdate() throws Exception {
        ConsoleParamVO consoleParamVO = new ConsoleParamVO();
        consoleParamVO.setId(1L);
        consoleParamVO.setParamName("test");
        consoleParamVO.setDateFormat("yyyyMMddHHss");
        consoleParamVO.setParamType(EParamType.CUSTOMIZE_TYPE.getType());
        File file = new File(getClass().getClassLoader().getResource("csv/calendar.csv").getFile());
        paramService.addOrUpdate(consoleParamVO, file);
    }

    @Test
    public void testPageQuery() throws Exception {
        List<ConsoleParam> result = paramService.pageQuery(new PageQuery(0, 0, null, "sort"));
        Assert.assertNotNull(result);
    }

    @Test
    public void testGeneralCount() throws Exception {
        int result = paramService.generalCount();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteById() throws Exception {
        paramService.deleteById(1L);
    }


    @Test(expected = Exception.class)
    public void testAddOrUpdateTaskParam() throws Exception {
        paramService.addOrUpdateTaskParam(1L, 0, Collections.singletonList(new ConsoleParamBO()));
    }

    @Test
    public void testFindById() throws Exception {
        ConsoleParam result = paramService.findById(1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testSelectByName() throws Exception {
        ConsoleParam result = paramService.selectByName("paramName");
        Assert.assertNotNull(result);
    }

    @Test
    public void testSelectSysParam() throws Exception {
        paramService.selectSysParam();
    }

    @Test
    public void testSelectByTaskId() throws Exception {

        List<ConsoleParamBO> result = paramService.selectByTaskId(1L, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void testDeleteTaskParamByTaskId() throws Exception {
        paramService.deleteTaskParamByTaskId(Collections.singletonList(1L), 0);
    }

    @Test
    public void testFindTotalTask() throws Exception {

        int result = paramService.findTotalTask(0L);
        Assert.assertEquals(1, result);
    }

    @Test
    public void testFindTask() throws Exception {
        List<ScheduleTaskParam> result = paramService.findTask(0L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testConvertGlobalToParamType() throws Exception {
        ScheduleTaskParamShade scheduleTaskParamShade = new ScheduleTaskParamShade();
        scheduleTaskParamShade.setParamName("test");
        scheduleTaskParamShade.setType(EParamType.GLOBAL.getType());
        JSONObject info = new JSONObject();
        info.put(GlobalConst.taskParamToReplace, JSONObject.toJSONString(Lists.newArrayList(scheduleTaskParamShade)));
        paramService.convertGlobalToParamType(info.toJSONString(),
                (p, taskParamShades) -> {

                });
    }

    @Test
    public void downloadExcel() {
        File file = new File(ConfigConstant.USER_DIR_DOWNLOAD + File.separator);
        try {
            if(!file.exists()){
                file.mkdirs();
            }
            file = paramService.downloadExcel(1L);
            Assert.assertNotNull(file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected = Exception.class)
    public void dealCustomizeDate() {
       paramService.dealCustomizeDate("test", "yyyy-MM-dd", "20220228022000",null);
    }

    public void getTimeByCalenderId() {
        ConsoleCalenderTimeShowVO timeByCalenderId = paramService.getTimeByCalenderId(1L);
        Assert.assertNotNull(timeByCalenderId);
    }


}