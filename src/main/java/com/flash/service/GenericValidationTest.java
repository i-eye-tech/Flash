package com.flash.service;

import com.flash.mongo.model.TestDataModel;
import com.flash.service.managers.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class GenericValidationTest extends BaseTest {


    private static final Logger logger = LoggerFactory.getLogger(GenericTest.class);

    @BeforeClass
    public void setRequestManager() {
        try {
            baseApiManager = new GenericValidator(requestId);
            testCollection =
                    BaseApiManager.getMongoHelperInstance().getMongoCollection("flash",
                            testCollectionName);

        }catch (Exception e){
            logger.error("Exception occurred while creating manager and mongo instance for requestId {}, nested exception is {}",requestId,e.getMessage());
        }
    }

    @Test(dataProvider = "getData")
    void validateApiComparator(TestDataModel testDataModel) {
        try {
            assertTrue(((GenericValidator)baseApiManager).responseValidation(testDataModel, apiSpecification));
        } catch (Exception e) {
            logger.info("Exception while starting up test for test data {} and requestId {}, Exception is {}", testDataModel, requestId, e.getMessage());
            throw e;
        }
    }
}
