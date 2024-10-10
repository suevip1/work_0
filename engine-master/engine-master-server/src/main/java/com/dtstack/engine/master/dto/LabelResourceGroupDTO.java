package com.dtstack.engine.master.dto;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-22 11:20
 */
public class LabelResourceGroupDTO {
    /**
     * 节点标签
     */
    private String label;

    /**
     * 服务器用户列表
     */
    private List<LabelResourceGroupUserDTO> userList;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<LabelResourceGroupUserDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<LabelResourceGroupUserDTO> userList) {
        this.userList = userList;
    }

}