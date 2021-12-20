package com.ieye.core.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.helper.Reporter;
import com.ieye.core.helper.RestHelper;
import com.ieye.core.helper.database.MongoHelper;
import com.ieye.core.lib.ActionExecutioner;
import com.ieye.core.lib.RestManager;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.ApiSpecification;
import com.ieye.model.core.TestDataModel;
import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.List;

@Slf4j
abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired protected MongoHelper mongoHelper;
    @Autowired CurrentTest currentTest;
    @Autowired Reporter reporter;
    @Autowired RestHelper restHelper;
    @Autowired RestManager restManager;
    @Autowired ActionExecutioner actionExecutioner;

    protected ApiSpecification apiSpecification;

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

    @DataProvider(parallel = true)
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
        reporter.flush(requestId);
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

    @BeforeMethod
    @Parameters("requestId")
    protected void executePreActions(String requestId, Object[] args) {
        TestDataModel testDataModel = (TestDataModel) args[0];
        if(testDataModel.getPreSteps() == null || testDataModel.getPreSteps().isEmpty())
            return;
        log.debug("{} - Pre execution started {}.", requestId, currentTest);
        try {
            actionExecutioner.execute(apiSpecification, testDataModel.getPreSteps());
        } catch (Exception | AssertionError e) {
            log.error("{} - Error in pre-execution {}", requestId, e.getMessage());
            reporter.fail(currentTest.getExtentTest(), "Error in pre-execution");
            reporter.fail(currentTest.getExtentTest(), e);
        }
        log.debug("{} - Pre execution finished {}.", requestId, currentTest);
    }

    @AfterMethod
    @Parameters("requestId")
    protected void executePostActions(String requestId, Object[] args) {
        TestDataModel testDataModel = (TestDataModel) args[0];
        if(testDataModel.getPostSteps() == null || testDataModel.getPostSteps().isEmpty())
            return;
        log.debug("{} - Post execution started {}.", requestId, currentTest);
        try {
            actionExecutioner.execute(apiSpecification, testDataModel.getPostSteps());
        } catch (Exception | AssertionError e) {
            log.error("{} - Error in post-execution {}", requestId, e.getMessage());
            reporter.fail(currentTest.getExtentTest(), "Error in post-execution");
            reporter.fail(currentTest.getExtentTest(), e);
        }
        log.debug("{} - Post execution finished {}.", requestId, currentTest);
    }

    @AfterSuite
    @Parameters("requestId")
    protected void afterSuite(String requestId, ITestContext testContext) {
        int pass = testContext.getPassedTests().size();
        int fail = testContext.getFailedTests().size();
        int skipped = testContext.getSkippedTests().size();
        reporter.remove(requestId);
        log.debug("{} - Test Suite ended.", requestId);
        log.info("{} - Test finished successfully. Total: {}, Pass: {}, Fail: {}, Skipped: {}.",
                requestId, pass + fail + skipped, pass, fail, skipped);
    }

}
