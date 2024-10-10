package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamCleaner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:04 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AppReadyListenerMock {

     @MockInvoke(targetClass = ComponentService.class)
     public void clearConfigCache() {
     }

     @MockInvoke(targetClass = JobChainParamCleaner.class)
     public void scheduledCleanNotUsedOutputParams() {
     }


}
