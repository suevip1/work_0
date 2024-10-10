package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.cron.constant.DataLifeTable;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author jiuwei@dtstack.com
 **/
public enum DataLifeEnum {
    /**
     * 周期实例
     */
    INSTANCE("周期实例,补数据实例", DataLifeTable.SCHEDULE_JOB, Lists.newArrayList(
            DataLifeTable.SCHEDULE_JOB,
            DataLifeTable.SCHEDULE_JOB_EXPAND,
            DataLifeTable.SCHEDULE_JOB_JOB,
            DataLifeTable.SCHEDULE_FILL_DATA_JOB,
            DataLifeTable.SCHEDULE_JOB_GANTT_CHART,
            DataLifeTable.SCHEDULE_JOB_CHAIN_OUTPUT_PARAM,
            DataLifeTable.SCHEDULE_PLUGIN_JOB_INFO));

    /**
     * 展示的数据名
     */
    private final String dataName;

    /**
     * 主表
     */
    private final String mainDataTableName;

    /**
     * 副表
     */
    private final List<String> dataTableNames;

    DataLifeEnum(String dataName, String mainDataTableName, List<String> dataTableNames) {
        this.dataName = dataName;
        this.mainDataTableName = mainDataTableName;
        this.dataTableNames = dataTableNames;
    }

    public String getDataName() {
        return dataName;
    }

    public List<String> getDataTableNames() {
        return dataTableNames;
    }

    public String getMainDataTableName() {
        return mainDataTableName;
    }

    public static boolean isDataLifeMainTable(String tableName){
        for (DataLifeEnum value : DataLifeEnum.values()) {
            if (value.getMainDataTableName().equals(tableName)){
                return true;
            }
        }
        return false;
    }

    public static DataLifeEnum ofMainTableName(String mainDataTableName){
        for (DataLifeEnum value : DataLifeEnum.values()) {
            if (value.getMainDataTableName().equals(mainDataTableName)){
                return value;
            }
        }
        throw new RdosDefineException(String.format("%s不存在",mainDataTableName));
    }
}
