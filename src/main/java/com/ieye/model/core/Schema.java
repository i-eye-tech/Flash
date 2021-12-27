package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "schema")
public class Schema {

    @Id
    private SchemaIdentifier id;
    private List<ApiSpecification> apiSpec;
    private Map<String, Object> vars;
    private boolean active = true;

}
