package com.flash.service.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flash.mongo.model.*;
import com.flash.service.BaseApiManager;
import com.flash.service.Request;
import com.flash.service.RestSpecification;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class RequestManager extends BaseApiManager  {

    private static final Logger logger = LoggerFactory.getLogger(RequestManager.class);

    public RequestManager(String requestId) {
        super(requestId);
    }

    public Request createRequest(ApiSpecification apiSpecification, TestDataModel testDataModel) {
        Request request = new Request();
        request.setBasePath(apiSpecification.getDomain());
        request.setUrl(getPreferredValue(apiSpecification.getEndPoint(), testDataModel.getEndPoint()));
        request.setMethod(getPreferredValue(apiSpecification.getMethod(), testDataModel.getMethod()));
        request.setQueryParams(getParamsInMapWithEvaluatorCriteria(apiSpecification.getQueryParams(), testDataModel.getQueryParams()));
        request.setPathParams(getParamsInMapWithEvaluatorCriteria(apiSpecification.getPathParams(), testDataModel.getPathParams()));
        request.setHeaders(getParamsInMapWithEvaluatorCriteria(apiSpecification.getHeaders(), testDataModel.getHeaders()));
        request.setBody(!this.evaluatedJson.isEmpty() ? resolvePatternString(testDataModel.getRequestBody(),
                this.evaluatedJson.toString()) : testDataModel.getRequestBody());
        request.setFormParams(getParamsInMapWithEvaluatorCriteria(apiSpecification.getFormParams(),
                testDataModel.getFormParams()));
        request.setCredentials(testDataModel.getCredentials());
        request.setExpectedStatusCode(testDataModel.getTestMeta().getStatusCode());
        request.setCookies(getParamsInMapWithEvaluatorCriteria(apiSpecification.getCookies(), testDataModel.getCookies()));
        request.setContentType(getPreferredValue(apiSpecification.getContentType(), testDataModel.getContentType()));
        return request;
    }

    private Map<String, String> getParamsInMap(List<KeyValuePair> m1, List<KeyValuePair> m2) {
        Map<String, String> params = new HashMap<>();
        if (m1 != null)
            m1.forEach(param -> params.put(param.getKey(), readJsonPath(evaluatedJson.toString(),
                    param.getValue())));

        if (m2 != null)
            m2.forEach(param -> params.put(param.getKey(), readJsonPath(evaluatedJson.toString(), param.getValue())));
        return params;
    }

    private Map<String, String> getParamsInMapWithEvaluatorCriteria(List<KeyValuePair> m1, List<KeyValuePair> m2) {
        if (evaluators.evaluatorList == null || evaluators.evaluatorList.isEmpty())
            return getParamsInMap(m1, m2);
        Map<String, String> params = new HashMap<>();
        if (m1 != null)
            params.putAll(
                    m1.stream().map(val -> getMapper().convertValue(val, KeyValuePair.class)).collect(Collectors.toMap(KeyValuePair::getKey,
                            o -> evaluators.getEvaluator(readJsonPath(evaluatedJson.toString(), o.getValue()),
                                    o.getEvaluator()).toString())));

        if (m2 != null)
            params.putAll(
                    m2.stream().map(val -> getMapper().convertValue(val, KeyValuePair.class)).collect(Collectors.toMap(KeyValuePair::getKey,
                            o -> evaluators.getEvaluator(readJsonPath(evaluatedJson.toString(), o.getValue()).toString(),
                                    o.getEvaluator()).toString())));
        return params;
    }

    private Map<String, String> getParamsInMap(List<KeyValuePair> m1) {
        return getParamsInMap(m1, null);
    }

    private String getPreferredValue(String s1, String s2) {
        // give preference to s2
        return isNotBlank(s2) ? s2 : s1;
    }

    public Response execute(ApiSpecification apiSpecification, TestDataModel testDataModel) {
        Request request = createRequest(apiSpecification, testDataModel);

//        if (evaluatedJson != null)
        ((ObjectNode) evaluatedJson).putPOJO("request", getMapper().valueToTree(request));
//        else {
//            ObjectNode objectNode = getMapper().createObjectNode();
//            objectNode.putPOJO("request", getMapper().valueToTree(request));
//            testDataModel.setEvaluatedJson(objectNode);
//        }
        Response response = execute(request, true);
        try {
            ((ObjectNode) evaluatedJson).putPOJO("response",
                    response.as(JsonNode.class));
        } catch (Exception e) {
            logger.error("Exception while setting response in Evaluated Json , nested Exception is {}", e.getMessage());
        }
        return response;
    }


    /**
     * @param preExecutionList
     * @return
     * @author Avdhesh Gupta (av-g1)
     */

    public JsonNode getPreExecutionResponseList(Map<String, PreExecution> preExecutionList) {
        for (Map.Entry<String, PreExecution> entry : preExecutionList.entrySet()) {
            if ("true".equals(readJsonPath(this.evaluatedJson.toString(), entry.getValue().getActive().toString()).toString())) {
                try {
                    if (entry.getValue().getSleepinMillis() > 0) {
                        logger.info("Thread sleeping for {} milliseconds", entry.getValue().getSleepinMillis());
                        Thread.sleep(entry.getValue().getSleepinMillis());
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                ((ObjectNode) evaluatedJson).putPOJO(entry.getKey(), selectResponseForRunType(entry.getValue()));
            }
        }
        return evaluatedJson;
    }

    /**
     * @param preExecution
     * @param <T>
     * @return
     * @author Avdhesh Gupta (av-g1)
     */
    private <T> T selectResponseForRunType(PreExecution preExecution) {
        switch (preExecution.getRunType()) {
            case REST:
                return getRestResponse(preExecution);
//            case INSERT_DATA:
//                return new ArrayList<>();
            case GET_DATA:
                return getDataFromDb(preExecution.getDbSpecification());
            case MODIFY_DATA:
                return (T) updateDataInDb(preExecution.getDbSpecification());
        }
        return null;
    }

    /**
     * @param preExecution
     * @param <T>
     * @return response in jsonNode format
     * @author Avdhesh Gupta (av-g1)
     * @description returns response for pre execution step, response is returned in JsonNode format
     */
    public <T> T getRestResponse(PreExecution preExecution) {
        ObjectMapper mapper = new ObjectMapper();
        Request request = createEvaluatedRequest(preExecution.getRequestSpecification());
        try {
            return mapper.readValue(execute(request).asString(),
                    new TypeReference<>() {
                    });
        } catch (JsonProcessingException e) {
            logger.error("Exception in processing pre request response json with pre execution step {}, for requestId :{}, nested exception is : {}", preExecution.toString(), reportHelper.getRequestId(), e.getMessage());
        }
        return null;
    }

    public Request createEvaluatedRequest(RestSpecification restSpecification) {
        Request request = new Request();
        request.setHeaders(getValueInMap(restSpecification.getHeaders()));
        request.setPathParams(getValueInMap(restSpecification.getPathParams()));
        request.setCookies(getValueInMap(restSpecification.getCookies()));
        request.setFormParams(getValueInMap(restSpecification.getFormParams()));
        request.setQueryParams(getValueInMap(restSpecification.getQueryParams()));
        request.setCredentials(restSpecification.getCredentials());
        request.setBasePath(restSpecification.getBasePath());
        request.setContentType(restSpecification.getContentType());
        request.setExpectedStatusCode(restSpecification.getExpectedStatusCode());
        request.setBody(resolvePatternString(restSpecification.getBody(), evaluatedJson.toString()));
        request.setMethod(restSpecification.getMethod());
        request.setUrl(restSpecification.getUrl());
        return request;

    }


    public Map<String, String> getValueInMap(Object incomingValue) {
        if (incomingValue instanceof List<?>) {
            List<KeyValuePair> pairList = getMapper().convertValue(incomingValue,
                    new TypeReference<List<KeyValuePair>>() {
                    });
            return getParamsInMapWithEvaluatorCriteria(pairList, null);
        } else
            return (Map<String, String>) incomingValue;
    }


}
