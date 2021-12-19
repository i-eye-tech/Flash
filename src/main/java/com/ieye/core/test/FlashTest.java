package com.ieye.core.test;

import com.ieye.FlashApplication;
import com.ieye.core.lib.TestManager;
import com.ieye.model.core.ActionType;
import com.ieye.model.core.RestSpecification;
import com.ieye.model.core.TestDataModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

@SpringBootTest(classes = FlashApplication.class)
@Slf4j
public class FlashTest extends BaseTest {

    @Autowired
    TestManager testManager;

    @Test(dataProvider = "testData")
    void genericValidatorTest(TestDataModel testDataModel) {
        log.debug("{} - Staring test method for {} in thread {}", currentTest.getRequestId(),
                currentTest.getTestId(), Thread.currentThread().getId());
        try {
            RestSpecification r1 = restManager.createRestSpecification(apiSpecification, testDataModel);

            String actual = restHelper.execute(r1).asString();
            String expected;

            if(testDataModel.getExpectedJson() == null) {
                RestSpecification r2 = new RestSpecification(r1);
                r2.setBasePath(apiSpecification.getStableDomain());
                expected = restHelper.execute(r2).asString();
            } else {
                expected = r1.getExpectedJson();
            }
            testManager.compareResponse(expected, actual);
            testManager.validate(testDataModel.getValidator(), actual);
            log.debug("{} - test method finished for {}", currentTest.getRequestId(), currentTest.getTestId());
        } catch (Exception | AssertionError e) {
            log.info("{} -  Exception in test {} for data {} & requestId {}. Exception: {}", currentTest.getRequestId(),
                    currentTest.getTestId(), testDataModel, currentTest.getRequestId(), e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            log.debug("test method finished for {}", currentTest.getTestId());
            throw e;
        }
    }

}
