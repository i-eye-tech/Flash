package com.ieye.core.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.core.helper.Reporter;
import com.ieye.core.helper.database.MongoHelper;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.DatabaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatabaseManager {

    @Autowired
    private CurrentTest currentTest;

    @Autowired
    private Reporter reporter;

    @Autowired
    private PatternResolver patternResolver;

    @Autowired
    private MongoHelper mongoHelper;

    public <T> T getDatafromDB(DatabaseTemplate database) {
        if(database == null || (database.getQuery() == null && database.getMongoQuery() == null))
            return null;

        String query = "";
        if(database.getMongoQuery() != null) {
            query = patternResolver.resolve(database.getMongoQuery().getFind(),
                    currentTest.getData());
            database.getMongoQuery().setFind(query);
        } else {
            query = patternResolver.resolve(database.getQuery(), currentTest.getData());
            database.setQuery(query);
        }
        reporter.info(currentTest.getExtentTest(), "Executing Query: " + query);
        T dataAsListOfMap = null;
        switch (database.getType()) {
            case MONGO:
                if(database.getQuery() == null)
                    dataAsListOfMap = mongoHelper.getDataAsListOfMap(database.getDatabaseName(),
                            database.getCollectionName(), database.getMongoQuery());
                else
                    dataAsListOfMap = mongoHelper.getDataAsListOfMap(database.getDatabaseName(),
                            database.getCollectionName(), database.getQuery());
                break;
            case MYSQL:
                //TODO
            case POSTGRES:
                //TODO
                break;
        }
        if(dataAsListOfMap == null)
            reporter.info(currentTest.getExtentTest(), "No rows returned using query " + database.getQuery());
        else {
            try {
                reporter.info(currentTest.getExtentTest(), "Rows returned: \n" +
                        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dataAsListOfMap));
            } catch (JsonProcessingException ignore) {}
        }
        return dataAsListOfMap;
    }

}
