package com.ieye.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.ieye.core.lib.currenttest.CurrentTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Slf4j
public class JsonComparator {

    @Autowired
    CurrentTest currentTest;

    @Autowired
    Reporter reporter;

    public boolean compareJson(String expected, String actual, Set<String> ignoreFields, String ignoreOperation) {
        log.debug("{} - Start of method compare json for test {}.", currentTest.getRequestId(), currentTest.getTestId());

        ObjectMapper objectMapper = new ObjectMapper();
        int differenceCounter = 0;
        List<String[]> logList = new ArrayList<>();
        logList.add(new String[]{"Operation", "Json Path", "Actual", "Expected"});
        try {
            JsonNode actualJson = objectMapper.readTree(actual);
            JsonNode expectedJson = objectMapper.readTree(expected);
            JsonNode jsonDiff = JsonDiff.asJson(expectedJson, actualJson);

            for (JsonNode node : jsonDiff) {
                String diffPath = node.get("path").asText();
                String operationType = node.get("op").asText();

                if ((ignoreFields == null || !ignoreFieldOperation(ignoreFields, diffPath))
                        && !operationType.equalsIgnoreCase(ignoreOperation)) {

                    differenceCounter++;

                    JsonNode diffValueJsonNode = node.get("value");

                    String diffValue = Optional.ofNullable(diffValueJsonNode)
                            .map(dataValue -> diffValueJsonNode.toPrettyString()).orElse(null);

                    String parentValue = Optional.ofNullable(expected)
                            .map(dataValue -> expectedJson.at(diffPath).toPrettyString()).orElse(null);

                    logList.add(new String[]{getPreString(operationType), getPreString(diffPath), getPreString(diffValue),
                            getPreString(parentValue)});

                }
            }

            if (logList.size() > 1)
                reporter.fail(currentTest.getExtentTest(), logList.toArray(new String[0][]));

            if (differenceCounter > 0)
                return false;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.debug("{} - End of method compare json for test {}.", currentTest.getRequestId(), currentTest.getTestId());
        return true;
    }

    private boolean ignoreFieldOperation(Set<String> ignoreProps, String field) {
        boolean ignoreField = false;
        for (String ignoreProperty : ignoreProps) {
            if (Pattern.matches(ignoreProperty, field)) {
                ignoreField = true;
                break;
            }
        }
        return ignoreField;
    }

    private String getPreString(String val) {
        return String.format("<pre>%s</pre>", val);
    }
}
