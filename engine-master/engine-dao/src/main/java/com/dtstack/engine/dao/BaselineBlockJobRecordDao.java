package com.dtstack.engine.dao;

import com.dtstack.engine.po.BaselineBlockJobRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/6/2 9:58 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface BaselineBlockJobRecordDao {

    Integer insert(BaselineBlockJobRecord record);

    Integer deleteByBaselineJobId(@Param("baselineJobId") Long baselineJobId);

    List<BaselineBlockJobRecord> selectByBaselineJobId(@Param("baselineJobId") Long baselineJobId);

}
