package com.flash.service.utilities;

import com.flash.service.BaseApiManager;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.flash.constants.TestExecutionStatus;
import com.flash.mongo.model.TestDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReportingService  {


    public String requestId;
    public ReportHelper reportHelper ;
    public MongoDatabase mongoDatabase = BaseApiManager.getMongoHelperInstance().getMongoDatabase("flash");;
    private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);


    @BeforeMethod()
    protected void createTestInReport(Method method,Object[] testArgs) throws IOException {
        try {
            String description = null;
            String testCaseName= method.getName();
            for (Object o : testArgs) {
                if(o instanceof  TestDataModel) {
                    if (!((TestDataModel) o).getId().getTestCaseId().isEmpty())
                        testCaseName = ((TestDataModel) o).getId().getTestCaseId();
                    if (((TestDataModel) o).getTestMeta() != null && ((TestDataModel) o).getTestMeta().getTcDescription() != null && ((TestDataModel) o).getTestMeta().getTcDescription().containsKey("TCDESCRIPTION"))
                        description = ((TestDataModel) o).getTestMeta().getTcDescription().get("TCDESCRIPTION");
                }
            }
            logger.info(" test case Id {}, description {}",testCaseName, description);
            reportHelper.createTest(testCaseName, description);
        } catch (Exception e) {
            logger.error("Exception occurred while creating test in beforeMethod for requestId {}, nested exception is {} : ",requestId,e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }

    }

    @AfterMethod()
    protected void afterMethod(ITestResult testResult) {
        try{
            if (testResult.isSuccess())
                reportHelper.pass();
            else
                reportHelper.fail(testResult.getThrowable());
        } catch (Exception e) {
            logger.error("Exception occurred while executing test pass fail in afterMethod for requestId {}, nested exception is {}",requestId,e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     *
     * @param iTestContext
     * @author Avdhesh Gupta (av-g1)
     */
    @AfterSuite()
    protected void afterSuite(ITestContext iTestContext) {


        double passTests=0,failTests=0, skipTests=0 ;
        long duration=0;

        try {
            reportHelper.endReport();
            for (ISuiteResult iSuiteResult : iTestContext.getSuite().getResults().values()) {
                passTests += iSuiteResult.getTestContext().getPassedTests().size();
                failTests += iSuiteResult.getTestContext().getFailedTests().size();
                skipTests += iSuiteResult.getTestContext().getSkippedTests().size();
                duration += iSuiteResult.getTestContext().getEndDate().getTime() - iSuiteResult.getTestContext().getStartDate().getTime();
            }
        }
        catch (Exception e){
            logger.error("Exception occurred while getting results for requestId {}, nested exception is : {}",requestId, e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }

        double totalTests=passTests+failTests+skipTests;
        double passPercentage = (passTests / totalTests) * 100;
        try {
            updateRequest(requestId, totalTests, passTests, failTests, skipTests, passPercentage,duration);
        }catch (Exception e){
            logger.error("Exception occurred while updating request in mongo for rqeuestId {} with exception {}",requestId,e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        ReportHelper.removeInstance(requestId);
    }



    /**
     *
     * @param uuid
     * @param totalTestCases
     * @param passedTestCases
     * @param failedTestCases
     * @param skippedTestCases
     * @param passPercentage
     * @author Avdhesh Gupta (av-g1)
     */
    public void updateRequest( String uuid, Double totalTestCases, Double passedTestCases, Double failedTestCases, Double skippedTestCases, Double passPercentage, long duration)  {

        BasicDBObject updateFields = null;
        try {
            String reportUrl=S3Helper.getInstance().uploadFileToS3(reportHelper.getFileName());
            updateFields = new BasicDBObject("totalTestCases", totalTestCases).append("passedTestCases", passedTestCases).append("failedTestCases", failedTestCases).append("skippedTestCases", skippedTestCases).append("passPercentage", passPercentage).append("testRunStatus", TestExecutionStatus.COMPLETED.toString()).append("testReportUrl",reportUrl).append("durationInMs",duration);
        } catch (Exception e) {
            logger.error("Exception occured while getting host details, nested exception is {}",e.getMessage());
        }
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", updateFields);
        try {
            mongoDatabase.getCollection("requests").updateOne(new BasicDBObject("_id.requestId", uuid), updateObject);

        } catch (Exception e) {
            logger.error("Exception occurred while updating mongo request for requestId {} with update object {}, nested exception is {} ",requestId,updateObject,e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }


}
