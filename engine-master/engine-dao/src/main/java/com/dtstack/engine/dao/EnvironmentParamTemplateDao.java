package com.dtstack.engine.dao;

import com.dtstack.engine.po.EnvironmentParamTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnvironmentParamTemplateDao {


    List<EnvironmentParamTemplate> getParamsByTaskAndVersion(@Param("taskVersion") String taskVersion,@Param("taskType") int taskType,@Param("appType") int appType);

}
