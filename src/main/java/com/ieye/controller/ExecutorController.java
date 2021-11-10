package com.ieye.controller;

import com.ieye.core.Runner;
import com.ieye.model.StartRequestDTO;
import com.ieye.model.StartResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ExecutorController {

    @Autowired
    Runner runner;

    @PostMapping("start")
    public StartResponseDTO start(@RequestBody final StartRequestDTO startRequestDTO) {
        String uuid = UUID.randomUUID().toString();
        runner.run(startRequestDTO, uuid);
        return new StartResponseDTO(uuid, startRequestDTO.getTestId(), startRequestDTO.getProjectId());
    }

}
