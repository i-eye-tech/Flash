package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "schema")
@Data @NoArgsConstructor
public class TestSchemaModel {

    @Id
    @JsonProperty("_id")
    @JsonAlias("id")
    private MappingId id;
    private List<ApiSpecification> apiSpec;
    private List<PreExecution> preExecutionSteps;
    private List<Evaluator> evaluators;
    private boolean active;

    public MappingId getId() {
        return id;
    }

    public void setId(MappingId id) {
        this.id = id;
    }

    public List<ApiSpecification> getApiSpec() {
        return apiSpec;
    }

    public void setApiSpec(List<ApiSpecification> apiSpec) {
        this.apiSpec = apiSpec;
    }

    public List<PreExecution> getPreExecutionSteps() {
        return preExecutionSteps;
    }

    public void setPreExecutionSteps(List<PreExecution> preExecutionSteps) {
        this.preExecutionSteps = preExecutionSteps;
    }

    public List<Evaluator> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(List<Evaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
