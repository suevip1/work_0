package com.dtstack.engine.api.vo.datalife;

/**
 * @author jiuwei@dtstack.com
 **/

public class DataLifeVO {
    private Long id;

    private String dataName;
    private String mainDataTableName;
    private Integer softLifeDay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataName() {
        return dataName;
    }

    public String getMainDataTableName() {
        return mainDataTableName;
    }

    public void setMainDataTableName(String mainDataTableName) {
        this.mainDataTableName = mainDataTableName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getSoftLifeDay() {
        return softLifeDay;
    }

    public void setSoftLifeDay(Integer softLifeDay) {
        this.softLifeDay = softLifeDay;
    }
}
