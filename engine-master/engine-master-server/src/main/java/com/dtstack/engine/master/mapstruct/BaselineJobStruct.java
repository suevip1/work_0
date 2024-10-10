package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.BaselineJobConditionModel;
import com.dtstack.engine.api.vo.alert.BaselineBlockJobRecordVO;
import com.dtstack.engine.api.vo.alert.BaselineJobConditionVO;
import com.dtstack.engine.master.dto.BaselineJobJobDTO;
import com.dtstack.engine.po.BaselineBlockJobRecord;
import com.dtstack.engine.po.BaselineJobJob;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 4:13 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface BaselineJobStruct {


    BaselineJobConditionModel toBaselineJobConditionModel(BaselineJobConditionVO vo);

    BaselineJobJob toBaselineJobJob(BaselineJobJobDTO baselineJobJob);

    List<BaselineBlockJobRecordVO> baselineBlockJob(List<BaselineBlockJobRecord> baselineBlockJobRecordList);
}
