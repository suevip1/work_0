package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.dao.ScheduleTaskTagDao;
import com.dtstack.engine.po.ScheduleTaskTag;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2023/2/23 1:43 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskTagService {

    @Autowired
    private ScheduleTaskTagDao scheduleTaskTagDao;


    public Integer addOrUpdateTaskTag(Long taskId, Integer appType, List<Long> tagList) {
        scheduleTaskTagDao.deleteByTaskIdAndAppType(taskId, appType);

        if (CollectionUtils.isNotEmpty(tagList)) {
            List<ScheduleTaskTag> scheduleTaskTags = Lists.newArrayList();
            for (Long tagId : tagList) {
                ScheduleTaskTag scheduleTaskTag = new ScheduleTaskTag();
                scheduleTaskTag.setTaskId(taskId);
                scheduleTaskTag.setTagId(tagId);
                scheduleTaskTag.setAppType(appType);
                scheduleTaskTag.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
                scheduleTaskTags.add(scheduleTaskTag);
            }

            return scheduleTaskTagDao.insertBatch(scheduleTaskTags);
        }

        return 0;
    }

    public Map<Integer, List<Long>> findTaskByTagIds(List<Long> tagIds) {
        List<ScheduleTaskTag> scheduleTaskTags = scheduleTaskTagDao.findTaskByTagIds(tagIds,null);
        return scheduleTaskTags.stream()
                .collect(Collectors.groupingBy(ScheduleTaskTag::getAppType,
                        Collectors.mapping(ScheduleTaskTag::getTaskId,Collectors.toList())));
    }

    public List<ScheduleTaskTag> findTagsByTagIds(List<Long> tagIds,Integer appType) {
        return scheduleTaskTagDao.findTaskByTagIds(tagIds,appType);
    }

    public List<ScheduleTaskTag> findTagByTaskIds(Set<Long> taskIds, Integer appType) {
        if (CollectionUtils.isNotEmpty(taskIds) && appType != null) {
            return scheduleTaskTagDao.findTagByTaskIds(taskIds,appType);
        }
        return Lists.newArrayList();
    }

    public Integer deleteTagByTagId(Long tagId) {
        return scheduleTaskTagDao.deleteTagByTagId(tagId);
    }

    public List<Long> selectTagByTask(Long taskId, Integer appType) {
        List<ScheduleTaskTag> tagByTaskIds = scheduleTaskTagDao.findTagByTaskIds(Sets.newHashSet(taskId), appType);

        if (CollectionUtils.isNotEmpty(tagByTaskIds)) {
            return tagByTaskIds.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}
