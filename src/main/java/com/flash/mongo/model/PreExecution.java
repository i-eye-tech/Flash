package com.flash.mongo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flash.constants.PreExecutionRunType;
import com.flash.service.RestSpecification;
import lombok.Data;

@Data
public class PreExecution {
    private PreExecutionRunType runType;
    @JsonProperty("dbSpecification")
    @JsonAlias("dbSpec")
    private DbData dbSpecification;
    @JsonProperty("requestSpecification")
    @JsonAlias("request")
    private RestSpecification requestSpecification;
    private int sleepinMillis;
    private Boolean active;

    public PreExecutionRunType getRunType() {
        return runType;
    }

    public void setRunType(PreExecutionRunType runType) {
        this.runType = runType;
    }

    public DbData getDbSpecification() {
        return dbSpecification;
    }

    public void setDbSpecification(DbData dbSpecification) {
        this.dbSpecification = dbSpecification;
    }

    public RestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public void setRequestSpecification(RestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public int getSleepinMillis() {
        return sleepinMillis;
    }

    public void setSleepinMillis(int sleepinMillis) {
        this.sleepinMillis = sleepinMillis;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
