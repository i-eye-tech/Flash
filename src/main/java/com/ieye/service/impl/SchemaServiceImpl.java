package com.ieye.service.impl;

import com.ieye.model.ApiSpecification;
import com.ieye.model.Schema;
import com.ieye.model.SchemaIdentifier;
import com.ieye.model.exception.SchemaNotFoundException;
import com.ieye.repository.SchemaRepository;
import com.ieye.service.SchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService {

    @Autowired
    private SchemaRepository schemaRepository;

    @Override
    public List<ApiSpecification> findByProjectAndTestId(SchemaIdentifier schemaIdentifier) {
        log.debug("Start of method find by project and test id. Input param: {}", schemaIdentifier);
        Schema schema = schemaRepository.findById(schemaIdentifier).
                orElseThrow(() -> new SchemaNotFoundException("Schema not found using identifier: " + schemaIdentifier));
        return schema.getApiSpec();
    }

}
