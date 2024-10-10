package com.dtstack.engine.master.enums;

import com.dtstack.schedule.common.enums.DataSourceType;

/**
 * 这里有坑：val 枚举值必须跟 {@link DataSourceType} 一致，否则
 * ClusterService#componentInfo 方法中的 AccountTenantDao.getByUserIdAndTenantIdAndEngineType
 * 会查询不到值
 */
public enum AccountType {
    TiDB(31),
    Oracle(2),
    GREENPLUM6(36),
    ADB_POSTGREPSQL(54),

    OUSHUDB(113),
    //非数据源账号体系从201开始递增
    LDAP(201),
    TRINO(59),
    LIBRA(21),
    MySQL(1),
    SQLServer(3),
    SAP_HANA1(76),
    STARROCKS(91),
    HASHDATA(104),
    INCEPTOR_SQL(52),
    ;

    AccountType(int val) {
        this.val = val;
    }

    private int val;

    public int getVal() {
        return this.val;
    }
}
