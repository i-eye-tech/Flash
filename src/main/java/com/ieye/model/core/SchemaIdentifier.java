package com.ieye.model.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor @NoArgsConstructor
public class SchemaIdentifier {

    private String projectId;
    private String testId;

    @Override
    public String toString() {
        return String.format("Project Id: %s, Test Id: %s", projectId, testId);
    }

}
