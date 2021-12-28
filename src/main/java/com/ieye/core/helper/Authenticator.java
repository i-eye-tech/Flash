package com.ieye.core.helper;

import com.ieye.core.enums.RestMethod;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.Credential;
import com.ieye.model.core.RestSpecification;
import com.ieye.recur.TokensManager;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Authenticator {

    @Autowired
    private RestHelper restHelper;

    @Autowired
    CurrentTest currentTest;

    @Autowired
    TokensManager tokensManager;

    public void authenticate(String basePath, RequestSpecification request, Credential credential) {
        if(credential == null || !credential.isAuthenticate())
            return;

        String key = credential.getType() + ":" + credential.getUsername();
        String token = tokensManager.getToken(currentTest.getRequestId(), key);
        if(token == null) {
            token = login(basePath, credential.getType(), credential.getUsername(), credential.getPassword());
            tokensManager.addToken(currentTest.getRequestId(), key, token);
        }
        request.header("Authorization", "Bearer " + token);
    }

    /*
        private methods
     */

    private String login(String baseUrl, String type, String username, String password) {
        RestSpecification r = RestSpecification.builder()
                .basePath(baseUrl)
                .url("/api/auth/" + type.trim().toLowerCase() + "/login")
                .body(String.format("{ \"username\": \"%s\", \"password\":  \"%s\" }", username, password))
                .method(RestMethod.POST)
                .contentType("application/json")
                .build();
        return restHelper.execute(r, false).jsonPath().getString("data.token");
    }

}
