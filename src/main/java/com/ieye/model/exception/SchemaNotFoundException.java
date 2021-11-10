package com.ieye.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SchemaNotFoundException extends RuntimeException {
    public SchemaNotFoundException(String msg) { super(msg); }
}
