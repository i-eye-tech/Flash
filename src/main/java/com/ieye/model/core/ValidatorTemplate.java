package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidatorTemplate extends Wait {

    private ActionType type = ActionType.REST;
    private Map<String, Object> fields;
    private DatabaseTemplate database;
    private Integer count;

}
