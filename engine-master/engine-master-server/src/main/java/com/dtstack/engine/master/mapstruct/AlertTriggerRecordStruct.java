package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.AlertTriggerRecord;
import com.dtstack.engine.dto.AlertTriggerRecordModel;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordConditionVO;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordPageVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 3:45 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface AlertTriggerRecordStruct {


    AlertTriggerRecordModel alertTriggerRecordModelToAlertTriggerRecordConditionVO(AlertTriggerRecordConditionVO vo);

    List<AlertTriggerRecordPageVO> recordsToPageVOs(List<AlertTriggerRecord> records);
}
