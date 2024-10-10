package com.dtstack.engine.master.graph.adapter;

import com.dtstack.dtcenter.loader.utils.AssertUtils;
import com.dtstack.engine.api.domain.ScheduleTaskRefShade;
import com.dtstack.engine.master.graph.AbstractDirectGraphSide;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * {@link ScheduleTaskRefShade} 的有向图-边适配器
 *
 * @author leon
 * @date 2022-08-02 15:41
 **/
public class ScheduleTaskRefShadeGraphSideAdapter extends AbstractDirectGraphSide<String, Long> {

    private final ScheduleTaskRefShade scheduleTaskRefShade;

    public ScheduleTaskRefShadeGraphSideAdapter(ScheduleTaskRefShade scheduleTaskRefShade) {
        AssertUtils.notNull(scheduleTaskRefShade, "task ref is null");
        this.scheduleTaskRefShade = scheduleTaskRefShade;
    }

    @Override
    public String val() {
        return scheduleTaskRefShade.getTaskKey();
    }

    @Override
    public String parent() {
        return scheduleTaskRefShade.getRefTaskKey();
    }

    @Override
    public Long id() {
        return scheduleTaskRefShade.getTaskId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ScheduleTaskRefShadeGraphSideAdapter) {
            ScheduleTaskRefShadeGraphSideAdapter another = (ScheduleTaskRefShadeGraphSideAdapter) obj;
            // 比较 taskKey,parentTaskKey 是否都一样 @_@
            return (Objects.equals(this.val(), another.val())) && (Objects.equals(this.parent(), another.parent()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(val(), parent());
    }


    public static List<ScheduleTaskRefShadeGraphSideAdapter> build(List<ScheduleTaskRefShade> scheduleTaskRefShades) {
        List<ScheduleTaskRefShadeGraphSideAdapter> adapters = new ArrayList<>();
        if (CollectionUtils.isEmpty(scheduleTaskRefShades)) {
            return adapters;
        }
        scheduleTaskRefShades.forEach(refShade -> {
            adapters.add(new ScheduleTaskRefShadeGraphSideAdapter(refShade));
        });

        return adapters;
    }

    public static List<ScheduleTaskRefShade> extract(Collection<ScheduleTaskRefShadeGraphSideAdapter> adapters) {
        List<ScheduleTaskRefShade> shades = new ArrayList<>();
        if (CollectionUtils.isEmpty(adapters)) {
            return shades;
        }
        adapters.forEach(
                (ScheduleTaskRefShadeGraphSideAdapter adapter) -> {
                    shades.add(adapter.scheduleTaskRefShade);
                }
        );
        return shades;
    }

}
