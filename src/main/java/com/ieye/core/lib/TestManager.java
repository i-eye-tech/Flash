package com.ieye.core.lib;

import com.ieye.core.helper.Reporter;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.ValidatorTemplate;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TestManager {

    @Autowired
    CurrentTest currentTest;

    @Autowired
    Reporter reporter;

    @Autowired
    PatternResolver patternResolver;

    public boolean compareResponse(String expected, String actual) {
        return false;
    }

    public boolean validate(List<ValidatorTemplate> validators, Response response) {
        if(validators == null || validators.isEmpty())
            return true;

        log.debug("{} - Start of validate method for test {}.", currentTest.getRequestId(), currentTest.getTestId());

        boolean result = validators.stream().map(validator -> {
            switch (validator.getType()) {
                case REST:
                    return validateRestAction(validator, response);
                case GET_DATA:
                case QUERY_DATABASE:
                    return validateDBAction(validator);
            }
            return false;
        }).reduce(Boolean::logicalAnd).orElse(false);
        log.debug("{} - End of validate method for test {}.", currentTest.getRequestId(), currentTest.getTestId());
        return result;
    }

    private boolean validateDBAction(ValidatorTemplate validator) {
        if(validator.getDatabase() == null)
            return true;

        if(validator.getCount() == null && (validator.getFields().isEmpty() || validator.getFields() == null))
            return true;

        List<Map<String, Object>> rows = new ArrayList<>(); // TODO: get data from db
        reporter.info(currentTest.getExtentTest(), "Rows returned: \n " + StringUtils.join(rows, "\n"));
        boolean result = true;
        if(validator.getCount() != null) {
            reporter.info(currentTest.getExtentTest(),
                    String.format("Expecting database %s to return %d record(s) and found %s record(s).",
                            validator.getDatabase(), validator.getCount(), rows.size()));
            if(validator.getCount() != rows.size()) {
                reporter.fail(currentTest.getExtentTest(),
                        String.format("Mismatch in expected & actual rows count. Expected: %d, Actual: %d",
                                validator.getCount(), rows.size()));
                result = false;
            }
        }

        if(rows == null || rows.isEmpty())
            return result;

        if(validator.getFields() != null && !validator.getFields().isEmpty()) {
            result &= validator.getFields().entrySet().stream().map(n -> {
                boolean flag = rows.get(0).containsKey(n.getKey());
                if(flag){
                    String expected = patternResolver.resolve(n.getValue().toString(), currentTest.getData());
                    flag = rows.get(0).get(n.getKey()).equals(expected);
                    reporter.info(currentTest.getExtentTest(), String.format("Expected value: %s, Actual value: %s",
                            expected, rows.get(0).get(n.getKey())));
                } else {
                    reporter.fail(currentTest.getExtentTest(), String.format("Field %s not found in query output.", n.getKey()));
                }
                return flag;
            }).reduce(Boolean::logicalAnd).orElse(false);
        }
        return result;
    }

    private boolean validateRestAction(ValidatorTemplate validator, Response response) {
        if(validator.getFields().isEmpty())
            return true;

        return validator.getFields().entrySet().stream().map(map -> {
            boolean flag;
            Object expected = patternResolver.resolve(map.getValue().toString(), currentTest.getData());
            flag = response.jsonPath().getBoolean(map.getKey()) && response.jsonPath().get(map.getKey()).equals(expected);
            if (!flag) {
                reporter.fail(currentTest.getExtentTest(), String.format("Field: %s, Expected value: %s, Actual value: %s",
                        map.getKey(), expected, response.jsonPath().get(map.getKey())));
            } else
                reporter.info(currentTest.getExtentTest(), String.format("Field: %s, Expected value: %s, Actual value: %s",
                        map.getKey(), expected, response.jsonPath().get(map.getKey())));
            return flag;
        }).reduce(Boolean::logicalAnd).orElse(false);
    }

}
