package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDataModel {

    @Id @NonNull @JsonProperty("_id") @JsonAlias("id")
    private TestDataModelIdentifier id;
    private String description;
    private List<Map<String, Object>> queryParams;


}
