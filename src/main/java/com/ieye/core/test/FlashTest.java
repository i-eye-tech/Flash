package com.ieye.core.test;

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

@SpringBootTest
@Slf4j
public class FlashTest extends BaseTest {

    @Autowired
    RestHelper restHelper;

    @Autowired
    RestManager restManager;

    @Autowired
    CurrentTest currentTest;

    @Test(dataProvider = "testData")
    void genericValidatorTest(TestDataModel testDataModel) {
        log.debug("Staring test method for {}", currentTest.getTestId());
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

        } catch (Exception | AssertionError e) {
            log.info("Exception in test {} from request {} for data {} & requestId {}. Exception: {}", currentTest.getTestId(),
                    currentTest.getRequestId(), testDataModel, currentTest.getTestId(), e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            throw e;
        }

        log.debug("test method finished for {}", currentTest.getTestId());
    }

}
