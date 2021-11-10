package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@EqualsAndHashCode(callSuper = true)
@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSpecification extends BaseModel {

    @Id
    private String id;
    private String validationType;
    private String testCollection;
    private String domain;
    private String name;

}
