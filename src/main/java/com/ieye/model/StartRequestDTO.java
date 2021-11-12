package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartRequestDTO {

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
