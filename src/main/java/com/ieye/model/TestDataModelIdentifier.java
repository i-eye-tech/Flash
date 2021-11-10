package com.ieye.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class TestDataModelIdentifier {

    @NonNull
    private String testCaseId;

    @NonNull
    private String testDataId;

}
