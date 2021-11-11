package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ieye.core.enums.RestMethod;
import lombok.Data;

import java.util.Map;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class BaseModel {

    private Map<String, String> queryParams;
    private Map<String, String> formParams;
    private Map<String, String> pathParams;
    private Map<String, String> cookies;
    private Map<String, String> headers;
    private RestMethod method;
    private String contentType = "application/json";
    private String endPoint;

}