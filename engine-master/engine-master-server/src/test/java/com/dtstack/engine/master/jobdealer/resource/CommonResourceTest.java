package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.common.JobClient;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 8:58 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CommonResourceTest {

    CommonResource commonResource = new CommonResource();

    @Test
    public void newInstanceTest() {
        JobClient jobClient = new JobClient();
        jobClient.setEngineType("oceanbase");
        commonResource.newInstance(jobClient);

    }
}
