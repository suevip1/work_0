package com.dtstack.engine.master.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.vo.datalife.DataLifeVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.cron.DataClearSchedule;
import com.dtstack.engine.master.enums.DataLifeEnum;
import com.dtstack.engine.master.enums.DictType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiuwei@dtstack.com
 **/
@Service
public class DataLifeService {

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private DataClearSchedule dataClearSchedule;

    public List<DataLifeVO> getDataLifeConfigPage() {
        // 暂时仅支持周期实例和补数据实例
        List<ScheduleDict> scheduleDicts = scheduleDictService.listByDictTypeNames(DictType.DATA_CLEAR_NAME, Lists.newArrayList(DataLifeEnum.INSTANCE.getMainDataTableName()));
        return scheduleDicts.stream().map(scheduleDict -> {
            DataLifeVO dataLifeVO = new DataLifeVO();
            dataLifeVO.setId(scheduleDict.getId());
            Integer softClearDay = getConfigValByKey(scheduleDict,DataClearSchedule.clearDateConfig);
            DataLifeEnum dataLifeEnum = DataLifeEnum.ofMainTableName(scheduleDict.getDictName());
            dataLifeVO.setDataName(dataLifeEnum.getDataName());
            dataLifeVO.setMainDataTableName(dataLifeEnum.getMainDataTableName());
            dataLifeVO.setSoftLifeDay(softClearDay);
            return dataLifeVO;
        }).collect(Collectors.toList());
    }

    private  Integer getConfigValByKey(ScheduleDict scheduleDict,String key) {
        JSONObject dataCleanConfig = JSONObject.parseObject(scheduleDict.getDictValue());
        return dataCleanConfig.getInteger(key);
    }



    @Transactional(rollbackFor = Exception.class)
    public void updateDataLifeConfig(Long id,String mainDataTableName,Integer softLifeDay) {

        if (id == null || softLifeDay <0 ){
            throw new RdosDefineException("非法参数!");
        }

        ScheduleDict scheduleDict = scheduleDictService.getOne(id);
        if (null == scheduleDict ){
            throw new RdosDefineException(String.format("not found %s",id));
        }
        DataLifeEnum dataLifeEnum = DataLifeEnum.ofMainTableName(mainDataTableName);

        if (!ObjectUtil.equals(scheduleDict.getType(), DictType.DATA_CLEAR_NAME.type) || !scheduleDict.getDictName().equals(mainDataTableName)){
            throw new RdosDefineException("非法操作!");
        }

        List<ScheduleDict> dataTables = scheduleDictService.listByDictTypeNames(DictType.DATA_CLEAR_NAME, dataLifeEnum.getDataTableNames());
        refreshDataCleanConfigVal(dataTables,DataClearSchedule.clearDateConfig,softLifeDay);
        for (ScheduleDict dataTable : dataTables) {
            scheduleDictService.updateScheduleDict(dataTable);
        }
    }

    private void refreshDataCleanConfigVal(List<ScheduleDict> scheduleDicts,String key,Object val){
        if (CollectionUtils.isEmpty(scheduleDicts)){
            return;
        }
        for (ScheduleDict scheduleDict : scheduleDicts) {
            JSONObject dataCleanConfig = JSONObject.parseObject(scheduleDict.getDictValue());
            dataCleanConfig.put(key,val);
            scheduleDict.setDictValue(JSONObject.toJSONString(dataCleanConfig));
        }

    }

    public void cleanDataManual(){
        dataClearSchedule.process();
    }
}
