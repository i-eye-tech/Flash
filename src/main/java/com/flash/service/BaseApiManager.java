package com.flash.service;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.flash.mongo.model.*;
import com.flash.service.utilities.*;
import com.flash.service.utilities.database.*;
import com.flipkart.zjsonpatch.*;
import com.jayway.jsonpath.*;
import io.restassured.*;
import io.restassured.filter.log.*;
import io.restassured.response.*;
import io.restassured.specification.*;
import org.apache.commons.io.output.*;
import org.slf4j.*;
import org.testng.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

/**
 * @author Avdhesh Gupta (av-g1)
 */
public class BaseApiManager  {

    private static final Logger logger = LoggerFactory.getLogger(com.flash.service.BaseApiManager.class);
    public ReportHelper reportHelper;
    public MongoHelper mongoHelper;
    public PostgresqlHelper postgreGHelper;
    public MysqlHelper mysqlHelper;
    protected Properties properties;
    private ObjectMapper mapper = new ObjectMapper();
    public JsonNode evaluatedJson;
    protected Evaluators evaluators;


    public ObjectMapper getMapper() {
        return mapper;
    }

    public BaseApiManager(String requestId) {
        this.reportHelper = ReportHelper.getInstance(requestId);
    }

    public static MongoHelper getMongoHelperInstance() {
        return MongoHelper.getInstance();
    }

    public PostgresqlHelper getPostgresHelperInstance(String databaseName) {
        return PostgresqlHelper.getInstance(databaseName, this.reportHelper);
    }

    public MysqlHelper getMysqlHelperInstance(String databaseName) {
        return MysqlHelper.getInstance(databaseName, this.reportHelper);
    }

    private ThreadLocal<StringWriter> writer = new ThreadLocal<>();

    public boolean compareJson(String expectedResponse, String actualResponse) {
        return compareJson(expectedResponse, actualResponse, null, null);
    }

    public boolean compareJson(String expectedResponse, String actualResponse, ComparatorIgnore comparatorIgnore) {
        return compareJson(expectedResponse, actualResponse, comparatorIgnore.getIgnoreFields(), comparatorIgnore.getIgnoreOperation());
    }

    public boolean compareJson(String expectedResponse, String actualResponse, Set<String> dndCompareValues, String ignoreOperation) {
        ObjectMapper objectMapper = new ObjectMapper();
        int differenceCounter = 0;
        List<String[]> log = new ArrayList<>();
        log.add(new String[]{"Operation", "Json Path", "Actual", "Expected"});
        try {
            JsonNode actualJson = objectMapper.readTree(actualResponse);
            JsonNode expectedJson = objectMapper.readTree(expectedResponse);
            JsonNode jsonDiff = JsonDiff.asJson(expectedJson, actualJson);

            for (JsonNode node : jsonDiff) {
                String diffPath = node.get("path").asText();
                String operationType = node.get("op").asText();

                if ((dndCompareValues == null || !ignoreFieldOperation(dndCompareValues, diffPath))
                        && !operationType.equalsIgnoreCase(ignoreOperation)) {

                    differenceCounter++;

                    JsonNode diffValueJsonNode = node.get("value");

                    String diffValue = Optional.ofNullable(diffValueJsonNode)
                            .map(dataValue -> diffValueJsonNode.toPrettyString()).orElse(null);

                    String parentValue = Optional.ofNullable(expectedResponse)
                            .map(dataValue -> expectedJson.at(diffPath).toPrettyString()).orElse(null);

                    log.add(new String[]{getPreString(operationType), getPreString(diffPath), getPreString(diffValue),
                            getPreString(parentValue)});

                }
            }

            if (log.size() > 1)
                reportHelper.fail(log.toArray(new String[0][]));

            if (differenceCounter > 0)
                return false;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean ignoreFieldOperation(Set<String> ignoreProps, String field) {
        boolean ignoreField = false;
        for (String ignoreProperty : ignoreProps) {
            if (Pattern.matches(ignoreProperty, field)) {
                ignoreField = true;
                break;
            }
        }
        return ignoreField;
    }

//    public boolean validateJsonSchema(String response, String responseSchema) {
//        boolean schemaValidationFlag = false;
//        try {
//            JsonNode jsonSchema = JsonLoader.fromString(responseSchema);
//            JsonNode jsonResponse = JsonLoader.fromString(response);
//
//            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
//            JsonValidator schemaValidator = factory.getValidator();
//            ProcessingReport errorReport = schemaValidator.validate(jsonSchema, jsonResponse);
//
//            String error = "";
//            if(errorReport.isSuccess())
//                schemaValidationFlag = true;
//            else
//                for (ProcessingMessage m : errorReport)
//                    if(m.getLogLevel().equals(LogLevel.ERROR))
//                        error += m.getMessage() + "\n";
//
//            System.out.println(error);
//
//        } catch (IOException | ProcessingException e) {
//            e.printStackTrace();
//        }
//        return schemaValidationFlag;
//    }

    private String getPreString(String val) {
        return String.format("<pre>%s</pre>", val);
    }

    /**
     * @param key
     * @param filePath
     * @return
     * @author Avdhesh Gupta (av-g1)
     */
    public String getValueOfProperty(String key, String filePath) {
        filePath = System.getProperty("user.dir") + filePath;
        try {
            properties = new PropertyReader().readProperty(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }

    /**
     * @param request - request object
     * @param log     - print logs true/false
     * @return - request specification for rest-assured
     * @author udit-jaiswal
     */
    private RequestSpecification createRequestParams(Request request, boolean log) {
        RequestSpecification requestSpecification = RestAssured.given();

        writer.set(new StringWriter());
        PrintStream captor = new PrintStream(new WriterOutputStream(writer.get(), Charset.defaultCharset()), true);
        requestSpecification.filter(new RequestLoggingFilter(captor));

        if (request.getHeaders() != null && !request.getHeaders().isEmpty())
            requestSpecification.headers(request.getHeaders());

        if (request.getFormParams() != null && !request.getFormParams().isEmpty())
            requestSpecification.formParams(request.getFormParams());

        if (request.getPathParams() != null && !request.getPathParams().isEmpty())
            requestSpecification.pathParams(request.getPathParams());

        if (request.getQueryParams() != null && !request.getQueryParams().isEmpty())
            requestSpecification.queryParams(request.getQueryParams());

        if (request.getCookies() != null && !request.getCookies().isEmpty())
            requestSpecification.cookies(request.getCookies());

        if (request.getBody() != null && !request.getBody().isEmpty())
            requestSpecification.body(request.getBody());

        requestSpecification.contentType(request.getContentType());

        requestSpecification.baseUri(request.getBasePath());
        requestSpecification.basePath(request.getUrl());

        if (log)
            requestSpecification.log().all();

        return requestSpecification;
    }


    /**
     * @param request
     * @param requestSpecification
     * @param log
     * @return rest-assured response
     * @author udit-jaiswal
     */
    private Response send(Request request, RequestSpecification requestSpecification, boolean log) {
        Response response = requestSpecification.request(request.getMethod().toUpperCase());
        if (log) {
            try {
                reportHelper.info(writer.get().toString());
                reportHelper.info(response.prettyPrint());
            } catch (Exception e) {
                logger.error("Exception occured while logging request {} ,response {}", request.toString(), response.asString());
            }
        }

        if (request.getExpectedStatusCode() != null
                && response.statusCode() != request.getExpectedStatusCode())
            Assert.fail("Expected response code was " + request.getExpectedStatusCode() + " but got " + response.statusCode());

        return response;
    }

    /**
     * @param request
     * @param log
     * @return rest-assured response
     * @author udit-jaiswal
     */
    public Response execute(Request request, boolean log) {
//        authenticateSession(request);
        RequestSpecification requestSpecification = createRequestParams(request, log);
        return send(request, requestSpecification, log);
    }


    public Response execute(Request request, boolean log, Map<String, Object> preExecution) {
        return execute(request, log);
    }


    public Response execute(Request request) {
        return execute(request, true);
    }


    /**
     * @param dbData
     * @param <T>
     * @return generic type
     * @author Avdhesh Gupta (av-g1)
     * @description returns data in list<Map> from different databases
     */
    public <T> T getDataFromDb(DbData dbData) {
        if (dbData != null && dbData.getQuery() != null && !dbData.getQuery().isEmpty()) {
            String query = resolvePatternString(dbData.getQuery(), this.evaluatedJson.toString());
            reportHelper.info(query);
            switch (dbData.getDbType()) {
//            case MONGO:return new TypeReference<ArrayList>(){};
                case POSTGRES:
                    return getPostgresHelperInstance(dbData.getDatabaseName()).getDataAsListOfMap(query);
                case MYSQL:
                    return getMysqlHelperInstance(dbData.getDatabaseName()).getDataAsListOfMap(query);

            }
        }
        return null;
    }

    /**
     * @param dbData
     * @param testDataModel
     * @param <T>
     * @return generic type
     * @author Avdhesh Gupta (av-g1)
     * @description returns data in list<Map> from different databases
     */
//    public <T> T getDataFromDb(DbData dbData, TestDataModel testDataModel) {
//        if (dbData != null) {
//            dbData.setQuery(resolvePatternString(dbData.getQuery(), testDataModel.getEvaluatedJson().toString()));
//            return getDataFromDb(dbData);
//        } else {
//            logger.error("Incorrect dbSpecification, dbData is null");
//        }
//        return null;
//    }

    /**
     * @param dbData
     * @return integer value
     * @author Avdhesh Gupta (av-g1)
     * @description updates db with provided dbData
     */
    public Integer updateDataInDb(DbData dbData) {
        if (dbData != null && dbData.getQuery() != null && !dbData.getQuery().isEmpty()) {
            String query = resolvePatternString(dbData.getQuery(), this.evaluatedJson.toString());
            switch (dbData.getDbType()) {
//            case MONGO:return new TypeReference<ArrayList>(){};
                case POSTGRES:
                    return getPostgresHelperInstance(dbData.getDatabaseName()).update(query);
                case MYSQL:
                    return getMysqlHelperInstance(dbData.getDatabaseName()).update(query);

            }
        } else {
            assert dbData != null;
            logger.error("Incorrect db mappings in preExecutionSteps for dbdata {}", dbData.toString());
        }

        return 0;
    }

    /**
     * @param testDataModel
     * @param apiSpecification
     * @return Comparartor ignore object
     * @author Avdhesh Gupta (av-g1)
     * @description This object contains ignore fields and ignore operation value, it is directly being used in
     * compare json method as a parameter
     */

    public ComparatorIgnore getComparatorIgnore(TestDataModel testDataModel, ApiSpecification apiSpecification) {
        String ignoreOperation = testDataModel.getIgnore() != null && testDataModel.getIgnore().getIgnoreOperation() != null ? testDataModel.getIgnore().getIgnoreOperation() : Optional.ofNullable(apiSpecification.getIgnore()).map(ComparatorIgnore::getIgnoreOperation).orElse(null);
        Set<String> dndValues = testDataModel.getIgnore() != null && testDataModel.getIgnore().getIgnoreFields() != null ? testDataModel.getIgnore().getIgnoreFields() : Optional.ofNullable(apiSpecification.getIgnore()).map(ComparatorIgnore::getIgnoreFields).orElse(null);
        return new ComparatorIgnore(ignoreOperation, dndValues);
    }

    /**
     * @param json
     * @param jsonPathOrValue
     * @param <T>
     * @return generic return type
     * @author Avdhesh Gupta (av-g1)
     * @description method is used to extract values from  provided json by using the jsonpath can get values
     * in arrays, json, String format depending on jsonpath query
     */
    public <T> T readJsonPath(String json, Object jsonPathOrValue) {
        try {
            return JsonPath.read(json, jsonPathOrValue.toString());
        } catch (Exception e) {
            return (T) jsonPathOrValue;
        }
    }

    public <T> T isValidJsonPath( Object jsonPathOrValue){
        try {
            return JsonPath.read(this.evaluatedJson,jsonPathOrValue.toString());

        }catch (Exception e){
            return null;
        }
    }

    /**
     * @param text (entire string containing jsonpath which has to be evaluated)
     * @param json
     * @return String
     * @author Avdhesh Gupta (av-g1)
     * @description returns the argument 'text' post evaluating, evaluation criteria is  finding jsonpath in pattern
     * 'var($.a.b)' and extracting the value of json path from provided json
     */
    public String resolvePatternString(String text, String json) {
        if (text == null || text.isEmpty())
            return text;
        Pattern jsonpathPattern = Pattern.compile("var\\((.+?)\\)");
        Matcher matcher = jsonpathPattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = null;
            try {
                Object value = JsonPath.read(json, matcher.group(1));
                replacement = value instanceof String ? value.toString() : getMapper().writeValueAsString(value);
            } catch (Exception e) {
                logger.error("Exception occured while reading jsonpath, nested exception is {}", e.getMessage());
            }
            if (replacement != null) {
                matcher.appendReplacement(buffer, "");
                buffer.append(replacement);
            }
        }
        matcher.appendTail(buffer);
        Pattern evaluatorPattern = Pattern.compile("evaluate\\((.+?)\\)");
        Matcher evaluatorMatcher = evaluatorPattern.matcher(buffer.toString());
        StringBuffer evaluatorBuffer = new StringBuffer();
        while (evaluatorMatcher.find()) {
            String replacement = null;
            try {
                String[] matcherOutput = evaluatorMatcher.group(1).split(",");
                if (matcherOutput.length != 2)
                    throw new Exception("Invalid evaluate format, should include both evaluator name and valu to be " +
                            "evaluated");
                Object value =
                        this.evaluators.getEvaluator(matcherOutput[1],
                                matcherOutput[0]);
                replacement = value instanceof String ? value.toString() : getMapper().writeValueAsString(value);
            } catch (Exception e) {
                logger.error("Exception occured while reading jsonpath, nested exception is {}", e.getMessage());
            }
            if (replacement != null) {
                evaluatorMatcher.appendReplacement(evaluatorBuffer, "");
                evaluatorBuffer.append(replacement);
            }
        }
        evaluatorMatcher.appendTail(evaluatorBuffer);
        return evaluatorBuffer.toString();
    }



}
