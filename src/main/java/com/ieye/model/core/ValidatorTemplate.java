package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidatorTemplate {

    private ActionType type = ActionType.REST;
    private Map<String, Object> fields;
    private DatabaseTemplate database;
    private Integer count;

}
