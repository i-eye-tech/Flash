package com.ieye.core.lib;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Configuration
public class ExtentManager {

    @Value("${flash.report.logo:@null}")
    private String logo;

    @Value("${flash.report.prefix:}")
    private String prefix;

    @Value("${flash.report.document.title:Flash API Automation Report}")
    private String documentTitle;

    private static final ConcurrentMap<String, ExtentReports> reports = new ConcurrentHashMap<>();

    public ExtentReports getExtent(String requestId) {
        if(reports.containsKey(requestId))
            return reports.get(requestId);
        else
            synchronized (this) {
                return reports.put(requestId, createReport(requestId));
            }
    }

    private ExtentReports createReport(String requestId) {
        String reportName = prefix + "-" + requestId + ".html";
        log.debug("{} - Creating report {}.", requestId, reportName);
        ExtentReports extentReport = new ExtentReports();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("result/" + reportName)
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[] {ViewName.DASHBOARD, ViewName.TEST, ViewName.LOG}).apply();
        sparkReporter.config().setTimelineEnabled(false);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle(documentTitle);

        sparkReporter.config().setReportName("<img alt=\"Flash\" src=\"" + logo + "\" style=\"width: auto;\"></img>");

        extentReport.attachReporter(sparkReporter);
        extentReport.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReport.setSystemInfo("Request Id", requestId);
        extentReport.flush();
        log.info("{} - Report {} generated successfully.", requestId, reportName);
        return extentReport;
    }

    public ExtentReports remove(String requestId) {
        return reports.remove(requestId);
    }

}
