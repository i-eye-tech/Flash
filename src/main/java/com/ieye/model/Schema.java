package com.ieye.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@NoArgsConstructor
public class Schema {

    @NonNull @Id
    private SchemaIdentifier id;
    private List<ApiSpecification> apiSpec;
    private boolean active;

}
