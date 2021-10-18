package com.flash.service.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import java.time.LocalDateTime;

public class ExtentManager {
    private  ExtentReports extent;
    String fileName;

    public ExtentReports getInstance() {
        if (extent == null){
            this.fileName="Results/Flash_"+ LocalDateTime.now().toString()+".html";
            extent = createInstance(fileName);
        }

        return extent;
    }

    public ExtentReports createInstance(String fileName) {
        ExtentSparkReporter spark = new ExtentSparkReporter(fileName)
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST, ViewName.LOG } )
                .apply();
        spark.config().setReportName("<img alt=\"Flash\" src=\"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQASsREEOQPOhktdRXJEpWp6V-mx1zjjN3dbA&usqp=CAU\" style=\"width: 30%;\"");
        spark.config().setDocumentTitle("FLASH AUTOMATION EXECUTION REPORT");
        spark.config().setTimelineEnabled(false);
        spark.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("os", System.getProperty("os.name"));

        return extent;
    }

    public void endReport() {
        extent.flush();
    }
}
