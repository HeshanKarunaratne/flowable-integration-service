package com.example.flowable.delegates;

/**
 * @author Heshan Karunaratne
 */

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("loggingServiceTask")
public class LoggingServiceTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("Flowable Service Task Executed!");
        System.out.println("Process Instance ID: " + execution.getProcessInstanceId());
    }
}