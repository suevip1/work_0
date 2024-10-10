package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.master.enums.CloseRetryEnum;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 1:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BatchEnhanceFillDataTask extends WhiteAbstractFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(BatchEnhanceFillDataTask.class);

    private final List<TaskKeyVO> chooseTaskInfo;

    public BatchEnhanceFillDataTask(ApplicationContext applicationContext,List<TaskKeyVO> chooseTaskInfo,List<TaskKeyVO> whitelist) {
        super(applicationContext,whitelist);
        this.chooseTaskInfo = chooseTaskInfo;
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.BATCH;
    }

    @Override
    public Set<String> getRunList() {
        Set<String> runList = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(chooseTaskInfo)) {
            chooseTaskInfo.forEach(task -> runList.add(task.getTaskId() + FillDataConst.KEY_DELIMITER + task.getAppType()));
        }
        runList.addAll(addWhite(CloseRetryEnum.CLOSE.getType()));
        return runList;
    }


}
