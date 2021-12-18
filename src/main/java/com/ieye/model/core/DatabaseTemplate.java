package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseTemplate {

    @JsonAlias("dbType")
    private DatabaseType type;

    private String databaseName;
    private String query;

}
