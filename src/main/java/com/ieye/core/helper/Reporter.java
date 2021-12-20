package com.ieye.core.helper;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.ieye.core.lib.ExtentManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class Reporter {

    @Autowired
    ExtentManager extentManager;

    public void createReport(String requestId) {
        extentManager.getExtent(requestId);
        flush(requestId);
    }

    public ExtentTest createTest(String testId, String requestId, String description) {
        log.debug("{} - Adding test for {}", requestId, testId);
        return extentManager.getExtent(requestId).createTest(testId, description);
    }

    public void pass(ExtentTest test, String msg) { test.pass(msg); }

    public void pass(ExtentTest test) {
        test.pass("Pass");
    }

    public void fail(ExtentTest test, Object o) {
        if(o instanceof Throwable)
            test.fail((Throwable) o);
        else if(o instanceof String)
            test.fail(String.valueOf(o));
        else if(o instanceof String[][])
            test.fail(MarkupHelper.createTable(((String[][]) o)));
    }

    public void warn(ExtentTest test, String msg) {
        test.warning(msg);
    }

    public void info(ExtentTest test, String s) { test.info(MarkupHelper.createCodeBlock(s)); }

    public void flush(String requestId) {
        extentManager.getExtent(requestId).flush();
    }

    public void remove(String requestId) {
        extentManager.remove(requestId);
    }

    public String getReportName(String requestId) {
        return extentManager.getReportName(requestId);
    }

}
