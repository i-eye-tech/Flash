package com.ieye.core.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.ieye.core.lib.currenttest.CurrentTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Evaluator {

    @Autowired
    CurrentTest currentTest;

    public String evaluate(String s1, JsonNode jsonNode) {
        String json = jsonNode.toString();
        if(!isAnExpression(s1))
            return s1;
        return "";
    }

    public String evaluate(String s1) {
        return evaluate(s1, currentTest.getData());
    }

    private String resolveUsingEvaluator(String s1, String json) {
        return "";
    }

    private String resolveUsingData(String s1, String json) {
        return "";
    }

    private boolean isEvaluatorExpression(String s1) {
        return s1.startsWith("${evaluate.");
    }

    private boolean isNonEvaluatorExpression(String s1) {
        return s1.startsWith("${data.");
    }

    private boolean isAnExpression(String s1) {
        if (s1 == null || s1.isEmpty())
            return false;
        return isEvaluatorExpression(s1) || isNonEvaluatorExpression(s1);
    }

}
