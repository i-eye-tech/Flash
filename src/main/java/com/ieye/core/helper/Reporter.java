package com.ieye.core.helper;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.ieye.core.lib.currenttest.CurrentTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class Reporter {

    private final ExtentReports extentReport = new ExtentReports();
    private String reportName;

    @Value("${flash.report.prefix:}")
    private String prefix;

    @Value("${flash.report.logo:@null}")
    private String logo;

    @Value("${flash.report.document.title:Flash API Automation Report}")
    private String documentTitle;

    @Autowired
    CurrentTest currentTest;

    public void generate(String requestId) {
        reportName = prefix + "-" + DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now()) + ".html";
        log.debug("Creating report {} for request {}.", reportName, requestId);
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("result/" + reportName)
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[] {ViewName.DASHBOARD, ViewName.TEST, ViewName.LOG}).apply();
        sparkReporter.config().setTimelineEnabled(false);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle(documentTitle);

        if(logo != null && !logo.isBlank())
            sparkReporter.config().setReportName("<img alt=\"Flash\" src=\"" + logo + "\" style=\"width: auto;\"></img>");

        extentReport.attachReporter(sparkReporter);
        extentReport.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReport.setSystemInfo("RequestId", requestId);
        flush();
        log.debug("Report {} generated successfully for request {}.", reportName, requestId);
    }

    public ExtentTest createTest(String testId, String description) {
        log.debug("Adding test for {} from request {}.", testId, currentTest.getRequestId());
        return extentReport.createTest(testId, description);
    }

    public void pass(String msg) { currentTest.getExtentTest().pass(msg); }

    public void pass() {
        currentTest.getExtentTest().pass("Pass");
    }

    public void fail(Object o) {
        if(o instanceof Throwable)
            currentTest.getExtentTest().fail((Throwable) o);
        else if(o instanceof String)
            currentTest.getExtentTest().fail(String.valueOf(o));
        else if(o instanceof String[][])
            currentTest.getExtentTest().fail(MarkupHelper.createTable(((String[][]) o)));
    }

    public void info(String s) { currentTest.getExtentTest().info(MarkupHelper.createCodeBlock(s)); }

    public void flush() {
        extentReport.flush();
    }

    public String getReportName() { return reportName; }


}
