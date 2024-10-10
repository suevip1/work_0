package com.dtstack.engine.master.scheduler;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.mockcontainer.impl.JobGraphBuilderMock;
import com.dtstack.engine.master.vo.AlertConfigVO;
import com.dtstack.engine.master.vo.NotifyMethodVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 9:45 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobGraphBuilderMock.class)
public class JobGraphBuilderTest {

    JobGraphBuilder jobGraphBuilder = new JobGraphBuilder();

   @Test
    public void paramReplaceTest() throws Exception {

        String triggerDay1 = "2020-06-05";
        jobGraphBuilder.buildTaskJobGraph(triggerDay1);
    }

    @Test
    public void saveJobGraphTest() throws Exception {

        String triggerDay1 = "2020-06-05";
        jobGraphBuilder.saveJobGraph(triggerDay1);
    }

    @Test
    public void exceptionNotifyTest() throws Exception {
        AlertConfigVO vo = new AlertConfigVO();
        ArrayList<NotifyMethodVO> methods = Lists.newArrayList();
        NotifyMethodVO vo1 = new NotifyMethodVO();
        methods.add(vo1);
        vo.setMethods(methods);

        String content = "";
        jobGraphBuilder.exceptionNotify(vo,content);
    }


    @Test
    public void buildFillDataJobGraph(){
        Set<String> all = getSet();
        Set<String> run = getSet();
        List<String> blackTaskKey = Lists.newArrayList();
        try {
            jobGraphBuilder.buildFillDataJobGraph("",0L,all,run,blackTaskKey,
                    "2020-06-05","00:00","23:59",0L,0L,
                    0L,0L,Boolean.TRUE, EScheduleType.FILL_DATA,1,1,new AtomicInteger());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<String> getSet(){
        Set<String> set = Sets.newHashSet();
        set.add(48371+"-"+1);
        set.add(48395+"-"+1);
        set.add(48373+"-"+1);
        set.add(49349+"-"+1);
        set.add(49355+"-"+1);
        set.add(49357+"-"+1);
        set.add(49347+"-"+1);
        set.add(49345+"-"+1);
        set.add(49359+"-"+1);
        set.add(48379+"-"+1);
        set.add(48375+"-"+1);
        set.add(48387+"-"+1);
        set.add(48383+"-"+1);
        set.add(49407+"-"+1);
        set.add(49409+"-"+1);
        set.add(49405+"-"+1);
        return set;
    }
}
