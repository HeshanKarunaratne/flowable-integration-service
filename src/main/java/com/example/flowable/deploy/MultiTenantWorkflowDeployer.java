package com.example.flowable.deploy;

import jakarta.annotation.PostConstruct;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

@Configuration
public class MultiTenantWorkflowDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(MultiTenantWorkflowDeployer.class);
    private final RepositoryService repositoryService;

    @Value("${flowable.tenants}")
    private String[] tenants;

    public MultiTenantWorkflowDeployer(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostConstruct
    public void deployWorkflowsForAllTenants() throws IOException {

        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:processes/*.bpmn20.xml");
        LOG.info("Starting workflow deployment for {} tenants", tenants.length);

        for (String tenantId : tenants) {
            for (Resource resource : resources) {

                String filename = resource.getFilename();
                if (filename == null) continue;

                String processKey = filename.replaceAll("\\.bpmn20\\.xml$", "");

                long count = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(processKey)
                        .processDefinitionTenantId(tenantId)
                        .count();

                if (count == 0) {
                    repositoryService.createDeployment()
                            .name(filename)
                            .tenantId(tenantId)
                            .addInputStream(filename, resource.getInputStream())
                            .enableDuplicateFiltering()
                            .deploy();

                    LOG.info("Deployed {} for tenant {}", filename, tenantId);
                } else {
                    LOG.info("Skipped {} for tenant {} (already exists)", filename, tenantId);
                }

            }
        }

        LOG.info("Completed workflow deployment for all tenants");
    }
}