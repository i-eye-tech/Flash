package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TestDataModel extends BaseModel {

    @Id
    @JsonProperty("_id")
    @JsonAlias("id")
    private TestDataId id;
    private String testId;
    private TestMeta testMeta;
    private boolean active;
    private String testType;
    private String expectedJsonResponse;
    private String requestBody;
    private JsonNode evaluatedJson;
    private List<ResponseValidator> validator;

    public TestDataId getId() {
        return id;
    }

    public void setId(TestDataId id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public TestMeta getTestMeta() {
        return testMeta;
    }

    public void setTestMeta(TestMeta testMeta) {
        this.testMeta = testMeta;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getExpectedJsonResponse() {
        return expectedJsonResponse;
    }

    public void setExpectedJsonResponse(String expectedJsonResponse) {
        this.expectedJsonResponse = expectedJsonResponse;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public JsonNode getEvaluatedJson() {
        return evaluatedJson;
    }

    public void setEvaluatedJson(JsonNode evaluatedJson) {
        this.evaluatedJson = evaluatedJson;
    }

    public List<ResponseValidator> getValidator() {
        return validator;
    }

    public void setValidator(List<ResponseValidator> validator) {
        this.validator = validator;
    }
}
