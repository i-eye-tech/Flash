package com.ieye.model.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
class TestDataModelIdentifier {

    private String testCaseId;
    private String testDataId;

}
