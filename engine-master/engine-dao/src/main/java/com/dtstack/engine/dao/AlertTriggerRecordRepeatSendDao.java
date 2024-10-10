package com.dtstack.engine.dao;

import com.dtstack.engine.po.AlertTriggerRecordRepeatSend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/6/2 4:21 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlertTriggerRecordRepeatSendDao {


    List<AlertTriggerRecordRepeatSend> selectByJobIdAndRuleId(@Param("jobId") String jobId, @Param("ruleId") Long ruleId);

    Integer insert(AlertTriggerRecordRepeatSend alertTriggerRecordRepeatSend);

}
