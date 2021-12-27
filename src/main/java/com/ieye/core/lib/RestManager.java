package com.ieye.core.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ieye.core.enums.RestMethod;
import com.ieye.core.helper.RestHelper;
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
public final class RestManager {

    @Autowired
    PatternResolver patternResolver;
    @Autowired CurrentTest currentTest;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestSpecification createRestSpecification(ApiSpecification apiSpecification, TestDataModel testDataModel) {
        ((ObjectNode) currentTest.getData()).putPOJO("vars", objectMapper.valueToTree(
                getPreferredValueWithObject(apiSpecification.getVars(), testDataModel.getVars())));

        RestSpecification restSpecification = RestSpecification.builder()
                .basePath(getPreferredValue(apiSpecification.getDomain(), testDataModel.getDomain()))
                .url(getPreferredValue(apiSpecification.getEndPoint(), testDataModel.getEndPoint()))
                .method(RestMethod.valueOf(getPreferredValue(String.valueOf(apiSpecification.getMethod()), String.valueOf(testDataModel.getMethod()))))
                .queryParams(getPreferredValue(apiSpecification.getQueryParams(), testDataModel.getQueryParams()))
                .pathParams(getPreferredValue(apiSpecification.getPathParams(), testDataModel.getPathParams()))
                .formParams(getPreferredValue(apiSpecification.getFormParams(), testDataModel.getFormParams()))
                .expectedStatusCode(testDataModel.getExpectedStatusCode())
                .contentType(getPreferredValue(apiSpecification.getContentType(), testDataModel.getContentType()))
                .cookies(getPreferredValue(apiSpecification.getCookies(), testDataModel.getCookies()))
                .headers(getPreferredValue(apiSpecification.getHeaders(), testDataModel.getHeaders()))
                .body(patternResolver.resolve(testDataModel.getBody(), currentTest.getData()))
                .build();

       ((ObjectNode) currentTest.getData()).putPOJO("request", objectMapper.valueToTree(restSpecification));
       restSpecification.setExpectedJson(patternResolver.resolve(testDataModel.getExpectedJson(), currentTest.getData()));
       return restSpecification;
    }

    public RestSpecification createRestSpecification(ApiSpecification apiSpecification, RestTemplate restTemplate) {
        return RestSpecification.builder()
                .basePath(getPreferredValue(apiSpecification.getDomain(), restTemplate.getDomain()))
                .url(getPreferredValue(apiSpecification.getEndPoint(), restTemplate.getEndPoint()))
                .method(restTemplate.getMethod())
                .queryParams(restTemplate.getQueryParams())
                .pathParams(restTemplate.getPathParams())
                .formParams(restTemplate.getFormParams())
                .expectedStatusCode(restTemplate.getExpectedStatusCode() == null ? 200 :
                        restTemplate.getExpectedStatusCode())
                .contentType(restTemplate.getContentType())
                .cookies(restTemplate.getCookies())
                .headers(restTemplate.getHeaders())
                .body(patternResolver.resolve(restTemplate.getBody(), currentTest.getData()))
                .build();
    }

    /*
        Private Methods
    */

    private String getPreferredValue(String s1, String s2) {
        return (StringUtils.isBlank(s2)  || s2.equalsIgnoreCase("null")) ?
                patternResolver.resolve(s1, currentTest.getData()) : patternResolver.resolve(s2, currentTest.getData());
    }

    private Map<String, String> getPreferredValue(Map<String, String> m1, Map<String, String> m2) {
        Map<String, String> params = new HashMap<>();

        Arrays.asList(m1, m2).forEach(n -> {
            if(n != null)
                n.forEach((k,v) -> params.put(k, patternResolver.resolve(v, currentTest.getData())));
        });

        return params;
    }

    private Map<String, Object> getPreferredValueWithObject(Map<String, Object> m1, Map<String, Object> m2) {
        Map<String, Object> params = new HashMap<>();

        Arrays.asList(m1, m2).forEach(n -> {
            if(n != null)
                n.forEach((k,v) -> params.put(k, patternResolver.resolve(v.toString(), currentTest.getData())));
        });

        return params;
    }

}
