package com.ieye.controller;

import com.ieye.model.ApiSpecification;
import com.ieye.model.SchemaIdentifier;
import com.ieye.model.dto.GetApiSpecResponseDTO;
import com.ieye.model.mapper.GetSpecificationMapper;
import com.ieye.service.SchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/schema")
@Slf4j
public class SchemaController {

    @Autowired
    SchemaService schemaService;

    @Autowired
    GetSpecificationMapper getSpecificationMapper;

    @GetMapping("/get_specification")
    public ResponseEntity<GetApiSpecResponseDTO> getApiSpec(@RequestBody final SchemaIdentifier schemaIdentifier) {
        log.debug("Start of method getApiSpec with input parameter: {}", schemaIdentifier);
        List<ApiSpecification> apiSpecificationList = schemaService.findByProjectAndTestId(schemaIdentifier);
        GetApiSpecResponseDTO response = getSpecificationMapper.mapApiSpecsToResponse(apiSpecificationList);
        return ResponseEntity.of(Optional.of(response));
    }

}
