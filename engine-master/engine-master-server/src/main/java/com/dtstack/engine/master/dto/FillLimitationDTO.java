package com.dtstack.engine.master.dto;

/**
 * @Auther: dazhi
 * @Date: 2023-11-27 16:15
 * @Email: dazhi@dtstack.com
 * @Description: FillLimitationDTO
 */
public class FillLimitationDTO {

    private Long maxOperate;

    private Integer maxParallelNum;


    public Long getMaxOperate() {
        return maxOperate;
    }

    public void setMaxOperate(Long maxOperate) {
        this.maxOperate = maxOperate;
    }

    public Integer getMaxParallelNum() {
        return maxParallelNum;
    }

    public void setMaxParallelNum(Integer maxParallelNum) {
        this.maxParallelNum = maxParallelNum;
    }

    public static FillLimitationDTO build() {
        FillLimitationDTO fillLimitationDTO = new FillLimitationDTO();
        fillLimitationDTO.maxOperate = 5000L;
        fillLimitationDTO.maxParallelNum = 3;
        return fillLimitationDTO;
    }
}
