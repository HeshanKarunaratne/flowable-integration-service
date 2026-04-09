package com.example.flowable.deploy.workflows;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Heshan Karunaratne
 */
@Service
public class WorkflowDeploymentService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowDeploymentService.class);

    private final RepositoryService repositoryService;
    private final WorkflowResourceLoader resourceLoader;

    @Value("${flowable.tenants}")
    private String[] tenants;

    public WorkflowDeploymentService(RepositoryService repositoryService,
                                     WorkflowResourceLoader resourceLoader) {
        this.repositoryService = repositoryService;
        this.resourceLoader = resourceLoader;
    }

    public void deployAll() {

        long start = System.currentTimeMillis();

        Resource[] commonResources = resourceLoader.loadCommonDiagrams();

        LOG.info("Starting workflow deployment for {} tenants", tenants.length);

        for (String tenantId : tenants) {
            deployForTenant(tenantId, commonResources);
        }

        LOG.info("Completed workflow deployment in {} ms",
                System.currentTimeMillis() - start);
    }

    private void deployForTenant(String tenantId, Resource[] commonResources) {

        Resource[] tenantResources = resourceLoader.loadTenantSpecificDiagrams(tenantId);

        Set<String> overriddenKeys = new HashSet<>();

        try {
            for (Resource resource : tenantResources) {
                String processKey = extractProcessKey(resource);
                overriddenKeys.add(processKey);
                deploy(tenantId, resource, processKey);
            }

            for (Resource resource : commonResources) {

                String processKey = extractProcessKey(resource);

                if (overriddenKeys.contains(processKey)) {
                    LOG.debug("Skipping overridden process {} for tenant {}", processKey, tenantId);
                    continue;
                }

                deploy(tenantId, resource, processKey);
            }

        } catch (Exception ex) {
            LOG.error("Failed to deploy workflows for tenant {}", tenantId, ex);
        }
    }

    private void deploy(String tenantId, Resource resource, String processKey) throws IOException {

        String filename = resource.getFilename();

        Deployment deployment = repositoryService.createDeployment()
                .name(filename)
                .tenantId(tenantId)
                .addInputStream(filename, resource.getInputStream())
                .enableDuplicateFiltering()
                .deploy();

        logVersion(tenantId, processKey, deployment);
    }

    private void logVersion(String tenantId, String processKey, Deployment deployment) {

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .processDefinitionTenantId(tenantId)
                .latestVersion()
                .singleResult();

        if (pd != null) {
            LOG.info("Tenant={} Process={} Version={} DeploymentId={}",
                    tenantId, processKey, pd.getVersion(), deployment.getId());
        }
    }

    private String extractProcessKey(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            throw new IllegalStateException("Invalid BPMN file");
        }
        return filename.replaceAll("\\.bpmn20\\.xml$", "");
    }
}