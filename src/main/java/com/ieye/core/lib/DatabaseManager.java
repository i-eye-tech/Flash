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
        if(database == null || database.getQuery() == null)
            return null;

        database.setQuery(patternResolver.resolve(database.getQuery(), currentTest.getData()));
        reporter.info(currentTest.getExtentTest(), "Executing Query: " + database.getQuery());
        T dataAsListOfMap = null;
        switch (database.getType()) {
            case MONGO:
                 dataAsListOfMap = mongoHelper.getDataAsListOfMap(database.getDatabaseName(),
                         database.getCollectionName(), database.getQuery());
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

        return null;

    }

}
