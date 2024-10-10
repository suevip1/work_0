package com.dtstack.engine.master.scheduler;

import junit.framework.TestCase;
import org.junit.Assert;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledCallableStatement;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.PreparedStatementHolder;

import java.sql.*;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-01-11 17:31
 */
public class PoolableCallableStatementTest extends TestCase {

    private DruidDataSource dataSource = new DruidDataSource();
    protected DruidPooledConnection conn;
    protected MockCallableStatement raw;
    protected DruidPooledCallableStatement stmt;

    protected void setUp() throws Exception {
        MockConnection mockConn = new MockConnection();
        DruidConnectionHolder connHolder = new DruidConnectionHolder(dataSource, mockConn, 0);
        conn = new DruidPooledConnection(connHolder);
        raw = new MockCallableStatement(null, null);
        stmt = new DruidPooledCallableStatement(conn, new PreparedStatementHolder(new PreparedStatementKey("", null,
                null, 0, 0,
                0), raw)) {
            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }
                return new SQLException(error);
            }
        };
        Assert.assertEquals(0, raw.getOutParameters().size());
        stmt.registerOutParameter(1, Types.INTEGER);
        Assert.assertEquals(1, raw.getOutParameters().size());
        stmt.registerOutParameter(2, Types.DECIMAL, 10);
        Assert.assertEquals(2, raw.getOutParameters().size());
    }

    public void test_executeQuery_large() throws Exception {
        // int total = 1000 * 1000;
        int total = 50 * 50;
        for (int i = 0; i < total; ++i) {
            ResultSet rs = stmt.executeQuery();
            rs.close();
        }
    }

}