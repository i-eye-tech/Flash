package com.flash.service;

import com.flash.constants.*;
import com.flash.dto.request.*;
import com.flash.dto.response.*;
import com.flash.mongo.model.*;
import com.flash.mongo.repositories.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.util.*;
import java.util.concurrent.*;

@Service
public class AutomationService {

    @Autowired
    private ApplicationContext applicationContext;
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;


    @Autowired
    ExecuteAsyncService executeAsyncService;

    private static final Logger logger = LoggerFactory.getLogger(AutomationService.class);


    @Autowired
    private TestSchemaRepository testSchemaRepository;
    @Autowired
    private RequestRepository requestRepository;
    public Map<MappingId, SchemaData> inMemoryMap;
    RequestDao requestDao;


    /**
     *
     * @param startTestRequestDto
     * @author Avdhesh Gupta (av-g1)
     * @return StartTestResponseDto
     */
    public StartTestResponseDto saveRequestAndStartTest(StartTestRequestDto startTestRequestDto) {

        String uuId = UUID.randomUUID().toString();
        requestDao = new RequestDao(new MappingRequestId(startTestRequestDto.getProjectId(), startTestRequestDto.getTestId(), uuId), TestExecutionStatus.STARTED, startTestRequestDto.getTestType());
        try {
            requestRepository.save(requestDao);
            executeAsyncService.executeTestInAsync(requestDao, startTestRequestDto);
            return new StartTestResponseDto(requestDao.getId().getRequestId(), requestDao.getId().getProjectId(), requestDao.getId().getTestId(), null);
        } catch (Exception e) {
            logger.error("Exception occurred while starting test in async for request {}, nested exception is {}",requestDao.getId(),e.getMessage());
            requestDao.setTestRunStatus(TestExecutionStatus.FAILED);
            requestRepository.save(requestDao);
            return new StartTestResponseDto(null, null, null, new Error(e.getMessage()));
        }
    }

    /**
     * @return
     * @author Avdhesh Gupta (av-g1)
     */
    @PostConstruct
    public List<InMemorySchemaResponse> loadInMemoryMap() {
        List<InMemorySchemaResponse> inMemorySchemaResponses = new LinkedList<>();
        inMemoryMap = new ConcurrentHashMap<>();
        testSchemaRepository.findByActive(true).forEach(responseMap -> {
            inMemorySchemaResponses.add(new InMemorySchemaResponse(responseMap.getId().getProjectId(),
                    responseMap.getId().getTestId(), new SchemaData(responseMap.getApiSpec(),responseMap.getEvaluators())));
            inMemoryMap.put(responseMap.getId(), new SchemaData( responseMap.getApiSpec(),responseMap.getEvaluators()));
        });
        BaseTest.inMemoryMap = inMemoryMap;
        return inMemorySchemaResponses;
    }

    /**
     * @param requestId
     * @param projectId
     * @param testId
     * @return
     * @author Avdhesh Gupta (av-g1)
     */
    public RequestDao getRequestStatus(String requestId, String projectId, String testId) {
        return requestRepository.findById(new MappingRequestId(projectId, testId, requestId)).get();
    }

}
