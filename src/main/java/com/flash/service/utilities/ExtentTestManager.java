package com.flash.service.utilities;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.Map;

public class ExtentTestManager {

    Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
    private ExtentReports EXTENT = new ExtentManager().getInstance();

    public  ExtentTest getTest() {
        return extentTestMap.get((int) Thread.currentThread().getId());
    }

    public  ExtentTest createTest(String testName, String description) {
        ExtentTest test = EXTENT.createTest(testName, description);
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        return test;
    }
}
