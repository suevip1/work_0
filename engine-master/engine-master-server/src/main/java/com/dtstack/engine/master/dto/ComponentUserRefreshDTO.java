package com.dtstack.engine.master.dto;

import com.alibaba.fastjson.JSON;
import com.dtstack.dtcenter.common.util.JsonUtils;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.po.ComponentUser;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-20 22:47
 */
public class ComponentUserRefreshDTO {
    /**
     * 新增的 componentUser
     */
    private List<ComponentUser> create;
    /**
     * 修改的 componentUser
     */
    private List<ComponentUser> update;
    /**
     * 修改为 default 的 componentUser
     */
    private List<ComponentUser> updateToDefault;
    /**
     * 删除的 componentUser
     */
    private List<ComponentUser> delete;

    private ComponentUserRefreshDTO(Builder builder) {
        this.create = builder.create;
        this.update = builder.update;
        this.updateToDefault = builder.updateToDefault;
        this.delete = builder.delete;
    }

    public List<ComponentUser> getCreate() {
        return create;
    }

    public List<ComponentUser> getUpdate() {
        return update;
    }

    public List<ComponentUser> getDelete() {
        return delete;
    }

    public List<ComponentUser> getUpdateToDefault() {
        return updateToDefault;
    }

    public String prettyLog() {
        return String.format("create:[%s], update:[%s], updateToDefault:[%s], delete:[%s]",
                JSON.toJSONString(this.create),
                JSON.toJSONString(this.update),
                JSON.toJSONString(this.updateToDefault),
                JSON.toJSONString(this.delete));
    }

    public static class Builder {
        private List<ComponentUser> create;
        private List<ComponentUser> update;
        private List<ComponentUser> updateToDefault;
        private List<ComponentUser> delete;


        public Builder() {
        }

        public Builder setCreate(List<ComponentUser> create) {
            this.create = create;
            return this;
        }

        public Builder setUpdate(List<ComponentUser> update) {
            this.update = update;
            return this;
        }

        public Builder setDelete(List<ComponentUser> delete) {
            this.delete = delete;
            return this;
        }

        public Builder setUpdateToDefault(List<ComponentUser> updateToDefault) {
            this.updateToDefault = updateToDefault;
            return this;
        }

        public ComponentUserRefreshDTO build() {
            return new ComponentUserRefreshDTO(this);
        }
    }
}