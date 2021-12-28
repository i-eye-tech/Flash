package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ieye.core.enums.RestMethod;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class BaseRestTemplate {

    private String domain;
    private Map<String, String> queryParams;
    private Map<String, String> formParams;
    private Map<String, String> pathParams;
    private Map<String, String> cookies;
    private Map<String, String> headers;
    private RestMethod method;
    private String contentType = "application/json";
    private String endPoint;
    private Comparator ignore;
    private Credential credential;

}
