package com.example.flowable.delegates;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Slf4j
@Component("taskA")
public class TaskADelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info(">>> Task A executed. processInstanceId={}", execution.getProcessInstanceId());
    }
}