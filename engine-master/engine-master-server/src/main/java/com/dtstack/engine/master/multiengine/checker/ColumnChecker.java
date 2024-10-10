package com.dtstack.engine.master.multiengine.checker;

import com.dtstack.dtcenter.common.convert.load.SourceLoaderService;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.Column;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2023/2/17
 */
@Component
public class ColumnChecker {

    @Autowired
    private SourceLoaderService sourceLoaderService;

    private static List<Integer> SUPPORT_DATA_SOURCE = new ArrayList<>(DataSourceType.RDBM_S);

    static {
        SUPPORT_DATA_SOURCE.add(DataSourceType.Kudu.getVal());
        SUPPORT_DATA_SOURCE.add(DataSourceType.DORIS.getVal());
    }

    /**
     * 对于数据同步任务的临时运行/周期实例运行/补数据实例运行/手动实例运行前检查源表或目标表的表结构是否发生变更，如果有变更则任务提交失败
     * 临时运行在日志中打印变更内容
     *
     * @param columnList
     * @param datasourceId
     * @param table
     * @param dataSourceType
     * @param schema
     */
    public String checkColumn(List<Column> columnList, Long datasourceId, String table, String schema, Integer dataSourceType, boolean isReader) {
        if (!SUPPORT_DATA_SOURCE.contains(dataSourceType)) {
            return "";
        }
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(datasourceId);
        IClient client = ClientCache.getClient(dataSourceType);
        List<ColumnMetaDTO> columnMetaData = client.getColumnMetaData(sourceDTO, SqlQueryDTO.builder().tableName(table).schema(schema).build());
        Map<String, String> datasourceTableColumn = new HashMap<>(), taskTableColumn = new HashMap<>();
        for (ColumnMetaDTO columnMetaDatum : columnMetaData) {
            datasourceTableColumn.put(columnMetaDatum.getKey(), columnMetaDatum.getType());
        }
        for (Column column : columnList) {
            taskTableColumn.put(column.getName(), column.getType());
        }
        MapDifference<String, String> difference = Maps.difference(datasourceTableColumn, taskTableColumn);
        if (difference.entriesDiffering().isEmpty() && difference.entriesOnlyOnLeft().isEmpty() && difference.entriesOnlyOnRight().isEmpty()) {
            return "";
        }
        Map<String, String> newColumn = difference.entriesOnlyOnRight();
        String newColumnContent = MapUtils.isEmpty(newColumn) ? "" : "移除字段" + String.join(",", newColumn.keySet());
        Map<String, String> lostColumn = difference.entriesOnlyOnLeft();
        String lostColumnContent = MapUtils.isEmpty(lostColumn) ? "" : "新增字段" + String.join(",", lostColumn.keySet());
        Map<String, MapDifference.ValueDifference<String>> changeColumn = difference.entriesDiffering();
        String changeColumnMsg = MapUtils.isEmpty(changeColumn) ? "" : changeColumn.keySet().stream().map(column -> {
            String oldType = changeColumn.get(column).rightValue();
            String newType = changeColumn.get(column).leftValue();
            return column + "类型" + oldType + "变更为" + newType;
        }).collect(Collectors.joining());
        return String.format("%s表元数据发生变更(%s %s %s %s)", isReader ? "源" : "结果", table, newColumnContent, lostColumnContent, changeColumnMsg);
    }

}

