package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleProjectAccount;
import org.apache.ibatis.annotations.Param;

/**
 * 项目级绑定账号设置
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 15:20
 */
public interface ConsoleProjectAccountDao {
    int insert(@Param("record") ConsoleProjectAccount record);

    ConsoleProjectAccount selectById(@Param("id") Long id);

    boolean modify(@Param("record") ConsoleProjectAccount record);

    ConsoleProjectAccount findByProjectAndComponent(@Param("entity") ConsoleProjectAccount entity);
}