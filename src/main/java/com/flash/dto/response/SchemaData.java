package com.flash.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash.mongo.model.ApiSpecification;
import com.flash.mongo.model.Evaluator;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchemaData {

    private List<ApiSpecification> apiSpec;
    private List<Evaluator> evaluators;

    public SchemaData( List<ApiSpecification> apiSpec,      List<Evaluator> evaluators) {
        this.apiSpec = apiSpec;
        this.evaluators=evaluators;
    }

    public List<ApiSpecification> getApiSpec() {
        return apiSpec;
    }

    public void setApiSpec(List<ApiSpecification> apiSpec) {
        this.apiSpec = apiSpec;
    }

    public List<Evaluator> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(List<Evaluator> evaluators) {
        this.evaluators = evaluators;
    }
}
