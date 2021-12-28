package com.ieye.core.helper;

import com.ieye.core.enums.RestMethod;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.Credential;
import com.ieye.model.core.RestSpecification;
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

    public void authenticate(String basePath, RequestSpecification request, Credential credential) {

    }

}
