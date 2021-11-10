package com.ieye.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSpecification {

    @Id
    private String id;
    private String validationType;
    private String testCollection;
    private String domain;
    private String method;
    private String name;
    private String endPoint;

}
