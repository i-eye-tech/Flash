package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestTemplate extends BaseRestTemplate {

    private String expectedJson;
    private Object body;
    private Integer expectedStatusCode;
    private List<ValidatorTemplate> validator;

}
