package com.dtstack.engine.master.multiengine.checker;

import cn.hutool.core.lang.Assert;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.convert.load.SourceLoaderService;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.Column;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.assertj.core.util.Lists;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnCheckerTest {
    ColumnChecker columnChecker = new ColumnChecker();


    @Test
    public void testTemplate() throws Exception {
        String template = "[${sign!}]离线任务：${task.name!}失败告警\n" +
                "任务：${task.name!}\n" +
                "项目：${project.projectAlias!}\n" +
                "租户：${tenant.tenantName!}\n" +
                "调度类型：${jobScheduleType!}\n" +
                "计划时间：${jobCycTime!}\n" +
                "开始时间：${job.execStartTime!,dateFormat='yyyy-MM-dd HH:mm:ss'}\n" +
                "结束时间：${job.execEndTime!,dateFormat='yyyy-MM-dd HH:mm:ss'}\n" +
                "运行时长：${jobExecTime!}\n" +
                "当前状态：${statusStr!}\n" +
                "<%\n" +
                "       if(errorMsg!=null){\n" +
                "            println('失败原因:' + errorMsg);\n" +
                "        }\n" +
                "%>\n" +
                "责任人：${user.userName!}\n" +
                "请及时处理!";
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template t = gt.getTemplate(template);
        Map<String,Object> replaceParamMap = new HashMap<>();
        replaceParamMap.put("errorMsg","元数据发生变更(源表testTable  新增字段age name类型varchar变更为int)");
        t.binding(replaceParamMap);
        System.out.println(t.render());
    }

    @Test
    public void testCheck() {
        List<Column> columnList = new ArrayList<>();
        Column column = new Column();
        column.setName("name");
        column.setType("int");
        columnList.add(column);
        String jobParams2 = columnChecker.checkColumn(columnList, 1L, "testTable", "default", DataSourceType.HIVE.getVal(), true);
        System.out.println(jobParams2);
        Assert.notNull(jobParams2);
    }

    public static class Mock {
        @MockInvoke(
                targetClass = IClient.class,
                targetMethod = "getColumnMetaData"
        )
        public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
            ColumnMetaDTO column = new ColumnMetaDTO();
            column.setKey("name");
            column.setType("varchar");

            ColumnMetaDTO column2 = new ColumnMetaDTO();
            column2.setKey("age");
            column2.setType("int");
            return Lists.newArrayList(column, column2);
        }

        @MockInvoke(
                targetClass = ClientCache.class,
                targetMethod = "getClient"
        )
        public static IClient getClient(Integer dataSourceType) {
            return null;
        }

        @MockInvoke(
                targetClass = SourceLoaderService.class,
                targetMethod = "buildSourceDTO"
        )
        public ISourceDTO buildSourceDTO(Long datasourceId) {
            return new ISourceDTO() {
                @Override
                public String getUsername() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public Integer getSourceType() {
                    return null;
                }

                @Override
                public Long getTenantId() {
                    return null;
                }

                @Override
                public void setTenantId(Long tenantId) {

                }
            };
        }
    }
}
