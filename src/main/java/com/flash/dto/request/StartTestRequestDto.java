package com.flash.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown=true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartTestRequestDto {

    private String projectId;
    private String testId;
    private String testType;
    private String domainName;
    private Set<String> apiSpecIds;
    private Error error;

    public StartTestRequestDto(){}

    public StartTestRequestDto(Error error){
        this.error=error;
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

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Set<String> getApiSpecIds() {
        return apiSpecIds;
    }

    public void setApiSpecIds(Set<String> apiSpecIds) {
        this.apiSpecIds = apiSpecIds;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
