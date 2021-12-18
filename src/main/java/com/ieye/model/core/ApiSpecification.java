package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class ApiSpecification extends BaseRestTemplate {

    @Id
    private String id;
    private String validationType = "FlashTest";
    private String testCollection;
    private String domain;
    private String stableDomain;

}
