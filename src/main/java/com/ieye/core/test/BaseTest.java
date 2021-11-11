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
import org.testng.annotations.*;

import java.util.List;

@Slf4j
abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected Reporter reporter;

    @Autowired
    protected MongoHelper mongoHelper;

    @Autowired
    protected CurrentTest currentTest;

    @BeforeClass(alwaysRun = true)
    @BeforeSuite(alwaysRun = true)
    @Override
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
    }

    @BeforeSuite
    @Parameters("requestId")
    protected void zBeforeSuite(String requestId) { reporter.generate(requestId); }

    @DataProvider(parallel = true)
    protected Object[][] testData(ITestContext iTestContext) {
        ApiSpecification apiSpecification;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String sApiSpecification = iTestContext.getCurrentXmlTest().getXmlClasses().get(0).getAllParameters()
                    .get("apiSpecification");
            apiSpecification = mapper.readValue(sApiSpecification, ApiSpecification.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessing Exception {} in data provider", e.getMessage());
            return new Object[0][0];
        }

        BasicDBObject basicDBObject = new BasicDBObject("_id.testDataId", apiSpecification.getId())
                .append("active", true);

        List<TestDataModel> testDataModelList = mongoHelper.getDataAsListOfMap(apiSpecification.getTestCollection(), basicDBObject);
        Object[][] output = new Object[testDataModelList.size()][1];
        for(int i=0; i< testDataModelList.size(); i++)
            output[i][0] = mapper.convertValue(testDataModelList.get(i), TestDataModel.class);

        return output;
    }

    @BeforeMethod
    @Parameters("requestId")
    protected void createTest(String requestId, Object[] args){
        TestDataModel testDataModel = (TestDataModel) args[0];
        reporter.createTest(testDataModel.getId().getTestCaseId(), testDataModel.getDescription());
        currentTest.setRequestId(requestId);
        currentTest.setTestId(testDataModel.getId().getTestCaseId());
    }

    @AfterMethod
    protected void afterMethod() {
        reporter.pass();
    }

}
