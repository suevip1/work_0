package com.dtstack.engine.master.sync.baseline;

import com.dtstack.engine.master.dto.BaselineBuildDTO;
import com.dtstack.engine.master.dto.BaselineTaskDTO;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 4:15 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineJobBuilder {

    /**
     * 生成策略
     */
    BaselineBuildDTO buildBaselineJob(String triggerDay, BaselineTaskDTO dto, Set<String> noExecCache, String cycTime);
}
