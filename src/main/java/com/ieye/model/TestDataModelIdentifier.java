package com.ieye.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class TestDataModelIdentifier {

    private String testCaseId;
    private String testDataId;

}
