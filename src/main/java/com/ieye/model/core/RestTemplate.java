package com.ieye.model.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestTemplate extends BaseRestTemplate {

    private String expectedJson;
    private String body;
    private Integer expectedStatusCode;

}
