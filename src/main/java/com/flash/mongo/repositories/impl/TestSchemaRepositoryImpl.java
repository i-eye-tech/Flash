//package com.flash.mongo.repositories.impl;
//
//import com.flash.mongo.model.TestSchemaModel;
//import com.flash.mongo.repositories.TestDataRepository;
//import com.flash.mongo.repositories.TestSchemaReporitory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public class TestSchemaRepositoryImpl implements TestSchemaReporitory {
//
//    private final MongoTemplate mongoTemplate;
//
//
//    @Autowired
//    public TestSchemaRepositoryImpl(MongoTemplate mongoTemplate) {
//        this.mongoTemplate = mongoTemplate;
//    }
//
//    @Override
//    public TestSchemaModel findByProjectIdAndTestId(String projectId, String testId) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id.projectId").is(projectId).and("_id.testId").is(testId));
//        return mongoTemplate.findOne(query,TestSchemaModel.class);
//    }
//
//    @Override
//    public List<TestSchemaModel> findAllSchemas() {
//        return mongoTemplate.findAll(TestSchemaModel.class);
//    }
//}
