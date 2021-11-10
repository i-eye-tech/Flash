package com.ieye.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class RestSpecification {

    private String basePath;
    private String method;
    private String url;
    private Map<String, String> queryParams;
    private Map<String, String> pathParams;
    private Map<String, String> formParams;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private Object body;
    private String contentType = "application/json";
    private Integer expectedStatusCode;
    private String expectedJson;

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
    }

}


