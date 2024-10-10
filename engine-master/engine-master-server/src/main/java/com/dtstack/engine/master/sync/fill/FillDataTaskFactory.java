package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.api.vo.schedule.job.FillDataConditionInfoVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataRelyInfo;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/11/15 5:16 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class FillDataTaskFactory {

    private final ApplicationContext applicationContext;
    private final List<TaskKeyVO> chooseTaskInfo;
    private final FillDataConditionInfoVO fillDataConditionInfoVO;
    private final FillDataRelyInfo fillDataRelyInfo;
    private final List<TaskKeyVO> whitelist;

    private FillDataTaskFactory(ApplicationContext applicationContext,
                               List<TaskKeyVO> chooseTaskInfo,
                               FillDataConditionInfoVO fillDataConditionInfoVO,
                               FillDataRelyInfo fillDataRelyInfo,
                               List<TaskKeyVO> whitelist) {
        this.applicationContext = applicationContext;
        this.chooseTaskInfo = chooseTaskInfo;
        this.fillDataConditionInfoVO = fillDataConditionInfoVO;
        this.fillDataRelyInfo = fillDataRelyInfo;
        this.whitelist = whitelist;
    }

    public static FillDataTaskFactory getInstance(ApplicationContext applicationContext,
                                                   List<TaskKeyVO> chooseTaskInfo,
                                                   FillDataConditionInfoVO fillDataConditionInfoVO,
                                                   FillDataRelyInfo fillDataRelyInfo,
                                                   List<TaskKeyVO> whitelist) {
        return new FillDataTaskFactory(applicationContext, chooseTaskInfo,fillDataConditionInfoVO,fillDataRelyInfo,whitelist);
    }

    public FillDataTask getFillDataTask(Integer fillDataType) {
        if (FillDataTypeEnum.BATCH.getType().equals(fillDataType) || FillDataTypeEnum.MANUAL.getType().equals(fillDataType)) {
            return new BatchEnhanceFillDataTask(applicationContext, chooseTaskInfo,whitelist);
        } else if (FillDataTypeEnum.CONDITION_PROJECT_TASK.getType().equals(fillDataType)) {
            return new ConditionEnhanceFillDataTask(applicationContext, fillDataConditionInfoVO,whitelist);
        } else if (FillDataTypeEnum.RELY.getType().equals(fillDataType)) {
            return new RelyEnhanceFillDataTask(applicationContext, fillDataRelyInfo,whitelist);
        }
        return null;
    }

}
