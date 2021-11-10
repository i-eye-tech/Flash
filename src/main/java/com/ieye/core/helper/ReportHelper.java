package com.ieye.core.helper;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ReportHelper {

    private final ExtentReports extentReport = new ExtentReports();
    private final ConcurrentMap<Integer, ExtentTest> testInfo = new ConcurrentHashMap<>();
    private String reportName;

    public void generate(String requestId) {
        reportName = requestId + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("result/" + reportName);
        extentReport.attachReporter(sparkReporter);
        flush();
    }

    public void createTest(String testId, String description) {
        testInfo.put((int) Thread.currentThread().getId(), extentReport.createTest(testId, description));
    }

    private ExtentTest getTest() { return testInfo.get((int) Thread.currentThread().getId()); }

    public void pass(String msg) { getTest().pass(msg); }

    public void pass() { getTest().pass("Pass"); }

    public void info(String s) { getTest().info(s); }

    public void flush() { extentReport.flush(); }

    public String getReportName() { return reportName; }


}
