package com.dtstack.engine.api.dto;

import java.util.List;

/**
 * @author leon
 * @date 2022-09-19 11:04
 **/
public class ClusterPageQueryDTO {

    private String fuzzyClusterName;

    private List<Long> tenantIds;

    public String getFuzzyClusterName() {
        return fuzzyClusterName;
    }

    public void setFuzzyClusterName(String fuzzyClusterName) {
        this.fuzzyClusterName = fuzzyClusterName;
    }


    public static final class ClusterPageQueryDTOBuilder {
        private String fuzzyClusterName;
        private List<Long> tenantIds;

        private ClusterPageQueryDTOBuilder() {
        }

        public static ClusterPageQueryDTOBuilder aClusterPageQueryDTO() {
            return new ClusterPageQueryDTOBuilder();
        }

        public ClusterPageQueryDTOBuilder fuzzyClusterName(String fuzzyClusterName) {
            this.fuzzyClusterName = fuzzyClusterName;
            return this;
        }

        public ClusterPageQueryDTOBuilder tenantIds(List<Long> tenantIds) {
            this.tenantIds = tenantIds;
            return this;
        }

        public ClusterPageQueryDTO build() {
            ClusterPageQueryDTO clusterPageQueryDTO = new ClusterPageQueryDTO();
            clusterPageQueryDTO.setFuzzyClusterName(fuzzyClusterName);
            clusterPageQueryDTO.tenantIds = this.tenantIds;
            return clusterPageQueryDTO;
        }
    }
}
