package com.dtstack.engine.dao;

import com.dtstack.engine.po.ScheduleSqlTextTemp;

/**
 * @Author: ZYD
 * Date: 2021/4/23 10:30
 * Description: 临时运行sqlText dao接口
 * @since 1.0.0
 */
public interface ScheduleSqlTextTempDao {


    void insert(ScheduleSqlTextTemp sqlTextTemp);

    ScheduleSqlTextTemp selectByJobId(String jobId);
}
