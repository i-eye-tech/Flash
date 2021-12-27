package com.ieye.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSpecification extends BaseRestTemplate {

    @Id
    private String id;
    private String validationType = "FlashTest";
    private String testCollection;
    private String stableDomain;
    private Map<String, Object> vars;

}
