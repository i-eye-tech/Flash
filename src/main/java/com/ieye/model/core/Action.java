package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {

    private PreActionType type;

    @JsonAlias({"request", "requestSpecification"})
    private RestTemplate rest;

    @JsonAlias({"dbSpecification", "dbSpec"})
    private DatabaseTemplate database;

    @JsonAlias("sleepinMillis")
    private int sleepInMs;

    private boolean active = true;

}
