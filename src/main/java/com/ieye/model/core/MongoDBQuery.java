package com.ieye.model.core;

import lombok.Data;

@Data
public class MongoDBQuery {

    private String find;
    private String sort;
    private String projection;
    private int limit;

}
