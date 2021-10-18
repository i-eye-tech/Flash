package com.flash.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

//@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartTestResponseDto {

    private String requestId;
    private String projectId;
    private String testId;
    private Error error;

    public StartTestResponseDto(String requestId,String projectId, String testId, Error error) {
        this.projectId=projectId;
        this.testId=testId;
        this.requestId = requestId;
        this.error = error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
