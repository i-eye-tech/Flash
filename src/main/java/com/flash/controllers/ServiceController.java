package com.flash.controllers;

import com.flash.config.Configuration;
import com.flash.dto.request.StartTestRequestDto;
import com.flash.dto.response.*;
import com.flash.service.AutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
//@RequestMapping("/sla")
public class ServiceController {

    @Autowired
    private AutomationService automationService;

    @Autowired
    private Configuration configuration;

    @PostMapping("/start")
    public ResponseEntity<StartTestResponseDto> startTest(@RequestBody StartTestRequestDto startTestRequestDto) {

        try {
            return ResponseEntity.ok(automationService.saveRequestAndStartTest(startTestRequestDto));
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("reload/schema")
    public ResponseEntity reloadSchema() {
        return ResponseEntity.ok(automationService.loadInMemoryMap());
    }


    @GetMapping("requestStatus")
    public ResponseEntity getRequestStatus(String requestId, String projectId, String testId) {
        return ResponseEntity.ok(automationService.getRequestStatus(requestId, projectId, testId));
    }


    @GetMapping("getRequest")
    public ResponseEntity<StartTestRequestDto> getConfigParams(@RequestParam(required = true)String serviceName, @RequestParam(required = true)String testType, String domainName) {
        StartTestRequestDto startTestRequestDto = new StartTestRequestDto();
        startTestRequestDto.setTestType(testType);
        if (domainName != null)
            startTestRequestDto.setDomainName(domainName);
        return ResponseEntity.ok(automationService.inMemoryMap.keySet().stream().filter(schemaData -> serviceName.equals(schemaData.getTestId())).map(schemaData -> {
            startTestRequestDto.setTestId(schemaData.getTestId());
            startTestRequestDto.setProjectId(schemaData.getProjectId());
            return startTestRequestDto;
        }).findFirst().orElse(new StartTestRequestDto(new Error("No schema found with serviceName " + serviceName))));
    }


}
