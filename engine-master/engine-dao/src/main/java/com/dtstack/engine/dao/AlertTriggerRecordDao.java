package com.dtstack.engine.dao;

import com.dtstack.engine.dto.AlertTriggerRecordModel;
import com.dtstack.engine.po.AlertTriggerRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 2:48 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlertTriggerRecordDao {

    Long countByModel(@Param("model") AlertTriggerRecordModel model);

    List<AlertTriggerRecord> selectByModel(@Param("model") AlertTriggerRecordModel model);

    Integer countAlarmByGmtCreate(@Param("time") Timestamp time, @Param("projectId") Long projectId, @Param("appType") Integer appType);

    AlertTriggerRecord selectLast(@Param("appType") Integer appType);

    Integer insert(AlertTriggerRecord alertTriggerRecord);
}
