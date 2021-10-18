//package com.flash.mongo.repositories.impl;
//
//import com.flash.mongo.repositories.TestDataRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//
//import java.util.List;
//
//public class TestDataRepositoryImpl<T> implements TestDataRepository {
//
//    private final MongoTemplate mongoTemplate;
//
//
//    @Autowired
//    public TestDataRepositoryImpl(MongoTemplate mongoTemplate) {
//        this.mongoTemplate = mongoTemplate;
//    }
//
//
//    @Override
//    public List<T> findByTestTypeAndCollectionName(String testType, Class entityClass, String collectionName) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("testType").is(testType).and("active").is(true));
//        return mongoTemplate.find(query, entityClass, collectionName);
//    }
//
//}
