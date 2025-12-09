package com.benchmark.jersey.config;

import com.benchmark.jersey.repository.CategoryRepository;
import com.benchmark.jersey.repository.ItemRepository;
import com.benchmark.jersey.service.CategoryService;
import com.benchmark.jersey.service.ItemService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ServerProperties;

/**
 * Jersey application configuration
 */
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Scan packages for resources
        packages("com.benchmark.jersey.resource");

        // Register Jackson for JSON
        register(JacksonFeature.class);
        register(new JacksonConfig());

        // Enable Bean Validation
        register(org.glassfish.jersey.server.validation.ValidationFeature.class);

        // Disable WADL (not needed for benchmark)
        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        // Register DI bindings
        register(new DependencyBinder());
    }

    /**
     * HK2 Dependency Injection configuration
     */
    private static class DependencyBinder extends AbstractBinder {
        @Override
        protected void configure() {
            // Create EntityManagerFactory with environment overrides
            java.util.Map<String, Object> props = new java.util.HashMap<>();
            String url = System.getenv("SPRING_DATASOURCE_URL");
            if (url != null)
                props.put("jakarta.persistence.jdbc.url", url);

            String user = System.getenv("SPRING_DATASOURCE_USERNAME");
            if (user != null)
                props.put("jakarta.persistence.jdbc.user", user);

            String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
            if (password != null)
                props.put("jakarta.persistence.jdbc.password", password);

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("benchmark-pu", props);
            bind(emf).to(EntityManagerFactory.class);

            // Bind repositories
            bindAsContract(CategoryRepository.class);
            bindAsContract(ItemRepository.class);

            // Bind services
            bindAsContract(CategoryService.class);
            bindAsContract(ItemService.class);
        }
    }
}
