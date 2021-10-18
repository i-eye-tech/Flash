package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TestMeta {

    private Integer statusCode;
    private String responseMessage;
    private Map<String, String> tcDescription;
    private Map<String, Object> expectedMap;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<String, String> getTcDescription() {
        return tcDescription;
    }

    public void setTcDescription(Map<String, String> tcDescription) {
        this.tcDescription = tcDescription;
    }

    public Map<String, Object> getExpectedMap() {
        return expectedMap;
    }

    public void setExpectedMap(Map<String, Object> expectedMap) {
        this.expectedMap = expectedMap;
    }
}
