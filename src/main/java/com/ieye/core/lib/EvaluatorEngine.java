package com.ieye.core.lib;

import com.ieye.core.helper.CustomEvaluators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
@Slf4j
public final class EvaluatorEngine extends CustomEvaluators {

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
