package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 9:20 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Deprecated
public enum DbType {

    Oracle(2),
    TiDB(31),
    GREENPLUM6(36),
    ANALYTICDB_FOR_PG(54),
    MYSQL(1),
    DB2(19),
    SQL_SERVER(3),
    OCEANBASE(55);

    private int typeCode;

    DbType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
