package com.ieye.core.lib.currenttest;

import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;

@Data
@Component
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CurrentTest {

    private String requestId;
    private String testId;
    private JsonNode data = new ObjectMapper().createObjectNode();
    private ExtentTest extentTest;

}