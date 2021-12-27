package com.ieye.core.helper.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieye.model.core.MongoDBQuery;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class MongoHelper {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    private MongoClient mongoClient;

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings settings = MongoClientSettings.builder().
                applyConnectionString(new ConnectionString(uri)).
                codecRegistry(codecRegistry).build();

        mongoClient = MongoClients.create(settings);
        return mongoClient;
    }

    public <T> T getDataAsListOfMap(String database, String collectionName, String q) {
        return getDataAsListOfMap(database, collectionName, BasicDBObject.parse(q));
    }

    public <T> T getDataAsListOfMap(String database, String collectionName, BasicDBObject q) {
        MongoDBQuery query = new MongoDBQuery();
        query.setFind(q.toJson());
        return getDataAsListOfMap(database, collectionName, query);
    }

    public <T> T getDataAsListOfMap(String database, String collectionName, MongoDBQuery query) {
        MongoCollection<Document> mongoCollection = mongoClient.getDatabase(database).getCollection(collectionName);
        FindIterable<Document> output = mongoCollection.find(BasicDBObject.parse(query.getFind()));
        if(query.getProjection() != null)
            output.projection(BasicDBObject.parse(query.getProjection()));

        if(query.getSort() != null)
            output.sort(BasicDBObject.parse(query.getSort()));

        if(query.getLimit() > 0)
            output.limit(query.getLimit());

        List<Map<String, Object>> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for(Document document : output)
            try {
                result.add(mapper.readValue(document.toJson(), new TypeReference<Map<String, Object>>() {}));
            } catch (JsonProcessingException ignore) {}
        return (T) result;
    }

    public <T> T getDataAsListOfMap(String collectionName, BasicDBObject q) {
        return getDataAsListOfMap(database, collectionName, q);
    }

    public void updateOne(String collection, BasicDBObject q1, BasicDBObject q2, boolean upsert) {
        mongoClient.getDatabase(database).getCollection(collection).updateOne(q1, q2, new UpdateOptions().upsert(upsert));
    }

}
