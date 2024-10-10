package com.dtstack.engine.master.utils;

import com.dtstack.engine.master.dto.FsyncSql;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * fsync util
 *
 * @author ：wangchuan
 * date：Created in 20:40 2023/2/9
 * company: www.dtstack.com
 */
@Component
public class FsyncUtil {
    private static Set<Integer> NEED_RESULT_TYPE;

    /**
     * 需要结果集的 SqlType 类型
     * @see com.dtstack.engine.api.enums.SqlType
     * @param needResultType
     */
    @Value("${fsync.need.result.type:44,49,171,172,173,174,175,176,180,177}")
    private void setNeedResultType(Set<Integer> needResultType) {
        NEED_RESULT_TYPE = Collections.unmodifiableSet(needResultType);
    }

    /**
     * 判断是否包含 select 语句
     *
     * @param singleFsyncSql 离线单条语句
     * @return 是否包含
     */
    public static boolean containSelect(FsyncSql singleFsyncSql) {
        return NEED_RESULT_TYPE.contains(singleFsyncSql.getSqlType())
                && StringUtils.isNotEmpty(singleFsyncSql.getSqlId());
    }
}
