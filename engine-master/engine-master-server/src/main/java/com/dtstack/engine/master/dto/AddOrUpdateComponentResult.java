package com.dtstack.engine.master.dto;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-01-10 17:20
 * 新增或更新某个组件后的结果，用于剥离推送业务中心等第三方交互
 */

public class AddOrUpdateComponentResult {
    /**
     * 集群
     */
    private Cluster cluster;

    /**
     * 是否更新，默认 false
     */
    private boolean isUpdate;

    /**
     * yarn 组件是否被刷新，默认 false
     */
    private boolean yarnVersionChanged;

    /**
     * 当前刷新的组件
     */
    private Component refreshComponent;

    /**
     * 当前刷新的 principals
     */
    private String principals;

    /**
     * 当前刷新的 principal
     */
    private String principal;

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isYarnVersionChanged() {
        return yarnVersionChanged;
    }

    public void setYarnVersionChanged(boolean yarnVersionChanged) {
        this.yarnVersionChanged = yarnVersionChanged;
    }

    public Component getRefreshComponent() {
        return refreshComponent;
    }

    public void setRefreshComponent(Component refreshComponent) {
        this.refreshComponent = refreshComponent;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public static final class AddOrUpdateComponentResultBuilder {
        private Cluster cluster;

        private boolean isUpdate;

        private boolean yarnVersionChanged;

        private Component refreshComponent;

        private String principals;

        private String principal;

        private AddOrUpdateComponentResultBuilder() {}

        public static AddOrUpdateComponentResultBuilder builder() {
            return new AddOrUpdateComponentResultBuilder();
        }

        public AddOrUpdateComponentResultBuilder cluster(Cluster cluster) {
            this.cluster = cluster;
            return this;
        }

        public AddOrUpdateComponentResultBuilder isUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
            return this;
        }

        public AddOrUpdateComponentResultBuilder yarnVersionChanged(boolean yarnVersionChanged) {
            this.yarnVersionChanged = yarnVersionChanged;
            return this;
        }

        public AddOrUpdateComponentResultBuilder refreshComponent(Component refreshComponent) {
            this.refreshComponent = refreshComponent;
            return this;
        }

        public AddOrUpdateComponentResultBuilder principals(String principals) {
            this.principals = principals;
            return this;
        }

        public AddOrUpdateComponentResultBuilder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public AddOrUpdateComponentResult build() {
            AddOrUpdateComponentResult result = new AddOrUpdateComponentResult();
            result.setCluster(this.cluster);
            result.setUpdate(this.isUpdate);
            result.setYarnVersionChanged(this.yarnVersionChanged);
            result.setRefreshComponent(this.refreshComponent);
            result.setPrincipals(this.principals);
            result.setPrincipal(this.principal);
            return result;
        }

    }
}