package com.example.flowable.deploy.workflows;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
public class WorkflowResourceLoader {

    private final PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver();

    public Resource[] loadCommonDiagrams() {
        return safeLoad("classpath:processes/common/*.bpmn20.xml");
    }

    public Resource[] loadTenantSpecificDiagrams(String tenantId) {
        return safeLoad("classpath:processes/" + tenantId + "/*.bpmn20.xml");
    }

    private Resource[] safeLoad(String path) {
        try {
            return resolver.getResources(path);
        } catch (Exception e) {
            return new Resource[0];
        }
    }
}