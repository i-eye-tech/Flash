package com.ieye.repository;

import com.ieye.model.Schema;
import com.ieye.model.SchemaIdentifier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SchemaRepository extends MongoRepository<Schema, SchemaIdentifier> {

    Optional<Schema> findById(SchemaIdentifier schemaIdentifier);

}
