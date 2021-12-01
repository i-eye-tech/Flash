package com.flash.mongo.model;

import com.flash.constants.PreExecutionRunType;
import com.flash.service.RestSpecification;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
public class ResponseValidator {
    private PreExecutionRunType runType =PreExecutionRunType.GET_DATA;
    private DbData dbSpec;
    private RestSpecification reqSpec;
    private DbValidationCriteria criteria;
    private Integer count;
    private Map<String,Object> fields;
    private Boolean active;

    public PreExecutionRunType getRunType() {
        return runType;
    }

    public void setRunType(PreExecutionRunType runType) {
        this.runType = runType;
    }

    public DbData getDbSpec() {
        return dbSpec;
    }

    public void setDbSpec(DbData dbSpec) {
        this.dbSpec = dbSpec;
    }

    public RestSpecification getReqSpec() {
        return reqSpec;
    }

    public void setReqSpec(RestSpecification reqSpec) {
        this.reqSpec = reqSpec;
    }

    public DbValidationCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(DbValidationCriteria criteria) {
        this.criteria = criteria;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
