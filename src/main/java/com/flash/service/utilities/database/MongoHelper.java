package com.flash.service.utilities.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MongoHelper implements DbHelper {

    public static String mongoUri;
    private final MongoClient mongoClient ;
    public static MongoHelper mongoHelperInstance;

    private MongoHelper(String mongoUri) {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings settings = MongoClientSettings.builder().
                applyConnectionString(new ConnectionString(mongoUri)).
                codecRegistry(codecRegistry).build();
        this.mongoClient = MongoClients.create(settings);
    }

    public static MongoHelper getInstance( ) // considering mongo server at all time should be same
    {
        if(mongoHelperInstance==null)
            mongoHelperInstance= new MongoHelper(mongoUri);
        return mongoHelperInstance;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoCollection<Document> getMongoCollection(String databaseName, String collectionName) {
        return getMongoDatabase(databaseName).getCollection(collectionName);
    }

    public MongoDatabase getMongoDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }

    public List<Object> getResultAsListOfObject(BasicDBObject query, BasicDBObject sortQuery,String database,
                                                String collectionName, Type type){
        MongoCollection<Document> mongoCollection = getMongoDatabase(database).getCollection(collectionName);
        List<Object> list = new ArrayList<>();
        for (Document dbObject : mongoCollection.find(query).sort(sortQuery)) {
            ObjectMapper mapper = new ObjectMapper();

            try {
              list.add(mapper.readValue(dbObject.toJson(), type.getClass()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}