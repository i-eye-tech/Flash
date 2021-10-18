package com.flash.service.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public  class ReportHelper {

    public String requestId;
    private static Map<String,ReportHelper> reportHelperMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ReportHelper.class);

    Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
    private ReportHelper(String requestId){
        this.requestId=requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public static synchronized ReportHelper getInstance(String requestId) // considering mongo server at all time should be same
    {
        if (reportHelperMap.containsKey(requestId))
            return reportHelperMap.get(requestId);
        reportHelperMap.put(requestId, new ReportHelper(requestId));
        return reportHelperMap.get(requestId);
    }

    public static void removeInstance(String requestId){
        reportHelperMap.remove(requestId);
    }


    ExtentManager extentManager = new ExtentManager();
    private ExtentReports EXTENT = extentManager.getInstance();

    public  void createTest(String testName, String description) {
        createExtentTest(testName, description);
    }

    public String getFileName(){
        return extentManager.fileName;
    }

    public  void pass() {
        getTest().pass("Pass");
    }

    public  void fail() {
        getTest().fail("Fail");
    }

    public  void fail(String s1) {
        getTest().fail(s1);
    }

    public  void fail(String[][] arr) {
        getTest().fail(MarkupHelper.createTable(arr, "table-sm"));
    }

    public  void fail(Throwable t) {
        getTest().fail(t);
    }

    public  void info(String s1) {
        getTest().info(MarkupHelper.createCodeBlock(s1));
    }

    public  void warn(Throwable t) {
       getTest().warning(t);
    }

    public  void log(String s1, String s2) {
        Status s;
        if (s1.equalsIgnoreCase("pass"))
            s = Status.PASS;
        else if (s1.equalsIgnoreCase("fail"))
            s = Status.FAIL;
        else if (s1.equalsIgnoreCase("warn"))
            s = Status.WARNING;
        else
            s = Status.INFO;

       getTest().log(s, s2);
    }

    public  void logJSON(String s1) {
       getTest().info(MarkupHelper.createCodeBlock(s1, CodeLanguage.JSON));
    }

    public void endReport() {
        EXTENT.flush();
    }

    public  ExtentTest getTest() {
        return extentTestMap.get((int) Thread.currentThread().getId());
    }

    public  ExtentTest createExtentTest(String testName, String description) {
        ExtentTest test = null;
        try{
         test = EXTENT.createTest(testName, description);
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        }catch (Exception e){
            logger.error("Exception occurred while creating test for report , nested exception is {} :  ",e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return test;
    }


}
