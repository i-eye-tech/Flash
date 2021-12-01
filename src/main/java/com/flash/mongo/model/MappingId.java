package com.flash.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class MappingId {

    private String projectId;
    private String testId;

    @Override
    public String toString() {
        return "Id{" +
                "projectId='" + projectId + '\'' +
                ", testId='" + testId + '\'' +
                '}';
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }
}
