package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseTemplate {

    @JsonAlias("dbType")
    private DatabaseType type = DatabaseType.MONGO;

    private String databaseName;
    @JsonAlias("collection")
    private String collectionName;
    private String query;
    private MongoDBQuery mongoQuery;

}
