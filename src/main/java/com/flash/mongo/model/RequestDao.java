package com.flash.mongo.model;

import com.flash.constants.TestExecutionStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "requests")
public class RequestDao {

    @Id
    private MappingRequestId id;
    private Integer totalTestCases;
    private Integer passedTestCases;
    private Integer failedTestCases;
    private Integer skippedTestCases;
    private TestExecutionStatus testRunStatus;
    private Integer passPercentage;
    private String testReportUrl;
    private long durationInMs;

    private String testType;

    public RequestDao(MappingRequestId id, TestExecutionStatus testRunStatus, String testType) {
        this.id = id;
        this.testRunStatus = testRunStatus;
        this.testType = testType;
    }

    public MappingRequestId getId() {
        return id;
    }

    public void setId(MappingRequestId id) {
        this.id = id;
    }

    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(Integer totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public Integer getPassedTestCases() {
        return passedTestCases;
    }

    public void setPassedTestCases(Integer passedTestCases) {
        this.passedTestCases = passedTestCases;
    }

    public Integer getFailedTestCases() {
        return failedTestCases;
    }

    public void setFailedTestCases(Integer failedTestCases) {
        this.failedTestCases = failedTestCases;
    }

    public Integer getSkippedTestCases() {
        return skippedTestCases;
    }

    public void setSkippedTestCases(Integer skippedTestCases) {
        this.skippedTestCases = skippedTestCases;
    }

    public TestExecutionStatus getTestRunStatus() {
        return testRunStatus;
    }

    public void setTestRunStatus(TestExecutionStatus testRunStatus) {
        this.testRunStatus = testRunStatus;
    }

    public Integer getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(Integer passPercentage) {
        this.passPercentage = passPercentage;
    }

    public String getTestReportUrl() {
        return testReportUrl;
    }

    public void setTestReportUrl(String testReportUrl) {
        this.testReportUrl = testReportUrl;
    }

    public long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(long durationInMs) {
        this.durationInMs = durationInMs;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }
}
