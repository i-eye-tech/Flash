package com.ieye.core.helper;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Evaluator {

    public String evaluate(String s1) {
        if(!isAnExpression(s1))
            return s1;
        return "";
    }

    private String resolveUsingEvaluator(String s1) {
        return "";
    }

    private String resolveUsingData(String s1) {
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
