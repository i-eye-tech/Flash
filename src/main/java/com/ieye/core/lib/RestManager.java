package com.ieye.core.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ieye.core.enums.RestMethod;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.ApiSpecification;
import com.ieye.model.RestSpecification;
import com.ieye.model.TestDataModel;
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

    @Autowired private Evaluator evaluator;
    @Autowired private CurrentTest currentTest;

    private ObjectMapper objectMapper;

    public RestSpecification createRestSpecification(ApiSpecification apiSpecification, TestDataModel testDataModel) {
        RestSpecification restSpecification = RestSpecification.builder().basePath(apiSpecification.getDomain())
                .url(getPreferredValue(apiSpecification.getEndPoint(), testDataModel.getEndPoint()))
                .method(RestMethod.valueOf(getPreferredValue(apiSpecification.getMethod().toString(), testDataModel.getMethod().toString())))
                .queryParams(getPreferredValue(apiSpecification.getQueryParams(), testDataModel.getQueryParams()))
                .pathParams(getPreferredValue(apiSpecification.getPathParams(), testDataModel.getPathParams()))
                .formParams(getPreferredValue(apiSpecification.getFormParams(), testDataModel.getFormParams()))
                .expectedStatusCode(testDataModel.getExpectedStatusCode())
                .contentType(getPreferredValue(apiSpecification.getContentType(), testDataModel.getContentType()))
                .cookies(getPreferredValue(apiSpecification.getCookies(), testDataModel.getCookies()))
                .headers(getPreferredValue(apiSpecification.getHeaders(), testDataModel.getHeaders()))
                .build();

       ((ObjectNode) currentTest.getData()).putPOJO("request", objectMapper.valueToTree(restSpecification));
       restSpecification.setExpectedJson(evaluator.evaluate(testDataModel.getExpectedJson(), currentTest.getData()));
       return restSpecification;
    }

    /*
        Private Methods
    */

    private String getPreferredValue(String s1, String s2) {
        return StringUtils.isNotBlank(s2) ? evaluator.evaluate(s2, currentTest.getData()) :
                evaluator.evaluate(s1, currentTest.getData());
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
