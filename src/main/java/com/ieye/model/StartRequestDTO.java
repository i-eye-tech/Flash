package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartRequestDTO {

    @NonNull
    private String testId;
    private String projectId;
    private String testType;

    @Override
    public String toString() {
        return "StartRequestDTO{" +
                "testId='" + testId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", testType='" + testType + '\'' +
                '}';
    }
}
