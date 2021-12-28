package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ieye.core.enums.RestMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestSpecification {

    private String basePath;
    private RestMethod method = RestMethod.GET;
    private String url;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, String> formParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private Object body;
    private String contentType = "application/json";
    private Integer expectedStatusCode;
    private String expectedJson;
    private Credential credential;

    public RestSpecification() { }

    public RestSpecification(RestSpecification restSpecification) {
        this.basePath = restSpecification.basePath;
        this.method = restSpecification.method;
        this.url = restSpecification.url;
        this.queryParams = restSpecification.queryParams;
        this.pathParams = restSpecification.pathParams;
        this.expectedStatusCode = restSpecification.expectedStatusCode;
        this.cookies = restSpecification.cookies;
        this.headers = restSpecification.headers;
        this.formParams = restSpecification.formParams;
        this.body = restSpecification.body;
        this.expectedJson = restSpecification.expectedJson;
        this.contentType = restSpecification.contentType;
        this.credential = restSpecification.credential;
    }

}


