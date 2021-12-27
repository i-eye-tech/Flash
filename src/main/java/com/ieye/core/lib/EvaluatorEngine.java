package com.ieye.core.lib;

import com.ieye.core.helper.CustomEvaluators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public final class EvaluatorEngine extends CustomEvaluators {

    private String generateUUID(String... args) {
        return UUID.randomUUID().toString();
    }

    private String getRandomString(String... args) {
        int i = args.length == 1 ? Integer.parseInt(args[0]) : 10;
        return RandomStringUtils.randomAlphanumeric(i);
    }

    private String stringAppend(String... args) {
        String value = args[0];
        final int MSB = 0x80000000;
        SecureRandom ng = new SecureRandom();
        return String.format(value, Integer.toHexString(MSB | ng.nextInt()) + Integer.toHexString(MSB | ng.nextInt()));
    }

    private int sum(String... args) {
        return Arrays.stream(args).mapToInt(Integer::parseInt).sum();
    }

    public Object evaluate(String evaluatorName, String... args) {
        if(evaluatorName == null || evaluatorName.trim().isEmpty())
            return args[0];

        try {
            return this.getClass().getDeclaredMethod(evaluatorName, String[].class).invoke(this, (Object) args);
        } catch (NoSuchMethodException e) {
            log.error("{} - Evaluator \"{}\" not found.", currentTest.getRequestId(), evaluatorName);
            return args[0];
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("{} - Unable to execute evaluator \"{}\". Exception: {}", currentTest.getRequestId(), evaluatorName,
                    e.getMessage());
            return args[0];
        }

    }


}
