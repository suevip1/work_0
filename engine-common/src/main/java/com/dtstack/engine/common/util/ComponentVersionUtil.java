package com.dtstack.engine.common.util;

import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 组件版本信息
 * @author xinge
 */
public class ComponentVersionUtil {

    /**
     * 没有版本的任务 - 组件 映射
     */
    public static final Set<Integer> NO_VERSION_SET;
    /**
     * 存在版本的 任务 - 组件映射
     */
    public static final Map<Integer,EComponentType> TASK_COMPONENT;
    /**
     * 多版本的组件
     */
    public static final Set<Integer> VERSION_COMPONENT;
    /**
     * 不允许修改对应关系
     */
    static {
        NO_VERSION_SET = Collections.unmodifiableSet(initUnVersionComponent());
        TASK_COMPONENT = Collections.unmodifiableMap(initComponent());
        VERSION_COMPONENT = Collections.unmodifiableSet(initVersionComponent());
    }

    private static Set<Integer> initVersionComponent(){
        Set<Integer> set = new HashSet<>(2);
        set.add(EComponentType.FLINK.getTypeCode());
        set.add(EComponentType.SPARK.getTypeCode());
        return set;
    }

    private static Set<Integer> initUnVersionComponent(){
        Set<Integer> set=new HashSet<>(4);
        set.add(EScheduleJobType.VIRTUAL.getType());
        set.add(EScheduleJobType.WORK_FLOW.getType());
        set.add(EScheduleJobType.KYLIN_CUBE.getType());
        set.add(EScheduleJobType.HADOOP_MR.getType());
        set.add(EScheduleJobType.NOT_DO_TASK.getType());
        set.add(EScheduleJobType.KINGBASE.getType());
        set.add(EScheduleJobType.NOTEBOOK.getType());
        set.add(EScheduleJobType.ALGORITHM_LAB.getType());
        set.add(EScheduleJobType.ANALYTICDB_FOR_PG.getType());
        set.add(EScheduleJobType.MYSQL.getType());
        set.add(EScheduleJobType.SQL_SERVER.getType());
        set.add(EScheduleJobType.DB2.getType());
        set.add(EScheduleJobType.OCEANBASE.getType());
        return set;
    }
    private static Map<Integer,EComponentType> initComponent(){
        Map<Integer,EComponentType> map=new HashMap<>(32);
        // Flink
        map.put(EScheduleJobType.SYNC.getType(),EComponentType.FLINK);
        // Spark
        map.put(EScheduleJobType.SPARK_SQL.getType(),EComponentType.SPARK);
        map.put(EScheduleJobType.SPARK.getType(),EComponentType.SPARK);
        map.put(EScheduleJobType.SPARK_PYTHON.getType(),EComponentType.SPARK);
        // R
        // Deep learning
        // Shell
        map.put(EScheduleJobType.SHELL.getType(),EComponentType.DT_SCRIPT);
        // ML_Lib

        // DtScript
        map.put(EScheduleJobType.PYTHON.getType(),EComponentType.DT_SCRIPT);
        map.put(EScheduleJobType.SHELL.getType(),EComponentType.DT_SCRIPT);
        // CarbonData ThriftServer
        map.put(EScheduleJobType.CARBON_SQL.getType(),EComponentType.CARBON_DATA);
        // LibrA SQL
        map.put(EScheduleJobType.LIBRA_SQL.getType(),EComponentType.LIBRA_SQL);
        // TiDB SQL
        map.put(EScheduleJobType.TIDB_SQL.getType(),EComponentType.TIDB_SQL);
        // Oracle SQL
        map.put(EScheduleJobType.ORACLE_SQL.getType(),EComponentType.ORACLE_SQL);
        // HiveServer
        map.put(EScheduleJobType.HIVE_SQL.getType(),EComponentType.HIVE_SERVER);
        // Impala SQL
        map.put(EScheduleJobType.IMPALA_SQL.getType(),EComponentType.IMPALA_SQL);
        // Greenplum SQL
        map.put(EScheduleJobType.GREENPLUM_SQL.getType(),EComponentType.GREENPLUM_SQL);
        // Presto
        map.put(EScheduleJobType.PRESTO_SQL.getType(),EComponentType.PRESTO_SQL);
        // Learning
        map.put(EScheduleJobType.TENSORFLOW_1_X.getType(),EComponentType.LEARNING);
        map.put(EScheduleJobType.KERAS.getType(),EComponentType.LEARNING);
        map.put(EScheduleJobType.PYTORCH.getType(),EComponentType.LEARNING);
        map.put(EScheduleJobType.INCEPTOR_SQL.getType(),EComponentType.INCEPTOR_SQL);
        map.put(EScheduleJobType.SHELL_ON_AGENT.getType(),EComponentType.DTSCRIPT_AGENT);
        map.put(EScheduleJobType.FLINK_SQL.getType(),EComponentType.FLINK);
        map.put(EScheduleJobType.TRINO.getType(), EComponentType.TRINO_SQL);
        map.put(EScheduleJobType.DATA_COLLECTION.getType(), EComponentType.FLINK);
        map.put(EScheduleJobType.TENSORFLOW_2_X.getType(), EComponentType.TONY);
        map.put(EScheduleJobType.MYSQL.getType(), EComponentType.MYSQL);

        return map;
    }




    public static String getComponentVersion(Map<Integer,String > componentVersionMap, EComponentType componentType){
        return Objects.isNull(componentVersionMap)?null:componentVersionMap.get(componentType.getTypeCode());
    }

    public static String getComponentVersion(Map<Integer,String > componentVersionMap, Integer componentTypeCode){
        return Objects.isNull(componentVersionMap)?null:componentVersionMap.get(componentTypeCode);
    }

    public static boolean isMultiVersionComponent(Integer componentTypeCode){
        if (VERSION_COMPONENT.contains(componentTypeCode)){
            return true;
        }
        return false;
    }

    public static String getComponentVersion(String componentVersion){
        if (StringUtils.isBlank(componentVersion)){
            return null;
        }
        return componentVersion;
    }



    public static String formatMultiVersion(Integer componentCode,String componentVersion){
        return isMultiVersionComponent(componentCode)? componentVersion : null;
    }


}
