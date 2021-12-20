package com.ieye.core.lib;

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
    CurrentTest currentTest;

    @Autowired
    Reporter reporter;

    @Autowired
    PatternResolver patternResolver;

    @Autowired
    MongoHelper mongoHelper;

    public <T> T getDatafromDB(DatabaseTemplate database) {
        if(database == null || database.getQuery() == null)
            return null;

        database.setQuery(patternResolver.resolve(database.getQuery(), currentTest.getData()));
        reporter.info(currentTest.getExtentTest(), "Executing Query: " + database.getQuery());
        switch (database.getType()) {
            case MONGO:
                return mongoHelper.getDataAsListOfMap(database.getDatabaseName(), database.getCollectionName(),
                        database.getQuery());
            case MYSQL:
                //TODO
            case POSTGRES:
                //TODO
                break;
            default:
                return null;
        }
        return null;

    }

}
