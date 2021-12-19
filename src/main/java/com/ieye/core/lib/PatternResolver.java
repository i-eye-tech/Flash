package com.ieye.core.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PatternResolver {

    @Autowired
    CurrentTest currentTest;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String dataPattern = "${data.";
    private final String evaluatorPattern = "${evaluate.";

    public String resolve(String s1, JsonNode jsonNode) {
        if(!isAnExpression(s1))
            return s1;
        s1 = resolveUsingData(s1, jsonNode.toString());
        return resolveUsingEvaluator(s1);
    }

    private String resolveUsingEvaluator(String s1) {
        String s = getExpression(s1);
        String method = getMethodName(s);
        String[] params = getParams(s);

        Object value = null; //TODO: get value from evaluators
        String replacement = "";
        try {
            replacement = value instanceof String ? value.toString() : mapper.writeValueAsString(value);
            s1 = s1.replace(s, replacement);
        } catch (JsonProcessingException e) {
            log.error("{} - Exception while resolving evaluator pattern {}. Exception => {}",
                    currentTest.getRequestId(), method, e.getMessage());
        }
        return s1.contains(evaluatorPattern) ? resolveUsingEvaluator(s1) : s1;
    }

    private String resolveUsingData(String s1, String json) {
        Pattern pattern = Pattern.compile("");
        Matcher matcher = pattern.matcher(s1);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = null;
            try {
                Object value = JsonPath.read(json, matcher.group(1));
                replacement = value instanceof String ? value.toString() : mapper.writeValueAsString(value);
            } catch (Exception e) {
                log.error("{} - Exception while resolving data pattern. Exception => {}",
                        currentTest.getRequestId(), e.getMessage());
            }
            if(replacement == null)
                continue;

            matcher.appendReplacement(buffer, "");
            buffer.append(replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private boolean isEvaluatorExpression(String s1) {
        return s1.startsWith(evaluatorPattern);
    }

    private boolean isNonEvaluatorExpression(String s1) {
        return s1.startsWith(dataPattern);
    }

    private boolean isAnExpression(String s1) {
        if (s1 == null || s1.isEmpty())
            return false;
        return isEvaluatorExpression(s1) || isNonEvaluatorExpression(s1);
    }

    private String getExpression(String s) {
        if(!s.contains(evaluatorPattern))
            return s;

        int index = s.lastIndexOf(evaluatorPattern);
        return s.substring(index, s.indexOf('}', index) + 1);

    }

    private String getMethodName(String s1) {
        int index = s1.indexOf(",");
        return index > 0 ? s1.trim().substring(s1.indexOf("{") + 1, index) : s1;
    }

    private String[] getParams(String s1) {
        if(s1 == null)
            return new String[0];
        int index = s1.indexOf(",");
        String s = index > 0 ? s1.trim().substring(index + 1, s1.length()-1) : "";
        return Arrays.stream(s.split(",")).map(String::trim).toArray(String[]::new);
    }

}
