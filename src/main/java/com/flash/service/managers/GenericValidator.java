package com.flash.service.managers;

import com.flash.mongo.model.ApiSpecification;
import com.flash.mongo.model.ResponseValidator;
import com.flash.mongo.model.TestDataModel;
import com.flash.service.RestSpecification;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GenericValidator extends DbValidationManager {
    private static final Logger logger = LoggerFactory.getLogger(GenericValidator.class);

    public GenericValidator(String requestId) {
        super(requestId);
    }

    public boolean responseValidation(TestDataModel testDataModel, ApiSpecification apiSpecification) {
        Response testResponse = execute(apiSpecification, testDataModel);

        boolean isJsonValidated = true;

        if (testDataModel.getExpectedJsonResponse() != null) {
            isJsonValidated = compareJson(resolvePatternString(testDataModel.getExpectedJsonResponse(), testDataModel.getEvaluatedJson().toString()), testResponse.asString(), getComparatorIgnore(testDataModel, apiSpecification));
        }

        if (testDataModel.getValidator() != null) {
            isJsonValidated &= validateData(testDataModel);
        } else if (testDataModel.getExpectedJsonResponse() == null) {
            reportHelper.log("info", " validator and expectedJsonResponse both are null");
            logger.info("expectedJsonResponse and validator both are null for {} ", testDataModel.getId());
        }

        return isJsonValidated;
    }


    public boolean validateData(TestDataModel testDataModel) {
        return testDataModel.getValidator().stream().map(validate -> {
            switch (validate.getRunType()) {
                case GET_DATA:
                    return validateDbData(validate);
                case REST:
                    return validateWithApi(validate);
//                case VALIDATE_RESPONSE:

            }
            return false;
        }).reduce(Boolean::logicalAnd).orElseGet(() -> {
            logger.error("error evaluating validators ");
            return false;
        });
//        return false;
    }

    private boolean validateDbData(ResponseValidator validator) {
        switch (validator.getCriteria()) {
            case COUNT:
                return validateRecordsCount(validator);
            case COMPARE_FIELDS:
                return validateFieldsValue(validator);
        }
        return false;
    }

    private boolean validateWithApi(ResponseValidator validator) {
        switch (validator.getCriteria()) {
            case COMPARE_FIELDS:
                return validateApiResponse(validator);
        }
        return false;
    }

    private boolean validateApiResponse(ResponseValidator validator) {
        Response response = null;
        if (validator.getReqSpec() != null) {
            RestSpecification request = validator.getReqSpec();
            response = execute(createEvaluatedRequest(request));
        }
        if (validator.getFields().size() > 0) {
            return validateResponseFields(validator.getFields(), response != null ? response.asString() : evaluatedJson.get("response").toString());
        }
        return false;
    }

    private boolean validateResponseFields(Map<String, Object> fields, String json) {
//        String json = response.asString();

        return fields.entrySet().stream().map(map -> {
            boolean flag;
            Object evaluatedVal = readJsonPath(this.evaluatedJson.toString(),
                    map.getValue());
//            JsonPath.isPathDefinite()

            flag = readJsonPath(json, "$." + map.getKey()).equals(evaluatedVal);
//                    response.jsonPath().getBoolean("$."+map.getKey()) && response.jsonPath().get("$."+map.getKey())
//                            .equals(evaluatedVal);
            if (!flag) {
                reportHelper.fail("Validating field $." + map.getKey() + " expected value : " + evaluatedVal + " " +
                        "actual value : " + readJsonPath(json, "$." + map.getKey()));
            } else
                reportHelper.log("info",
                        "expecting value of field " + map.getKey() + " : " + evaluatedVal + " found : " + readJsonPath(json, "$." + map.getKey()));
            return flag;
        }).reduce(Boolean::logicalAnd).orElseGet(() -> {
            reportHelper.fail("Error validating fields ");
            logger.error("Error validating fields ");
            return false;
        });
    }


//    private boolean validateApiResponse(ResponseValidator validator){
//        return fields.entrySet().stream().map(map -> {
//            boolean flag;
//            Object evaluatedVal = readJsonPath(this.evaluatedJson.toString(),
//                    map.getValue());
////            JsonPath.isPathDefinite()
//
//            flag = readJsonPath(json,"$."+map.getKey()).equals(evaluatedVal);
////                    response.jsonPath().getBoolean("$."+map.getKey()) && response.jsonPath().get("$."+map.getKey())
////                            .equals(evaluatedVal);
//            if (!flag) {
//                reportHelper.fail("Validating field $." + map.getKey() + " expected value : " + evaluatedVal + " " +
//                        "actual value : " + readJsonPath(json,"$."+map.getKey()));
//            } else
//                reportHelper.log("info",
//                        "expecting value of field " + map.getKey() + " : " + evaluatedVal + " found : " + readJsonPath(json,"$."+map.getKey()));
//            return flag;
//        }).reduce(Boolean::logicalAnd).orElseGet(() -> {
//            reportHelper.fail("Error validating fields ");
//            logger.error("Error validating fields ");
//            return false;
//        });
//    }


}
