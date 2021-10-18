package com.flash.service;

import com.flash.mongo.model.*;
import com.flash.service.managers.*;
import org.slf4j.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

public class GenericTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GenericTest.class);


    @BeforeClass
    public void setRequestManager() {
        try {
            baseApiManager = new RequestManager(requestId);
            testCollection =
                    BaseApiManager.getMongoHelperInstance().getMongoCollection("flash",
                            testCollectionName);
        }catch (Exception e){
            logger.error("Exception occurred while creating manager and mongo instance for requestId {}, nested exception is {}",requestId,e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * @param testDataModel
     * @author Avdhesh Gupta (av-g1)
     */

    @Test(dataProvider = "getData")
    void validateApiComparator(TestDataModel testDataModel) {
        try {
            // test
            Request r1 = ((RequestManager)baseApiManager).createRequest(apiSpecification, testDataModel);
            String testResponse = ((RequestManager)baseApiManager).execute(r1).asString();
            String stableResponse;
            if(testDataModel.getExpectedJsonResponse() == null) {
                Request r2 = new Request(r1);
                r2.setBasePath(apiSpecification.getStableDomain());
                stableResponse = baseApiManager.execute(r2).asString();
            } else {
                stableResponse = testDataModel.getExpectedJsonResponse();
            }
            assertTrue(baseApiManager.compareJson(stableResponse, testResponse, baseApiManager.getComparatorIgnore(testDataModel,apiSpecification)));
        } catch (Exception e) {
            logger.info("Exception while starting up test for test data {} and requestId {}, Exception is {}", testDataModel, requestId, e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }

}
