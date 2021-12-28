package com.ieye.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.enums.RestMethod;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.RestSpecification;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;

@Component
@Slf4j
public class RestHelper {

    @Autowired
    CurrentTest currentTest;

    @Autowired
    Reporter reporter;

    @Autowired
    Authenticator authenticator;

    public Response execute(RestSpecification restSpecification) {
        return execute(restSpecification, true);
    }

    public Response execute(RestSpecification restSpecification, boolean log) {
        RequestSpecification request = createRequest(restSpecification);
        authenticator.authenticate(restSpecification.getBasePath(), request, restSpecification.getCredential());
        Response response = send(request, restSpecification.getMethod());

        if (log) {
            try {
                reporter.info(currentTest.getExtentTest(), String.format("%s %s%s%n%n%s%n%n",
                        restSpecification.getMethod(), restSpecification.getBasePath(), restSpecification.getUrl(),
                        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(restSpecification)));
            } catch (JsonProcessingException ignore) {}
            reporter.info(currentTest.getExtentTest(), "Response:\n" + response.body().asPrettyString());
        }

        if(restSpecification.getExpectedStatusCode() != null
                && response.getStatusCode() != restSpecification.getExpectedStatusCode()) {
            String msg = "Expected response code was " + restSpecification.getExpectedStatusCode() + " but got "
                    + response.statusCode();
            Assert.fail(msg);
        }

        return response;
    }

    private RequestSpecification createRequest(RestSpecification restSpecification) {
        RequestSpecification requestSpecification = RestAssured.given()
                .basePath(restSpecification.getUrl())
                .baseUri(restSpecification.getBasePath())
                .contentType(restSpecification.getContentType());

        if(restSpecification.getHeaders() != null && !restSpecification.getHeaders().isEmpty())
            requestSpecification.headers(restSpecification.getHeaders());

        if(restSpecification.getPathParams() != null && !restSpecification.getPathParams().isEmpty())
            requestSpecification.pathParams(restSpecification.getPathParams());

        if(restSpecification.getFormParams() != null && !restSpecification.getFormParams().isEmpty())
            requestSpecification.formParams(restSpecification.getFormParams());

        if(restSpecification.getQueryParams() != null && !restSpecification.getQueryParams().isEmpty())
            requestSpecification.queryParams(restSpecification.getQueryParams());

        if(restSpecification.getCookies() != null && !restSpecification.getCookies().isEmpty())
            requestSpecification.cookies(restSpecification.getCookies());

        if(restSpecification.getBody() != null && !restSpecification.getBody().toString().isEmpty())
            requestSpecification.body(restSpecification.getBody());

        return requestSpecification;

    }

    private Response send(RequestSpecification requestSpecification, RestMethod method) {
        return requestSpecification.request(method.toString());
    }

}
