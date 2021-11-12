package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class TestDataModel extends BaseModel {

    @Id @JsonProperty("_id") @JsonAlias("id")
    private TestDataModelIdentifier id;
    private String description;
    private String expectedJson;
    private String body;
    private int expectedStatusCode;

}
