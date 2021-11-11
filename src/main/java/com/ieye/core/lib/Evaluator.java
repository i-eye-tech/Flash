package com.ieye.core.lib;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {

    public String evaluate(String s1, JsonNode jsonNode) {
        String json = jsonNode.toString();
        if(!isAnExpression(s1))
            return s1;
        return "";
    }

    private String resolveUsingEvaluator(String s1, String json) {
        return "";
    }

    private String resolveUsingData(String s1, String json) {
        return "";
    }

    private boolean isEvaluatorExpression(String s1) {
        return false;
    }

    private boolean isNonEvaluatorExpression(String s1) {
        return false;
    }

    private boolean isAnExpression(String s1) {
        return false;
    }

}
