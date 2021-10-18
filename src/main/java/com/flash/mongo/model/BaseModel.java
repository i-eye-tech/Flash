package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BaseModel {

    private ComparatorIgnore ignore;
    private List<KeyValuePair> pathParams;
    private List<KeyValuePair> queryParams;
    private List<KeyValuePair> headers;
    @JsonAlias("endpoint")
    private String endPoint;
    private Map<String,PreExecution> preExecutionSteps;
    private Credentials credentials;
    private List<KeyValuePair> formParams;
    private List<KeyValuePair> cookies;
    private String method;
    private String contentType;

    public ComparatorIgnore getIgnore() {
        return ignore;
    }

    public void setIgnore(ComparatorIgnore ignore) {
        this.ignore = ignore;
    }

    public List<KeyValuePair> getPathParams() {
        return pathParams;
    }

    public void setPathParams(List<KeyValuePair> pathParams) {
        this.pathParams = pathParams;
    }

    public List<KeyValuePair> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<KeyValuePair> queryParams) {
        this.queryParams = queryParams;
    }

    public List<KeyValuePair> getHeaders() {
        return headers;
    }

    public void setHeaders(List<KeyValuePair> headers) {
        this.headers = headers;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Map<String, PreExecution> getPreExecutionSteps() {
        return preExecutionSteps;
    }

    public void setPreExecutionSteps(Map<String, PreExecution> preExecutionSteps) {
        this.preExecutionSteps = preExecutionSteps;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public List<KeyValuePair> getFormParams() {
        return formParams;
    }

    public void setFormParams(List<KeyValuePair> formParams) {
        this.formParams = formParams;
    }

    public List<KeyValuePair> getCookies() {
        return cookies;
    }

    public void setCookies(List<KeyValuePair> cookies) {
        this.cookies = cookies;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
