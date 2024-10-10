package com.dtstack.engine.master.dto;

import com.dtstack.engine.dto.LabelGrantedProjectDTO;

import java.util.List;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-17 18:12
 */
public class ComponentUserAffectProjectDTO {
    private Set<String> labels;

    private Set<String> userNames;

    private List<LabelGrantedProjectDTO> grantedProjects;

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public Set<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(Set<String> userNames) {
        this.userNames = userNames;
    }

    public List<LabelGrantedProjectDTO> getGrantedProjects() {
        return grantedProjects;
    }

    public void setGrantedProjects(List<LabelGrantedProjectDTO> grantedProjects) {
        this.grantedProjects = grantedProjects;
    }
}