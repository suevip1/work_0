package com.dtstack.engine.master.mockcontainer.scheduler;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.fastjson.JSON;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamCleaner;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-28 14:09
 */
public class DataClearScheduleMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleDictDao.class)
    List<ScheduleDict> listDictByType(Integer type) {
        if (DictType.DATA_CLEAR_NAME.type.equals(type)) {
            return JSON.parseArray("[{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"schedule_job\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1467,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"schedule_job_job\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1469,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"schedule_fill_data_job\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1471,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"schedule_engine_unique_sign\",\"dictValue\":\"{\\\"directDelete\\\":true,\\\"deleteDateConfig\\\":2}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1473,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"schedule_plugin_job_info\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1475,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"console_security_log\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1477,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"alert_record\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1479,\"isDeleted\":0,\"sort\":1,\"type\":8},{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"data_clear_name\",\"dictName\":\"alert_content\",\"dictValue\":\"{\\\"deleteDateConfig\\\":30,\\\"clearDateConfig\\\":1}\",\"gmtCreate\":1625455356000,\"gmtModified\":1625455356000,\"id\":1481,\"isDeleted\":0,\"sort\":1,\"type\":8}]\n",
                    ScheduleDict.class);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = JobChainParamCleaner.class)
    public void cleanUpCycAndPatchJobOutputParams() {
        return;
    }

    @MockInvoke(targetClass = DataSource.class)
    Connection getConnection() throws SQLException {
        MockConnection mockConnection = new MockConnection();
        return new MockConnection();
    }

    @MockInvoke(targetClass = Connection.class)
    Statement createStatement() throws SQLException {
        MockConnection mockConnection = new MockConnection();
        MockStatement mockStatement = new MockStatement(mockConnection);
        return mockStatement;
    }

    @MockInvoke(targetClass = Statement.class)
    ResultSet executeQuery(String sql) throws SQLException {
        MockConnection mockConnection = new MockConnection();
        MockStatement mockStatement = new MockStatement(mockConnection);
        MockResultSet mockResultSet = new MockResultSet(mockStatement);
        if ("show tables like 'schedule_job'".equals(sql)) {
            mockResultSet.getRows().add(new Object[]{"schedule_job"});
            MockResultSet rs = new MockResultSet(mockStatement, mockResultSet.getRows());
            return rs;
        }
        if ("select min(id), max(id) from schedule_job".equals(sql)) {
            mockResultSet.getRows().add(new Object[]{1L, 2000L});
            MockResultSet rs = new MockResultSet(mockStatement, mockResultSet.getRows());
            return rs;
        }
        if (StringUtils.isNotEmpty(sql) && sql.contains("select count(*) from schedule_job where is_deleted != 2")) {
            mockResultSet.getRows().add(new Object[]{1L});
            MockResultSet rs = new MockResultSet(mockStatement, mockResultSet.getRows());
            return rs;
        }
        return mockResultSet;
    }

    @MockInvoke(targetClass = Statement.class)
    int executeUpdate(String sql) throws SQLException {
        return 1;
    }
}