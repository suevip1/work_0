package com.dtstack.engine.dao;

import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.dto.ConsoleJobRestrictQueryDTO;
import com.dtstack.engine.po.ConsoleJobRestrict;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 
 * {@link com.dtstack.engine.master.enums.JobRestrictStatusEnum}
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:03
 */
public interface ConsoleJobRestrictDao {
    /**
     * 新增规则
     * @param record
     * @return
     */
    int add(@Param("record")ConsoleJobRestrict record);

    ConsoleJobRestrict selectById(@Param("id")Long id);

    /**
     * 修改规则状态
     * @param ids
     * @param toStatus
     * @param fromStatus
     * @return
     */
    int changeStatus(@Param("ids")List<Long> ids, @Param("toStatus")Integer toStatus, @Param("fromStatus")List<Integer> fromStatus);

    int generalCount(@Param("model")ConsoleJobRestrictQueryDTO model);

    List<ConsoleJobRestrict> generalQuery(PageQuery<ConsoleJobRestrictQueryDTO> pageQuery);

    /**
     * 关闭规则
     * @param id
     * @param now
     * @return
     */
    boolean close(@Param("id") Long id, @Param("now")Date now);

    /**
     * 移除规则
     * @param id
     * @return
     */
    boolean remove(@Param("id")Long id);

    /**
     * 开启规则
     * @param id
     * @param now
     * @return
     */
    Boolean open(@Param("id") Long id, @Param("now")Date now);

    List<ConsoleJobRestrict> listByStatus(@Param("status")List<Integer> status);

    /**
     * 是否存在等待或运行中的规则
     * @return
     */
    Boolean existWaitOrRunning();

    /**
     * 所有过期规则
     * @return
     * @param now
     */
    List<ConsoleJobRestrict> listAllOverEndTimeRecords(@Param("now") Date now);

    /**
     * 所有将要超期但未超期的规则
     * @param now
     * @return
     */
    List<ConsoleJobRestrict> listToOverEndTimeRecords(@Param("now") Date now);

    /**
     * 修改超过计划结束时间的记录状态，考虑到 redis 可能被误用在不同的环境，所以要加时间限制
     * @param ids
     * @return
     */
    int changeOverEndTimeStatusByIdsAndNow(@Param("ids")List<Long> ids, @Param("now") Date now);

    /**
     * 修改超过计划结束时间的记录状态，由使用方保证 ids 是大于「计划结束时间」的记录
     * @param ids
     * @return
     */
    int changeOverEndTimeStatusByIds(@Param("ids")List<Long> ids);

    /**
     * 距离当前最近的开启的规则
     * @return
     * @param now
     */
    ConsoleJobRestrict findLatestWaitAndRunRestrict(@Param("now") Date now);

    /**
     * 更新生效时间
     * @param id
     * @param now
     * @return
     */
    boolean updateEffectiveTime(@Param("id") Long id, @Param("now")Date now);
}