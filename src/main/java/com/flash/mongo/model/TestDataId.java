package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TestDataId {
    private String testDataId;
    private String testCaseId;

    public String getTestDataId() {
        return testDataId;
    }

    public void setTestDataId(String testDataId) {
        this.testDataId = testDataId;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }
}
