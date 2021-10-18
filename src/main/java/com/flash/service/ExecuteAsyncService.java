package com.flash.service;

import com.flash.dto.request.StartTestRequestDto;
import com.flash.mongo.model.RequestDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ExecuteAsyncService {
    @Autowired
    BaseTest baseTest;
    private static final Logger logger = LoggerFactory.getLogger(ExecuteAsyncService.class);

    /**
     *
     * @param requestDao
     * @author Avdhesh Gupta (av-g1)
     */
    @Async("threadPoolTaskExecutor")
    public void executeTestInAsync(RequestDao requestDao, StartTestRequestDto startTestRequestDto) {
        try {
            baseTest.executeRunner(requestDao, startTestRequestDto);
        } catch (Exception e) {
            logger.error("Exception occurred while starting test, nested exception is {}",e.getMessage());
        }

    }

}
