package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collation = "schema")
public class Schema {

    @NonNull @Id
    private SchemaIdentifier id;
    private List<ApiSpecification> apiSpec;
    private boolean active;

}
