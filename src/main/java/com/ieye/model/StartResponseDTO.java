package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartResponseDTO {

    private String requestId;
    private String testId;
    private String projectId;

}
