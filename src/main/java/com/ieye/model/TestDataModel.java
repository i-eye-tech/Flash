package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@EqualsAndHashCode(callSuper = true)
@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDataModel extends BaseModel {

    @Id @NonNull @JsonProperty("_id") @JsonAlias("id")
    private TestDataModelIdentifier id;
    private String description;
    private String expectedJson;
    private int expectedStatusCode;

}
