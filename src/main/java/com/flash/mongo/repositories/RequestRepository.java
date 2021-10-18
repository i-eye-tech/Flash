package com.flash.mongo.repositories;

import com.flash.mongo.model.MappingRequestId;
import com.flash.mongo.model.RequestDao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RequestRepository extends MongoRepository<RequestDao, MappingRequestId> {

    public Optional<RequestDao> findById(MappingRequestId id);

//    public Optional<RequestDao> findBy



}
