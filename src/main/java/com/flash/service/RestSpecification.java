package com.flash.service;

import com.flash.mongo.model.Credentials;
import lombok.Data;

@Data
public class RestSpecification {

    private String basePath;
    private String method;
    private String url;
    private Object queryParams;
    private Object pathParams;
    private Object cookies;
    private Object headers;
    private Object formParams;
    private Credentials credentials;
    private String body;
    private String contentType = "application/json";
    private Integer expectedStatusCode;
    private String expectedJson;
    private String requestId;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Object queryParams) {
        this.queryParams = queryParams;
    }

    public Object getPathParams() {
        return pathParams;
    }

    public void setPathParams(Object pathParams) {
        this.pathParams = pathParams;
    }

    public Object getCookies() {
        return cookies;
    }

    public void setCookies(Object cookies) {
        this.cookies = cookies;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    public Object getFormParams() {
        return formParams;
    }

    public void setFormParams(Object formParams) {
        this.formParams = formParams;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(Integer expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public String getExpectedJson() {
        return expectedJson;
    }

    public void setExpectedJson(String expectedJson) {
        this.expectedJson = expectedJson;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
