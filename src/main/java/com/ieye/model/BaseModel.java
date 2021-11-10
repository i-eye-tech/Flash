package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class BaseModel {

    private Map<String, String> queryParams;
    private Map<String, String> formParams;
    private Map<String, String> pathParams;
    private String method;
    private String contentType = "application/json";
    private String endPoint;

}
