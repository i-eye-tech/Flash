package com.ieye.core.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.helper.ReportHelper;
import com.ieye.core.helper.database.MongoHelper;
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
    protected ReportHelper reportHelper;

    @Autowired
    protected MongoHelper mongoHelper;

    @BeforeClass(alwaysRun = true)
    @BeforeSuite(alwaysRun = true)
    @Override
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
    }

    @BeforeSuite
    @Parameters("requestId")
    protected void zBeforeSuite(String requestId) { reportHelper.generate(requestId); }

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
    protected void createTest(Object[] args){
        TestDataModel testDataModel = (TestDataModel) args[0];
        reportHelper.createTest(testDataModel.getId().getTestCaseId(), testDataModel.getDescription());
    }

    @AfterMethod
    protected void afterMethod() {
        reportHelper.pass();
    }

}
