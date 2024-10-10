package com.dtstack.engine.master.sync.fill;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.sync.fill.strategy.AllSetStrategy;
import com.dtstack.engine.master.sync.fill.strategy.AllSetStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractFillDataTask implements FillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractFillDataTask.class);
    protected final ApplicationContext applicationContext;

    protected final ScheduleTaskShadeService scheduleTaskShadeService;

    protected final ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    protected final EnvironmentContext environmentContext;



    public AbstractFillDataTask(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scheduleTaskShadeService = applicationContext.getBean(ScheduleTaskShadeService.class);
        this.scheduleTaskTaskShadeService = applicationContext.getBean(ScheduleTaskTaskShadeService.class);
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
    }

    @Override
    public Set<String> getAllList(Set<String> run) {
        String strategy =  environmentContext.getAllSetStrategy();
        AllSetStrategy allSetStrategy = AllSetStrategyFactory.getAllSetStrategy(strategy, applicationContext);
        return allSetStrategy.getAllList(run);
    }

    public interface FillDataConst{
        String KEY_DELIMITER = "-";
    }
}
