package com.ieye.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Request {

    private String basePath;
    private String method;
    private String url;
    private Map<String, String> queryParams;
    private Map<String, String> pathParams;
    private Integer expectedStatusCode;
    private String requestId;

//    public Request(String basePath, String method, String url, Map<String, String> queryParams, Map<String, String> pathParams, Integer expectedStatusCode, String requestId) {
//        this.basePath = basePath;
//        this.method = method;
//        this.url = url;
//        this.queryParams = queryParams;
//        this.pathParams = pathParams;
//        this.expectedStatusCode = expectedStatusCode;
//        this.requestId = requestId;
//    }
//    public Request(Request request) {
//        this.basePath = request.basePath;
//        this.method=request.method;
//        this.url=request.url;
//        this.queryParams= request.queryParams;
//        this.pathParams= request.pathParams;
//        this.expectedStatusCode= request.expectedStatusCode;
//        this.requestId=request.requestId;
//    }
}


