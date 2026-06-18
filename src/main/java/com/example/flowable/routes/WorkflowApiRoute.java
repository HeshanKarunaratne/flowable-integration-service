package com.example.flowable.routes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowApiRoute extends RouteBuilder {

    private final RuntimeService runtimeService;

    @Override
    public void configure() {

        // SYNC PROCESS
        from("rest:post:/start-sync")
                .routeId("start-sync")
                .process(exchange -> {
                    runtimeService
                            .startProcessInstanceByKeyAndTenantId("sync-model", "SL");
                    exchange.getMessage().setBody("SYNC PROCESS COMPLETED");
                    log.info("SYNC PROCESS COMPLETED");
                })
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"));

        // ASYNC PROCESS
        from("rest:post:/start-async")
                .routeId("start-async")
                .process(exchange -> {
                    runtimeService
                            .startProcessInstanceByKeyAndTenantId("async-model", "SL");
                    exchange.getMessage().setBody("ASYNC PROCESS STARTED");
                    log.info("ASYNC PROCESS COMPLETED");
                })
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"));
    }
}