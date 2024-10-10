package com.dtstack.engine.dao;

import com.dtstack.engine.po.AlertRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 2:30 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface AlertRuleDao {

    /**
     * 查询规则集合
     *
     * @param ids 规则id
     * @return
     */
    List<AlertRule> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据 key 查询规则
     *
     * @param key 规则 key
     * @return AlertRule
     */
    AlertRule selectByKey(@Param("key") String key);

    /**
     * 查询所有规则
     */
    List<AlertRule> selectAll();

}
