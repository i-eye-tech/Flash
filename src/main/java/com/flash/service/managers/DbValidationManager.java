package com.flash.service.managers;

import com.flash.mongo.model.ApiSpecification;
import com.flash.mongo.model.ResponseValidator;
import com.flash.mongo.model.TestDataModel;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DbValidationManager extends RequestManager {
    public DbValidationManager(String requestId) {
        super(requestId);
    }

    private static final Logger logger = LoggerFactory.getLogger(DbValidationManager.class);

    /**
     * @author Avdhesh Gupta (av-g1)
     * @param testDataModel
     * @param apiSpecification
     * @return
     */

//    public boolean validateDb(TestDataModel testDataModel, ApiSpecification apiSpecification) {
//       Response testResponse = execute(apiSpecification, testDataModel);
//        boolean flag = true;
//        if(testDataModel.getExpectedJsonResponse() != null)
//         flag =  compareJson(testDataModel.getExpectedJsonResponse(), testResponse.asString(), testDataModel.getIgnore());
//        return flag && testDataModel.getValidator().stream().map(responseValidator -> {
//            switch (responseValidator.getCriteria()) {
//                case COUNT:
//                    return validateRecordsCount(testDataModel, responseValidator);
//                case COMPARE_FIELDS:
//                    return validateFieldsValue(testDataModel, responseValidator);
//            }
//            return false;
//        }).reduce(Boolean::logicalAnd).orElseGet(() -> {
//            logger.error("error evaluating validators ");
//            return false;
//        });
//    }
    public boolean validateDb(TestDataModel testDataModel, ApiSpecification apiSpecification) {
        Response testResponse = execute(apiSpecification, testDataModel);

        boolean isJsonValidated = true;

        if (testDataModel.getExpectedJsonResponse() != null) {
            isJsonValidated = compareJson(resolvePatternString(testDataModel.getExpectedJsonResponse(), testDataModel.getEvaluatedJson().toString()), testResponse.asString(), getComparatorIgnore(testDataModel, apiSpecification));
        }

        if(testDataModel.getValidator() != null){
            isJsonValidated &= validateDBData(testDataModel);
        }
        else if (testDataModel.getExpectedJsonResponse() == null)
        {
            reportHelper.log("info"," validator and expectedJsonResponse both are null");
            logger.info("expectedJsonResponse and validator both are null for {} ",testDataModel.getId());
        }

        return isJsonValidated;
    }

    private boolean validateDBData(TestDataModel testDataModel){
        return testDataModel.getValidator().stream().map(responseValidator -> {
            switch (responseValidator.getCriteria()) {
                case COUNT:
                    return validateRecordsCount( responseValidator);
                case COMPARE_FIELDS:
                    return validateFieldsValue( responseValidator);
            }
            return false;
        }).reduce(Boolean::logicalAnd).orElseGet(() -> {
            logger.error("error evaluating validators ");
            return false;
        });
    }

//    private boolean validateExpectedJson(TestDataModel testDataModel, ApiSpecification apiSpecification){
//        try {
// //           Response testResponse = execute(apiSpecification, testDataModel);
//            String stableResponse = testDataModel.getExpectedJsonResponse();
//            return compareJson(stableResponse, testResponse.asString(), getComparatorIgnore(testDataModel, apiSpecification));
//        } catch (Exception e) {
//            logger.error(Arrays.toString(e.getStackTrace()));
//            return false;
//        }
  //  }

    /**
     * @author Avdhesh Gupta (av-g1)

     * @param responseValidator
     * @return boolean
     * @description verifies the size of  data structure  returned from db  with the provided value
     */
    public boolean validateRecordsCount( ResponseValidator responseValidator) {
        boolean status = false;
        if (responseValidator == null || responseValidator.getCount() == null) {
            reportHelper.fail("responsevalidator/count is null in db data");
            logger.error("responsevalidator/count is null in db data");
            return status;
        }
        int expectedCount = responseValidator.getCount(), actualCount = 0;
        actualCount = ((List<Map<String, Object>>) getDataFromDb(responseValidator.getDbSpec())).size();
        reportHelper.log("info",
                " expecting database, "+responseValidator.getDbSpec().getDatabaseName()+" to return "+expectedCount +
                        " record(s) found "+actualCount +" " +
                "record(s)");
        status = actualCount == responseValidator.getCount();

        if (!status) {
            reportHelper.fail("expected and actual count mismatch while validating db data, expected count : " + expectedCount + " actual count " + actualCount);
            logger.error("Mismatch in db count expected count is {}, foun {}", expectedCount, actualCount);
        }
        return status;
    }


    /**
     * @author Avdhesh Gupta (av-g1)
     * @param responseValidator
     * @return boolean
     * @description compares value of db fields with provided values
     */
    public boolean validateFieldsValue( ResponseValidator responseValidator) {
        boolean status = false;
        if (responseValidator == null || responseValidator.getFields() == null) {
            reportHelper.fail("responsevalidator/fields is null in db data");
            logger.error("responsevalidator/fields is null in db data");
            return status;
        }
        return responseValidator.getFields().entrySet().stream().map(map -> {
            boolean flag;
            List<Map<String, Object>> list = getDataFromDb(responseValidator.getDbSpec());
            flag = list.size() != 0;
            flag &= list.get(0).containsKey(map.getKey());
            Object evaluatedVal = readJsonPath(this.evaluatedJson.toString(),
                    map.getValue());
            flag &= list.get(0).get(map.getKey()).equals(evaluatedVal);
            if (!flag) {
                reportHelper.fail("Validating field " + map.getKey() + " expected value : " + evaluatedVal + " actual" +
                        " " +
                        "value : " + (list.isEmpty() ? "null" : list.get(0).get(map.getKey())));
            }else
                reportHelper.log("info",
                        "expecting value of field "+map.getKey() +" : "+evaluatedVal+" found : "+list.get(0).get(map.getKey()));
            return flag;
        }).reduce(Boolean::logicalAnd).orElseGet(() -> {
            reportHelper.fail("Error validating fields ");
            logger.error("Error validating fields ");
            return false;
        });
    }


}