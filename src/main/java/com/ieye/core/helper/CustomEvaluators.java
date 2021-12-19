package com.ieye.core.helper;

import com.ieye.core.lib.currenttest.CurrentTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomEvaluators {

    @Autowired
    CurrentTest currentTest;

}
