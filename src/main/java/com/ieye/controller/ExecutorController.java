package com.ieye.controller;

import com.ieye.core.Runner;
import com.ieye.model.StartRequestDTO;
import com.ieye.model.StartResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ExecutorController {

    @Autowired
    Runner runner;

    @PostMapping("start")
    public ResponseEntity<StartResponseDTO> start(@RequestBody final StartRequestDTO startRequestDTO) {
        log.debug("Start of method start with input param {}", startRequestDTO);
        String uuid = UUID.randomUUID().toString();
        runner.run(startRequestDTO, uuid);
        return ResponseEntity.of(Optional.of(new StartResponseDTO(uuid, startRequestDTO.getTestId(), startRequestDTO.getProjectId())));
    }

}
