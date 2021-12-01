package com.flash.dto.response;

import lombok.Data;

@Data
public class InMemorySchemaResponse {
    private String projectId;
    private String testId;
    private SchemaData data;


    public InMemorySchemaResponse(String projectId, String testId, SchemaData data) {
        this.projectId = projectId;
        this.testId = testId;
        this.data = data;
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

    public SchemaData getData() {
        return data;
    }

    public void setData(SchemaData data) {
        this.data = data;
    }
}
