package com.ieye.service;

import com.ieye.model.ApiSpecification;
import com.ieye.model.SchemaIdentifier;
import java.util.List;

public interface SchemaService {

    List<ApiSpecification> findByProjectAndTestId(SchemaIdentifier schemaIdentifier);

}
