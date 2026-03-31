package com.example.flowable.deploy;

import jakarta.annotation.PostConstruct;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * Deploys all workflows from resources/processes/ to all tenants.
 * Skips deployment if the workflow already exists for a tenant.
 */
@Configuration
public class MultiTenantWorkflowDeployer {

    private final RepositoryService repositoryService;

    @Value("${flowable.tenants}")
    private String[] tenants;

    public MultiTenantWorkflowDeployer(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostConstruct
    public void deployWorkflowsForAllTenants() throws IOException {
        // Load all BPMN files
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:processes/*.bpmn20.xml");

        for (String tenantId : tenants) {
            System.out.println("Deploying workflows for tenant: " + tenantId);
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;

                // derive process key from filename (remove extension)
                String processKey = filename.replaceAll("\\.bpmn20\\.xml$", "");

                // Check if this process is already deployed for the tenant
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

                    System.out.println("✅ Deployed workflow: " + filename + " for tenant " + tenantId);
                } else {
                    System.out.println("⏩ Workflow already deployed: " + filename + " for tenant " + tenantId);
                }
            }
        }
        System.out.println("All tenant workflow deployments completed.");
    }
}