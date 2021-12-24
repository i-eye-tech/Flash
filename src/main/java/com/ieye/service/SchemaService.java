package com.ieye.service;

import com.ieye.model.core.ApiSpecification;
import com.ieye.model.core.SchemaIdentifier;
import java.util.List;

public interface SchemaService {

    List<ApiSpecification> findByProjectAndTestId(SchemaIdentifier schemaIdentifier);

}
