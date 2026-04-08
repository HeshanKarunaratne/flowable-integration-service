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

    @PostMapping("/start/log")
    public String startLogProcess(@RequestParam String tenantId) {
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKeyAndTenantId("log_process_key", tenantId);
        return "Started log_process_key for tenant " + tenantId +
                " with ID: " + instance.getId();
    }

    @PostMapping("/start/script")
    public String startScriptProcess(@RequestParam String tenantId) {
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKeyAndTenantId("logScript", tenantId);
        return "Started logScript for tenant " + tenantId +
                " with ID: " + instance.getId();
    }
}