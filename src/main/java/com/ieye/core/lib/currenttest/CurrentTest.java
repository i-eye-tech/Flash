package com.ieye.core.lib.currenttest;

import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Scope("testscope")
@Component
public class CurrentTest {

    private String requestId;
    private String testId;
    private JsonNode data = new ObjectMapper().createObjectNode();
    private ExtentTest extentTest;

}