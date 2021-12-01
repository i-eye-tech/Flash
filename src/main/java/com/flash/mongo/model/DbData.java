package com.flash.mongo.model;

import com.flash.constants.DbType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DbData{
    private DbType dbType;
    private String databaseName;
    private String query;
    private Class deserializeClass;


    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Class getDeserializeClass() {
        return deserializeClass;
    }

    public void setDeserializeClass(Class deserializeClass) {
        this.deserializeClass = deserializeClass;
    }
}
