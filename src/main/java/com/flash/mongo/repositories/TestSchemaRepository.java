package com.flash.mongo.repositories;

import com.flash.mongo.model.MappingId;
import com.flash.mongo.model.TestSchemaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSchemaRepository  extends MongoRepository<TestSchemaModel, MappingId>{


    public Optional<TestSchemaModel> findByIdAndActive(MappingId id, boolean active);

    public List<TestSchemaModel> findByActive( boolean active);

    public List<TestSchemaModel> findAll();



}
