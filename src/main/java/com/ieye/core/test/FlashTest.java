package com.ieye.core.test;

import com.ieye.model.TestDataModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

@SpringBootTest
@Slf4j
public class FlashTest extends BaseTest {

    @Test(dataProvider = "testData")
    void genericValidatorTest(TestDataModel testDataModel) {
        log.info(testDataModel.toString());
    }

}
