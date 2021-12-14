package com.ieye.core.test;

import com.ieye.FlashApplication;
import com.ieye.core.helper.RestHelper;
import com.ieye.core.lib.RestManager;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.RestSpecification;
import com.ieye.model.TestDataModel;
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
    RestHelper restHelper;

    @Autowired
    RestManager restManager;

    @Autowired
    private CurrentTest currentTest;

    @Test(dataProvider = "testData")
    void genericValidatorTest(TestDataModel testDataModel) {
        log.debug("Test Method {}", currentTest);
        log.debug("Staring test method for {} in thread {}", currentTest.getTestId(), Thread.currentThread().getId());
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

            assertEquals(actual, expected);
            log.debug("test method finished for {}", currentTest.getTestId());
        } catch (Exception | AssertionError e) {
            log.info("Exception in test {} from request {} for data {} & requestId {}. Exception: {}", currentTest.getTestId(),
                    currentTest.getRequestId(), testDataModel, currentTest.getRequestId(), e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            log.debug("test method finished for {}", currentTest.getTestId());
            throw e;
        }
    }

}
