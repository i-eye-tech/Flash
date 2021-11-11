package com.ieye.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.test.FlashTest;
import com.ieye.model.Schema;
import com.ieye.model.SchemaIdentifier;
import com.ieye.model.StartRequestDTO;
import com.ieye.repository.SchemaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.*;

@Service
@Slf4j
public class Runner {

    @Autowired
    SchemaRepository schemaRepository;

    @Async
    public void run(final StartRequestDTO startRequestDTO, String requestId) {
        Schema schema;

        if (startRequestDTO.getProjectId() == null) {
            schema = schemaRepository.findByIdTestIdAndActive(startRequestDTO.getTestId(), true).orElse(null);
        } else {
            SchemaIdentifier schemaIdentifier = new SchemaIdentifier(startRequestDTO.getProjectId(), startRequestDTO.getTestId());
            schema = schemaRepository.findByIdAndActive(schemaIdentifier, true).orElse(null);
        }

        if(schema == null) {
            log.error("No Schema found with identifier: {}", startRequestDTO);
            return;
        }

        buildAndRunSuite(startRequestDTO, schema, requestId);

    }

    public void buildAndRunSuite(StartRequestDTO startRequestDTO, Schema schema, String requestId) {
        TestNG testNG = new TestNG();
        Map<String, String> suiteParams = new HashMap<>();
        suiteParams.put("requestId", requestId);
        suiteParams.put("projectId", startRequestDTO.getProjectId());
        suiteParams.put("testId", startRequestDTO.getTestId());
        suiteParams.put("testType", startRequestDTO.getTestType());

        XmlSuite xmlSuite = new XmlSuite();
        xmlSuite.setParameters(suiteParams);
        xmlSuite.setDataProviderThreadCount(4);
        xmlSuite.setParallel(XmlSuite.ParallelMode.METHODS);

        Map<String, String> classParams = new HashMap<>();
        List<XmlClass> xmlClasses = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        schema.getApiSpec().forEach(n -> {
            XmlClass xmlClass = new XmlClass();
            xmlClass.setClass(FlashTest.class);
            try {
                classParams.put("apiSpecification", mapper.writeValueAsString(n));
            } catch (JsonProcessingException ignore) {}
            xmlClass.setParameters(classParams);
            xmlClasses.add(xmlClass);
        });

        XmlTest xmlTest = new XmlTest(xmlSuite);
        xmlTest.setXmlClasses(xmlClasses);
        testNG.setXmlSuites(Collections.singletonList(xmlSuite));

        testNG.run();
    }

}
