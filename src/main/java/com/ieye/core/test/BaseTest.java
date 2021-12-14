package com.ieye.core.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.helper.Reporter;
import com.ieye.core.helper.database.MongoHelper;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.ApiSpecification;
import com.ieye.model.TestDataModel;
import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.List;

@Slf4j
abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected MongoHelper mongoHelper;

    @Autowired
    CurrentTest currentTest;

    protected ApiSpecification apiSpecification;

    @Autowired
    private Reporter reporter;

    @BeforeClass(alwaysRun = true)
    @BeforeSuite(alwaysRun = true)
    @Override
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
    }

    @BeforeSuite
    @Parameters("requestId")
    protected void zBeforeSuite(String requestId) {
        log.debug("{} - Test Suite started.", requestId);
        reporter.createReport(requestId);
    }

    @BeforeClass
    @Parameters("requestId")
    protected void beforeClass(String requestId, ITestContext iTestContext) {
        log.debug("{} - Before Class started.", requestId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String sApiSpecification = iTestContext.getCurrentXmlTest().getXmlClasses().get(0).getAllParameters()
                    .get("apiSpecification");
            apiSpecification = mapper.readValue(sApiSpecification, ApiSpecification.class);
        } catch (JsonProcessingException e) {
            log.error("{} - JsonProcessing Exception {} in data provider", currentTest.getRequestId(), e.getMessage());
        }
        log.debug("{} - Before Class finished.", requestId);
    }

    @DataProvider(parallel = false)
    protected Object[][] testData(ITestContext iTestContext) {
        log.debug("DataProvider method started.");
        ObjectMapper mapper = new ObjectMapper();
        BasicDBObject basicDBObject = new BasicDBObject("_id.testDataId", apiSpecification.getId())
                .append("active", true);

        List<TestDataModel> testDataModelList = mongoHelper.getDataAsListOfMap(apiSpecification.getTestCollection(),
                basicDBObject);
        Object[][] output = new Object[testDataModelList.size()][1];
        for(int i=0; i< testDataModelList.size(); i++)
            output[i][0] = mapper.convertValue(testDataModelList.get(i), TestDataModel.class);
        log.debug("DataProvider method finished.");
        return output;
    }

    @BeforeMethod
    @Parameters("requestId")
    protected void createTest(String requestId, Object[] args) {
        log.debug("{} - Before Method started.", requestId);
        TestDataModel testDataModel = (TestDataModel) args[0];
        currentTest.setRequestId(requestId);
        currentTest.setTestId(testDataModel.getId().getTestCaseId());
        currentTest.setExtentTest(reporter.createTest(currentTest.getTestId(), requestId,
                testDataModel.getDescription()));
        log.debug("{} - Before Method finished {}.", requestId, currentTest);
    }

    @AfterMethod
    protected void afterMethod(ITestResult result) {
        log.debug("{} - After Method started {}.", currentTest.getRequestId(), currentTest);
        if(result.isSuccess())
            reporter.pass(currentTest.getExtentTest());
        else {
            log.debug("Error => {}", result.getThrowable().getMessage());
            reporter.fail(currentTest.getExtentTest(), result.getThrowable().getMessage());
            reporter.fail(currentTest.getExtentTest(), result.getThrowable());
        }
        reporter.flush(currentTest.getRequestId());
        log.debug("{} - After Method finished {}.", currentTest.getRequestId(), currentTest);
    }

    @AfterSuite
    @Parameters("requestId")
    protected void afterSuite(String requestId) {
        reporter.remove(requestId);
        log.debug("{} - Test Suite ended.", requestId);
    }

}
