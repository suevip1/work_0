package com.dtstack.engine.master.jobdealer.cache;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.ShardCacheMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 8:23 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(ShardCacheMock.class)
public class ShardCacheTest {

    ShardCache shardCache = new ShardCache();

    @Test
    public void updateLocalMemTaskStatusTest(){
        shardCache.updateLocalMemTaskStatus("123", 5);
        shardCache.updateLocalMemTaskStatus("123", 5,(jobId)->{});
        shardCache.removeIfPresent("123");
    }


}
