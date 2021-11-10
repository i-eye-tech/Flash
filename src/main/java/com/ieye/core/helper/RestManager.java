package com.ieye.core.helper;

import com.ieye.model.ApiSpecification;
import com.ieye.model.RestSpecification;
import com.ieye.model.TestDataModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RestManager {

    @Autowired
    private Evaluator evaluator;

    public RestSpecification createRequest(ApiSpecification apiSpecification, TestDataModel testDataModel) {
        RestSpecification restSpecification = RestSpecification.builder().basePath(apiSpecification.getDomain())
                .url(getPreferredValue(apiSpecification.getEndPoint(), testDataModel.getEndPoint()))
                .method(getPreferredValue(apiSpecification.getMethod(), testDataModel.getMethod()))
                .queryParams(getPreferredValue(apiSpecification.getQueryParams(), testDataModel.getQueryParams()))
                .pathParams(getPreferredValue(apiSpecification.getPathParams(), testDataModel.getPathParams()))
                .formParams(getPreferredValue(apiSpecification.getFormParams(), testDataModel.getFormParams()))
                .expectedStatusCode(testDataModel.getExpectedStatusCode())
                .contentType(getPreferredValue(apiSpecification.getContentType(), testDataModel.getContentType()))
                .expectedJson(testDataModel.getExpectedJson())
                .build();

       return restSpecification;
    }

    /*
        Private Methods
     */

    private String getPreferredValue(String s1, String s2) {
        return StringUtils.isNotBlank(s2) ? evaluator.evaluate(s2) : evaluator.evaluate(s1);
    }

    private Map<String, String> getPreferredValue(Map<String, String> m1, Map<String, String> m2) {
        Map<String, String> params = new HashMap<>();

        Arrays.asList(m1, m2).forEach(n -> {
            if(n != null)
                n.forEach((k,v) -> params.put(k, evaluator.evaluate(v)));
        });

        return params;
    }


}
