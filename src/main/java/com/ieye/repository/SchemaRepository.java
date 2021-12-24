package com.ieye.repository;

import com.ieye.model.core.Schema;
import com.ieye.model.core.SchemaIdentifier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SchemaRepository extends MongoRepository<Schema, SchemaIdentifier> {

    Optional<Schema> findByIdAndActive(SchemaIdentifier schemaIdentifier, boolean active);
    Optional<Schema> findByIdTestIdAndActive(String testId, boolean active);
    List<Schema> findAll();

}
