package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class TestDataModel extends RestTemplate {

    @Id @JsonProperty("_id") @JsonAlias("id")
    private TestDataModelIdentifier id;
    private String description;
    private boolean active = true;

    @JsonAlias("preExecutionSteps")
    private Map<String, Action> preSteps;

    @JsonAlias("postExecutionSteps")
    private Map<String, Action> postSteps;

    private Map<String, Object> vars;

}
