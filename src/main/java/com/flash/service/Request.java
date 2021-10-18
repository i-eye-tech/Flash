package com.flash.service;

import com.flash.mongo.model.Credentials;
import lombok.Data;

import java.util.Map;

@Data
public class Request {

    private String basePath;
    private String method;
    private String url;
    private Map<String, String> queryParams;
    private Map<String, String> pathParams;
    private Map<String, String> cookies;
    private Map<String, String> headers;
    private Map<String, String> formParams;
    private Credentials credentials;
    private String body;
    private String contentType = "application/json";
    private Integer expectedStatusCode;
    private String expectedJson;
    private String requestId;


    public Request() {
    }

    public Request(Request request) {
        this.basePath = request.getBasePath();
        this.url = request.getUrl();
        this.method = request.getMethod();
        this.queryParams = request.getQueryParams();
        this.pathParams = request.getPathParams();
        this.cookies = request.getCookies();
        this.headers = request.getHeaders();
        this.formParams = request.getFormParams();
        this.credentials = request.getCredentials();
        this.body = request.getBody();
        this.contentType = request.getContentType();
        this.expectedStatusCode = request.getExpectedStatusCode();
        this.expectedJson = request.getExpectedJson();
        this.requestId = request.getRequestId();
    }

    public void setContentType(String contentType) {
        if (contentType != null)
            this.contentType = contentType;
    }

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

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getFormParams() {
        return formParams;
    }

    public void setFormParams(Map<String, String> formParams) {
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
