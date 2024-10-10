package com.dtstack.engine.dao;

import com.dtstack.engine.po.AlertTriggerRecordReceive;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 2:48 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlertTriggerRecordReceiveDao {

    List<AlertTriggerRecordReceive> selectRecordUserIdByReceiverIds(@Param("gmtCreate") Timestamp gmtCreate);

    Integer selectCountByReceiverId(@Param("receiverId") Long receiverId, @Param("gmtCreate") Timestamp gmtCreate, @Param("appType") Integer appType,
                                    @Param("projectId") Long projectId);

    List<AlertTriggerRecordReceive> selectRecordNameByReceiverIds(@Param("ids") List<Long> ids);

    Integer batchInsert(@Param("alertTriggerRecordReceives") List<AlertTriggerRecordReceive> alertTriggerRecordReceives);
}
