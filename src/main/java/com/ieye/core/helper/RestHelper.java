package com.ieye.core.helper;

import com.ieye.core.enums.RestMethod;
import com.ieye.model.RestSpecification;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;
import org.testng.Assert;

@Component
public class RestHelper {

    public Response execute(RestSpecification restSpecification) {
        RequestSpecification request = createRequest(restSpecification);
        Response response = send(request, restSpecification.getMethod());

        if(restSpecification.getExpectedStatusCode() != null
                && response.getStatusCode() != restSpecification.getExpectedStatusCode())
            Assert.fail("Expected response code was " + restSpecification.getExpectedStatusCode() + " but got " + response.statusCode());

        return response;
    }

    private RequestSpecification createRequest(RestSpecification restSpecification) {
        return RestAssured.given()
                .basePath(restSpecification.getBasePath())
                .baseUri(restSpecification.getUrl())
                .headers(restSpecification.getHeaders())
                .queryParams(restSpecification.getPathParams())
                .formParams(restSpecification.getFormParams())
                .pathParams(restSpecification.getPathParams())
                .cookies(restSpecification.getCookies())
                .body(restSpecification.getBody())
                .contentType(restSpecification.getContentType());
    }

    private Response send(RequestSpecification requestSpecification, RestMethod method) {
        return requestSpecification.request(method.toString());
    }

}
