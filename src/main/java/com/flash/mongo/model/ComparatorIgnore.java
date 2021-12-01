package com.flash.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
//@AllArgsConstructor @NoArgsConstructor
public class ComparatorIgnore {

    private String ignoreOperation;
    private Set<String> ignoreFields;

    public ComparatorIgnore(String ignoreOperation, Set<String> ignoreFields) {
        this.ignoreOperation = ignoreOperation;
        this.ignoreFields = ignoreFields;
    }

    public ComparatorIgnore() {
    }

    public String getIgnoreOperation() {
        return ignoreOperation;
    }

    public void setIgnoreOperation(String ignoreOperation) {
        this.ignoreOperation = ignoreOperation;
    }

    public Set<String> getIgnoreFields() {
        return ignoreFields;
    }

    public void setIgnoreFields(Set<String> ignoreFields) {
        this.ignoreFields = ignoreFields;
    }
}