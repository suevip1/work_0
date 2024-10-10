package com.dtstack.engine.master.graph;

import com.dtstack.engine.api.domain.ScheduleTaskRefShade;
import com.dtstack.engine.master.graph.adapter.ScheduleTaskRefShadeGraphSideAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GenericDirectedCycleJudgeTest {

    @Test
    public void isCycle0() {

        ScheduleTaskRefShadeGraphSideAdapter ba = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("b","a"));
        ScheduleTaskRefShadeGraphSideAdapter cb = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("c","b"));
        ScheduleTaskRefShadeGraphSideAdapter dc = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("d","c"));
        ScheduleTaskRefShadeGraphSideAdapter ed = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("e","d"));
        ScheduleTaskRefShadeGraphSideAdapter fe = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("f","e"));

        ScheduleTaskRefShadeGraphSideAdapter df = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("d","f"));


        Map<String, ScheduleTaskRefShadeGraphSideAdapter> parentDb = new HashMap<>();
        parentDb.put("b",ba);
        parentDb.put("c",cb);
        parentDb.put("d",dc);
        parentDb.put("e",ed);
        parentDb.put("f",fe);

        Map<String, ScheduleTaskRefShadeGraphSideAdapter> childDb = new HashMap<>();
        childDb.put("a",ba);
        childDb.put("b",cb);
        childDb.put("c",dc);
        childDb.put("d",ed);
        childDb.put("e",fe);

        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList0 = new ArrayList<>();
        adapterList0.add(ba);
        adapterList0.add(df);


        Function<List<String>, List<ScheduleTaskRefShadeGraphSideAdapter>> parentProvider =
                scheduleTaskRefGraphSideAdapters -> {
                    List<ScheduleTaskRefShadeGraphSideAdapter> res = new ArrayList<>();
                    for (String adapter: scheduleTaskRefGraphSideAdapters) {
                        res.add(parentDb.get(adapter));
                    }
                    return res;
                };

        Function<List<String>, List<ScheduleTaskRefShadeGraphSideAdapter>> childProvider =
                scheduleTaskRefGraphSideAdapters -> {
                    List<ScheduleTaskRefShadeGraphSideAdapter> res = new ArrayList<>();
                    for (String adapter: scheduleTaskRefGraphSideAdapters) {
                        res.add(childDb.get(adapter));
                    }
                    return res;
                };

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge0 =
                new GenericDirectedCycleJudge<>(adapterList0);


        Assert.assertTrue(judge0.isCycle(parentProvider,childProvider));





        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList1 = new ArrayList<>();
        adapterList1.add(ba);

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge1 =
                new GenericDirectedCycleJudge<>(adapterList1);

        Assert.assertFalse(judge1.isCycle(parentProvider,childProvider));
    }


    @Test
    public void isCycle1() {
        ScheduleTaskRefShadeGraphSideAdapter ed = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("e","d"));
        ScheduleTaskRefShadeGraphSideAdapter ec = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("e","c"));
        ScheduleTaskRefShadeGraphSideAdapter eg = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("e","g"));
        ScheduleTaskRefShadeGraphSideAdapter db = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("d","b"));
        ScheduleTaskRefShadeGraphSideAdapter ca = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("c","a"));
        ScheduleTaskRefShadeGraphSideAdapter gc = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("g","c"));
        ScheduleTaskRefShadeGraphSideAdapter ba = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("b","a"));

        ScheduleTaskRefShadeGraphSideAdapter ab = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("a","b"));
        ScheduleTaskRefShadeGraphSideAdapter dc = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("d","c"));

        ScheduleTaskRefShadeGraphSideAdapter ce = new ScheduleTaskRefShadeGraphSideAdapter(new ScheduleTaskRefShade("c","e"));




        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList0 = new ArrayList<>();
        adapterList0.add(ed);
        adapterList0.add(ec);
        adapterList0.add(eg);
        adapterList0.add(db);
        adapterList0.add(ca);
        adapterList0.add(gc);
        adapterList0.add(ba);

        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList1 = new ArrayList<>();
        adapterList1.addAll(adapterList0);
        adapterList1.add(ab);


        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList2 = new ArrayList<>();
        adapterList2.addAll(adapterList0);
        adapterList2.add(dc);

        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList3 = new ArrayList<>();
        adapterList3.addAll(adapterList2);
        adapterList3.add(ce);

        // 重复值
        List<ScheduleTaskRefShadeGraphSideAdapter> adapterList4 = new ArrayList<>();
        adapterList4.addAll(adapterList2);
        adapterList4.add(dc);
        adapterList4.add(ed);

        Map<String, ScheduleTaskRefShadeGraphSideAdapter> parentDb = new HashMap<>();
        parentDb.put("e",ed);
        parentDb.put("d",db);
        parentDb.put("b",ba);

        Map<String, ScheduleTaskRefShadeGraphSideAdapter> childDb = new HashMap<>();
        childDb.put("a",ba);
        childDb.put("b",db);
        childDb.put("d",ed);


        Function<List<String>, List<ScheduleTaskRefShadeGraphSideAdapter>> parentProvider =
                scheduleTaskRefGraphSideAdapters -> {
                    List<ScheduleTaskRefShadeGraphSideAdapter> res = new ArrayList<>();
                    for (String adapter: scheduleTaskRefGraphSideAdapters) {
                        res.add(parentDb.get(adapter));
                    }
                    return res;
                };

        Function<List<String>, List<ScheduleTaskRefShadeGraphSideAdapter>> childProvider =
                scheduleTaskRefGraphSideAdapters -> {
                    List<ScheduleTaskRefShadeGraphSideAdapter> res = new ArrayList<>();
                    for (String adapter: scheduleTaskRefGraphSideAdapters) {
                        res.add(childDb.get(adapter));
                    }
                    return res;
                };

        Function<List<String>,List<ScheduleTaskRefShadeGraphSideAdapter>> empty = strings -> null;

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge0 =
                new GenericDirectedCycleJudge<>(adapterList0);

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge1 =
                new GenericDirectedCycleJudge<>(adapterList1);

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge2 =
                new GenericDirectedCycleJudge<>(adapterList2);

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge3 =
                new GenericDirectedCycleJudge<>(adapterList3);

        DirectedCycleJudge<String,Long, ScheduleTaskRefShadeGraphSideAdapter> judge4 =
                new GenericDirectedCycleJudge<>(adapterList4);


        Assert.assertFalse(judge0.isCycle(empty,empty));
        Assert.assertFalse(judge0.isCycle(parentProvider,childProvider));

        Assert.assertTrue(judge1.isCycle(empty,empty));
        Assert.assertTrue(judge1.isCycle(parentProvider,childProvider));

        Assert.assertFalse(judge2.isCycle(empty,empty));
        Assert.assertFalse(judge2.isCycle(parentProvider,childProvider));

        Assert.assertTrue(judge3.isCycle(empty,empty));
        Assert.assertTrue(judge3.isCycle(parentProvider,childProvider));

        Assert.assertFalse(judge4.isCycle(empty,empty));
        Assert.assertFalse(judge4.isCycle(parentProvider,childProvider));

    }
}