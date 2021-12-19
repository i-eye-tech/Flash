package com.ieye.core.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ieye.core.enums.RestMethod;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.ApiSpecification;
import com.ieye.model.core.RestSpecification;
import com.ieye.model.core.RestTemplate;
import com.ieye.model.core.TestDataModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RestManager {

    @Autowired Evaluator evaluator;
    @Autowired CurrentTest currentTest;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestSpecification createRestSpecification(ApiSpecification apiSpecification, TestDataModel testDataModel) {
        RestSpecification restSpecification = RestSpecification.builder().basePath(apiSpecification.getDomain())
                .url(getPreferredValue(apiSpecification.getEndPoint(), testDataModel.getEndPoint()))
                .method(RestMethod.valueOf(getPreferredValue(String.valueOf(apiSpecification.getMethod()), String.valueOf(testDataModel.getMethod()))))
                .queryParams(getPreferredValue(apiSpecification.getQueryParams(), testDataModel.getQueryParams()))
                .pathParams(getPreferredValue(apiSpecification.getPathParams(), testDataModel.getPathParams()))
                .formParams(getPreferredValue(apiSpecification.getFormParams(), testDataModel.getFormParams()))
                .expectedStatusCode(testDataModel.getExpectedStatusCode())
                .contentType(getPreferredValue(apiSpecification.getContentType(), testDataModel.getContentType()))
                .cookies(getPreferredValue(apiSpecification.getCookies(), testDataModel.getCookies()))
                .headers(getPreferredValue(apiSpecification.getHeaders(), testDataModel.getHeaders()))
                .body(evaluator.evaluate(testDataModel.getBody(), currentTest.getData()))
                .build();

       ((ObjectNode) currentTest.getData()).putPOJO("request", objectMapper.valueToTree(restSpecification));
       restSpecification.setExpectedJson(evaluator.evaluate(testDataModel.getExpectedJson(), currentTest.getData()));
       return restSpecification;
    }

    public RestSpecification createRestSpecification(ApiSpecification apiSpecification, RestTemplate restTemplate) {
        return RestSpecification.builder().basePath(restTemplate.getDomain())
                .url(getPreferredValue(restTemplate.getEndPoint(), restTemplate.getEndPoint()))
                .method(RestMethod.valueOf(getPreferredValue(String.valueOf(restTemplate.getMethod()),
                        String.valueOf(restTemplate.getMethod()))))
                .queryParams(getPreferredValue(restTemplate.getQueryParams(), restTemplate.getQueryParams()))
                .pathParams(getPreferredValue(restTemplate.getPathParams(), restTemplate.getPathParams()))
                .formParams(getPreferredValue(restTemplate.getFormParams(), restTemplate.getFormParams()))
                .expectedStatusCode(restTemplate.getExpectedStatusCode())
                .contentType(getPreferredValue(restTemplate.getContentType(), restTemplate.getContentType()))
                .cookies(getPreferredValue(restTemplate.getCookies(), restTemplate.getCookies()))
                .headers(getPreferredValue(restTemplate.getHeaders(), restTemplate.getHeaders()))
                .body(evaluator.evaluate(restTemplate.getBody(), currentTest.getData()))
                .build();
    }

    /*
        Private Methods
    */

    private String getPreferredValue(String s1, String s2) {
        return (StringUtils.isBlank(s2)  || s2.equalsIgnoreCase("null")) ?
                evaluator.evaluate(s1, currentTest.getData()) : evaluator.evaluate(s2, currentTest.getData());
    }

    private Map<String, String> getPreferredValue(Map<String, String> m1, Map<String, String> m2) {
        Map<String, String> params = new HashMap<>();

        Arrays.asList(m1, m2).forEach(n -> {
            if(n != null)
                n.forEach((k,v) -> params.put(k, evaluator.evaluate(v, currentTest.getData())));
        });

        return params;
    }

}
