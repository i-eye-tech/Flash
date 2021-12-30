package com.ieye.core.lib;

import com.ieye.core.helper.JsonComparator;
import com.ieye.core.helper.Reporter;
import com.ieye.core.helper.RestHelper;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.*;

@Component
@Slf4j
public class TestManager {

    @Autowired CurrentTest currentTest;
    @Autowired Reporter reporter;
    @Autowired PatternResolver patternResolver;
    @Autowired DatabaseManager databaseManager;
    @Autowired RestHelper restHelper;
    @Autowired JsonComparator comparator;

    public boolean compareResponse(ApiSpecification apiSpecification, TestDataModel testDataModel,
                                   RestSpecification r1, String response) {
        if(apiSpecification.getStableDomain() == null && testDataModel.getExpectedJson() == null)
            return true;
        log.debug("{} - Start of method compare response for test {}", currentTest.getRequestId(), currentTest.getTestId());
        String expected;
        if(testDataModel.getExpectedJson() == null && apiSpecification.getStableDomain() != null) {
            RestSpecification r2 = new RestSpecification(r1);
            r2.setBasePath(apiSpecification.getStableDomain());
            expected = restHelper.execute(r2).asString();
        } else {
            expected = r1.getExpectedJson();
        }
        log.debug("{} - End of method compare response for test {}", currentTest.getRequestId(), currentTest.getTestId());
        return comparator.compareJson(expected, response, null, null);
    }

    public boolean validate(List<ValidatorTemplate> validators, String response) {
        if(validators == null || validators.isEmpty())
            return true;

        log.debug("{} - Start of validate method for test {}.", currentTest.getRequestId(), currentTest.getTestId());

        boolean result = validators.stream().map(validator -> {
            switch (validator.getType()) {
                case REST:
                    return validateRestAction(validator, response);
                case GET_DATA:
                case QUERY_DATABASE:
                    return validator.getTimeout() > 0 ? validateDBAction(validator, true)
                            : validateDBAction(validator);
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

        log.debug("{} - Start of method validate db for test {}", currentTest.getRequestId(), currentTest.getTestId());
        boolean result = true;

        if(validator.getCount() != null) {
            int rows = getRowCount(validator);
            if(rows == validator.getCount())
                reporter.info(currentTest.getExtentTest(),
                        String.format("Expecting database %s to return %d record(s) and found %s record(s).",
                                validator.getDatabase(), validator.getCount(), rows));
            else {
                result = false;
                reporter.fail(currentTest.getExtentTest(),
                        String.format("Expecting database %s to return %d record(s) and found %s record(s).",
                                validator.getDatabase(), validator.getCount(), rows));
            }
        }

        if(validator.getFields() == null || validator.getFields().isEmpty())
            return result;

        List<String[]> r = compareFields(validator);
        result = r.stream().anyMatch(n -> n[3].equalsIgnoreCase("pass"));
        reporter.info(currentTest.getExtentTest(), r.toArray(new String[0][]));
        log.debug("{} - End of method validate db for test {}", currentTest.getRequestId(), currentTest.getTestId());
        return result;
    }

    private boolean validateDBAction(ValidatorTemplate validator, boolean await) {
        if(validator.getDatabase() == null)
            return true;

        if(validator.getCount() == null && (validator.getFields().isEmpty() || validator.getFields() == null))
            return true;

        log.debug("{} - Start of method validate db for test {}", currentTest.getRequestId(), currentTest.getTestId());

        // Verify row count
        boolean result = true;
        AtomicInteger rowCount = new AtomicInteger();
        if (validator.getCount() != null) {
            try {
                await().pollInSameThread()
                        .pollDelay(Duration.ofSeconds(validator.getDelay()))
                        .pollInterval(Duration.ofSeconds(validator.getInterval()))
                        .atMost(Duration.ofSeconds(validator.getTimeout()))
                        .until(() -> {
                            rowCount.set(getRowCount(validator));
                            return validator.getCount() == rowCount.get();
                        });

                reporter.info(currentTest.getExtentTest(),
                        String.format("Expecting database %s to return %d record(s) and found %s record(s).",
                                validator.getDatabase(), validator.getCount(), rowCount.get()));

            } catch (Exception e) {
                result = false;
                reporter.fail(currentTest.getExtentTest(),
                        String.format("Mismatch in expected & actual rows count. Expected: %d, Actual: %d",
                                validator.getCount(), rowCount.get()));
            }
        }

        if(validator.getFields() == null || validator.getFields().isEmpty())
            return result;

        // Check if query has been executed earlier
        // and no rows has been found.
        if(validator.getCount() != null && rowCount.get() == 0)
            return false;

        // Verify field values

        List<String[]> comparisonList = new ArrayList<>();
        try {
            await().pollInSameThread()
                    .pollDelay(Duration.ofSeconds(validator.getDelay()))
                    .pollInterval(Duration.ofSeconds(validator.getInterval()))
                    .atMost(Duration.ofSeconds(validator.getTimeout()))
                    .until(() -> {
                        List<String[]> r = compareFields(validator);
                        comparisonList.addAll(0, r);
                        return r.stream().anyMatch(n -> n[3].equalsIgnoreCase("pass"));
                    });
        } catch (Exception e) {
            result = false;
        }
        reporter.info(currentTest.getExtentTest(), comparisonList.toArray(new String[0][]));
        log.debug("{} - End of method validate db for test {}", currentTest.getRequestId(), currentTest.getTestId());
        return result;
    }

    private List<String[]> compareFields(ValidatorTemplate validatorTemplate) {
        List<Map<String, Object>> rows = databaseManager.getDatafromDB(validatorTemplate.getDatabase());
        List<String[]> result = new ArrayList<>();
        result.add(new String[] {"Field", "Expected", "Actual", "Result"});
        validatorTemplate.getFields().forEach((key, value) -> {
            boolean flag = rows.get(0).containsKey(key);
            if (flag) {
                String expected = patternResolver.resolve(value.toString(), currentTest.getData());
                String actual = rows.get(0).get(key).toString();
                flag = actual.equals(expected);

                result.add(new String[]{key, expected, actual,
                        "<p style=\"font-weight: bold;\" class=\"" + (flag ? "badge log pass-bg"
                                : "badge log fail-bg") + "\">" + (flag ? "PASS" : "FAIL")});
            }
        });
        return result;
    }

    private int getRowCount(ValidatorTemplate validator) {
        List<Map<String, Object>> rows = databaseManager.getDatafromDB(validator.getDatabase());
        return rows.size();
    }

    private boolean validateRestAction(ValidatorTemplate validator, String response) {
        if(validator.getFields().isEmpty())
            return true;

        log.debug("{} - Start of method validate rest for test {}.", currentTest.getRequestId(), currentTest.getTestId());

        List<String[]> comparisonList = new ArrayList<>();
        comparisonList.add(new String[] {"Field", "Expected", "Actual", "Result"});
        boolean result = validator.getFields().entrySet().stream().map(map -> {
            boolean flag;
            Object expected = patternResolver.resolve(map.getValue().toString(), currentTest.getData());
            String actual = patternResolver.readJsonPath(response, map.getKey());
            flag = actual.equals(expected);
            comparisonList.add(new String[] {map.getKey(), expected.toString(), actual,
                    "<p style=\"font-weight: bold;\" class=\"" + (flag ? "badge log pass-bg" : "badge log fail-bg") + "\">" +
                            (flag ? "PASS" : "FAIL" )});
            return flag;
        }).reduce(Boolean::logicalAnd).orElse(false);
        if(comparisonList.size() > 1)
            reporter.info(currentTest.getExtentTest(), comparisonList.toArray(new String[0][]));
        log.debug("{} - End of method validate rest for test {}.", currentTest.getRequestId(), currentTest.getTestId());
        return result;
    }

}
