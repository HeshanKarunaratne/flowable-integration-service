package com.example.flowable.deploy.workflows;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
public class WorkflowDeploymentRunner implements ApplicationRunner {

    private final WorkflowDeploymentService deploymentService;

    public WorkflowDeploymentRunner(WorkflowDeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @Override
    public void run(ApplicationArguments args) {
        deploymentService.deployAll();
    }
}