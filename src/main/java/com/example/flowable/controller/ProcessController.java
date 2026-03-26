package com.example.flowable.controller;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Heshan Karunaratne
 */
@RestController
@RequestMapping("/process")
public class ProcessController {

    private final RuntimeService runtimeService;

    public ProcessController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/start")
    public String startProcess() {
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey("log_process_key");
        return "Started process with ID: " + instance.getId();
    }
}