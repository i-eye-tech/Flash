package com.flash.mongo.model;

import lombok.Data;

@Data
public class Credentials {

    private String username;
    private String password;
    private String sessionId;
    private Boolean csrfToken = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(Boolean csrfToken) {
        this.csrfToken = csrfToken;
    }
}
