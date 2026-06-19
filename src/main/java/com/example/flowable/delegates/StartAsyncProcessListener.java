package com.example.flowable.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Heshan Karunaratne
 */
@Component("startAsyncProcessListener")
@RequiredArgsConstructor
@Slf4j
public class StartAsyncProcessListener implements ExecutionListener {
    private final RuntimeService runtimeService;

    @Override
    public void notify(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        String businessKey = execution.getProcessInstanceBusinessKey();
        String tenantId = execution.getTenantId();

        variables.put("MANUAL", "TRUE");

        runtimeService.startProcessInstanceByKeyAndTenantId(
                "async-model",
                businessKey,
                variables,
                tenantId
        );
    }
}
