package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash.constants.ValidationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSpecification extends BaseModel{
    private String _id;
    private String testName;
    private ValidationType validationType;
    private String testCollection;
    private String domain;
    private String stableDomain;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public String getTestCollection() {
        return testCollection;
    }

    public void setTestCollection(String testCollection) {
        this.testCollection = testCollection;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStableDomain() {
        return stableDomain;
    }

    public void setStableDomain(String stableDomain) {
        this.stableDomain = stableDomain;
    }
}
