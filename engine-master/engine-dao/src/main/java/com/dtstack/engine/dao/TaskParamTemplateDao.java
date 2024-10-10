package com.dtstack.engine.dao;

import com.dtstack.engine.po.TaskParamTemplate;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:44 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Deprecated
public interface TaskParamTemplateDao {

    TaskParamTemplate getByEngineTypeAndComputeType(@Param("engineType") int engineType, @Param("computeType") int computeType, @Param("taskType") int taskType);
}
