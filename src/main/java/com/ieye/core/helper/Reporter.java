package com.ieye.core.helper;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.ieye.core.lib.currenttest.CurrentTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Reporter {

    private final ExtentReports extentReport = new ExtentReports();
    private String reportName;

    @Autowired
    CurrentTest currentTest;

    public void generate(String requestId) {
        reportName = requestId + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("result/" + reportName);
        extentReport.attachReporter(sparkReporter);
        flush();
    }

    public void createTest(String testId, String description) {
        ExtentTest test = extentReport.createTest(testId, description);
        currentTest.setExtentTest(test);
    }

    public void pass(String msg) { currentTest.getExtentTest().pass(msg); }

    public void pass() { currentTest.getExtentTest().pass("Pass"); }

    public void info(String s) { currentTest.getExtentTest().info(s); }

    public void flush() { extentReport.flush(); }

    public String getReportName() { return reportName; }


}
