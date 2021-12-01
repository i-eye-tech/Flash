package com.flash.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor

public class MappingRequestId {

    private String projectId;
    private String testId;
    private String requestId;

    public MappingRequestId(String projectId, String testId, String requestId) {
        this.projectId = projectId;
        this.testId = testId;
        this.requestId = requestId;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
