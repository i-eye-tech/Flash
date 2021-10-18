package com.flash.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.service.BaseApiManager;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.flash.dto.request.StartTestRequestDto;
import com.flash.dto.response.SchemaData;
import com.flash.mongo.model.*;
import com.flash.service.managers.RequestManager;
import com.flash.service.utilities.ReportHelper;
import com.flash.service.utilities.ReportingService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BaseTest extends ReportingService {


    MongoCollection<Document> mongoCollection;
    protected ApiSpecification apiSpecification;
    protected List<Evaluator> evaluatorList;
    protected String testType;
    protected String testCollectionName;
    protected MongoCollection<Document> testCollection;
    protected BaseApiManager baseApiManager;
    String projectId;
    String testId;
    String className;
    public static Map<MappingId, SchemaData> inMemoryMap;
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    SchemaData testSchemaModel;


    /**
     * @param requestDao
     * @author Avdhesh Gupta (av-g1)
     */
    public void executeRunner(RequestDao requestDao, StartTestRequestDto startTestRequestDto) throws Exception {
        TestNG runner = new TestNG();
        inMemoryMap.entrySet().stream().filter(entry ->
                entry.getKey().getProjectId().equals(requestDao.getId().getProjectId())
                        && entry.getKey().getTestId().equals(requestDao.getId().getTestId())).findFirst().ifPresent(map -> testSchemaModel = map.getValue());

        if (testSchemaModel != null) {
            Map<String, String> suiteParams = new HashMap<>();
            XmlSuite xmlSuite = new XmlSuite();
            xmlSuite.setParameters(suiteParams);
            xmlSuite.setDataProviderThreadCount(5);
            xmlSuite.setParallel(XmlSuite.ParallelMode.METHODS);
            List<ApiSpecification> apiSpecifications = testSchemaModel.getApiSpec();

            if (startTestRequestDto.getApiSpecIds() != null && !startTestRequestDto.getApiSpecIds().isEmpty())
                apiSpecifications = apiSpecifications.stream().filter(n -> startTestRequestDto.getApiSpecIds()
                        .contains(n.get_id())).collect(Collectors.toList());

            for (ApiSpecification apiSpecification : apiSpecifications) {
                logger.info("api details {}", apiSpecification.get_id());

                Map<String, String> classParams = new HashMap<>();
                classParams.put("requestId", requestDao.getId().getRequestId());
                classParams.put("testType", requestDao.getTestType());
                classParams.put("projectId", requestDao.getId().getProjectId());
                classParams.put("testId", requestDao.getId().getTestId());
                classParams.put("className", apiSpecification.get_id());
                classParams.put("domainName", startTestRequestDto.getDomainName());
                classParams.put(apiSpecification.get_id(), apiSpecification.getTestCollection());
                XmlClass testClass = new XmlClass();
                testClass.setParameters(classParams);
                testClass.setClass(apiSpecification.getValidationType().getClassEntity());
                XmlTest test = new XmlTest(xmlSuite);
                test.setXmlClasses(Collections.singletonList(testClass));
            }
            List<XmlSuite> xmlSuiteList = new ArrayList<>();
            xmlSuiteList.add(xmlSuite);
            runner.setXmlSuites(xmlSuiteList);
            try {
                runner.run();
            } catch (Exception e) {
                logger.error("Exception while running testSuite for requestId {}, nested exception is {} ", requestId, e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        } else {
            throw new Exception("cannot start test, TestSchemaModel instance is null");
        }
    }


    /**
     * @param ctx
     * @throws JsonProcessingException
     * @author Avdhesh Gupta (av-g1)
     */
    @BeforeClass()
    public void beforeClass(ITestContext ctx) throws JsonProcessingException {
        try {
            for (XmlClass classe : ctx.getCurrentXmlTest().getXmlClasses()) {
//                baseApiManager = new BaseApiManager(requestId);
                requestId = classe.getAllParameters().get("requestId");
                reportHelper = ReportHelper.getInstance(requestId);
                testType = classe.getAllParameters().get("testType");
                projectId = classe.getAllParameters().get("projectId");
                testId = classe.getAllParameters().get("testId");
                className = classe.getAllParameters().get("className");
                testCollectionName = classe.getAllParameters().get(className);
//                mongoDatabase = BaseApiManager.getMongoHelperInstance().getMongoDatabase("automation_service");
                inMemoryMap.entrySet().stream().filter(entry -> entry.getKey().getProjectId().equals(projectId) && entry.getKey().getTestId().equals(testId)).findFirst().ifPresent(map -> testSchemaModel = map.getValue());
                testSchemaModel.getApiSpec().stream().filter(api -> className.equals(api.get_id())).findFirst().ifPresent(api -> apiSpecification = api);
                this.evaluatorList=testSchemaModel.getEvaluators();
                if (classe.getAllParameters().containsKey("domainName") && classe.getAllParameters().get("domainName") != null)
                    apiSpecification.setDomain(classe.getAllParameters().get("domainName"));
            }
        } catch (Exception e) {
            logger.error("Exception occurred while setting up beforeClass for requestId {}, exception occurred is : {}", requestId, e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }


    @DataProvider(parallel = false)
    protected Object[][] getData() {
        BasicDBObject basicDBObject = new BasicDBObject("testType", testType)
                .append("_id.testDataId", apiSpecification.get_id())
                .append("active", true);
        ObjectMapper mapper = new ObjectMapper();
        List<TestDataModel> testDataJsonList = new ArrayList<>();

        for (Document dbObject : testCollection.find(basicDBObject))
            try {
                testDataJsonList.add(mapper.readValue(dbObject.toJson(), TestDataModel.class));
            } catch (JsonProcessingException e) {
                logger.error("Exception parsing test data {} json for requestId {}, Exception is {} : ", dbObject.toJson(),
                        requestId,
                        e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
            } catch (Exception e) {
                logger.error("Exception occured while test data {} json for requestId {}, Exception is {} : ",
                        dbObject.toJson(), requestId, e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        Object[][] objectArray = new Object[testDataJsonList.size()][1];
        for (int i = 0; i < testDataJsonList.size(); i++)
            objectArray[i][0] = testDataJsonList.get(i);

        return objectArray;
    }

    @BeforeMethod
    public void runPreExecution(Object[] testArgs) {
        baseApiManager.evaluators=Evaluators.getInstance(this.requestId,this.evaluatorList);
        baseApiManager.evaluatedJson=baseApiManager.getMapper().createObjectNode();
        for (Object o : testArgs) {
            if (o instanceof TestDataModel) {
                baseApiManager.evaluatedJson = ((TestDataModel) o).getPreExecutionSteps() != null ?
                        ((RequestManager)baseApiManager).getPreExecutionResponseList(((TestDataModel) o).getPreExecutionSteps()) :
                        baseApiManager.getMapper().createObjectNode();
            }
            break;
        }
    }

}
