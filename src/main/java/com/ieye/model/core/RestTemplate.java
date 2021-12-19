package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestTemplate extends BaseRestTemplate {

    private String expectedJson;
    private String body;
    private Integer expectedStatusCode;
    private List<ValidatorTemplate> validator;

}
