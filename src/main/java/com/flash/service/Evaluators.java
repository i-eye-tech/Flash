package com.flash.service;

import com.flash.constants.*;
import com.flash.mongo.model.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.security.*;
import java.util.*;
import java.util.logging.*;
import org.slf4j.Logger;

public class Evaluators {


    public List<Evaluator> evaluatorList;
    private static final Logger logger = LoggerFactory.getLogger(Evaluators.class);


    private Evaluators(List<Evaluator> evaluators) {
        this.evaluatorList = evaluators;
    }

    private static Map<String,Evaluators> instanceMap=new HashMap<>();

    public static  Evaluators getInstance(String requestId,List<Evaluator> evaluators) {

        if (!instanceMap.containsKey(requestId)) {
            instanceMap.put(requestId, new Evaluators(evaluators));
        }
        return instanceMap.get(requestId);
    }

    public static  Evaluators getInstance(String requestId) {

        if (instanceMap.containsKey(requestId)) {
            instanceMap.get(requestId);
        }
        return null;
    }

    private String stringAppend(String value, Map<String, Object> arguments) {

        final int MSB = 0x80000000;
        SecureRandom ng = new SecureRandom();
        return String.format(value, Integer.toHexString(MSB | ng.nextInt()) + Integer.toHexString(MSB | ng.nextInt()));
    }


    private String concatString(String value, Map<String, Object> arguments) {

        if(arguments.containsKey(EvaluatorConstant.PREAPPEND.toString()))
            return arguments.get(EvaluatorConstant.PREAPPEND.toString())+value;
        else if(arguments.containsKey(EvaluatorConstant.POSTAPPEND.toString()))
            return value+arguments.get(EvaluatorConstant.POSTAPPEND.toString());
        else
            return value;
    }

    private String generateUUID(String value, Map<String, Object> arguments) {
        return UUID.randomUUID().toString();
    }

    private String toString(String value, Map<String, Object> arguments) {
        return UUID.randomUUID().toString();
    }
    public Object getEvaluator(String value, String evaluatorName) {
        return evaluatorList.stream().filter(evaluator -> evaluator.get_id().equals(evaluatorName)).map(evaluator -> {
            try {
                return this.getClass().getDeclaredMethod(evaluator.getName(), String.class, Map.class).invoke(this, value,
                        evaluator.getArguments());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }).findFirst().orElseGet(() -> {
            logger.error("Invalid evaluator list, missing fields");
            return value;
        });

    }

}