package io.argorand.poc.dcpass;

import io.argorand.poc.dcpass.config.AsyncSyncConfiguration;
import io.argorand.poc.dcpass.config.DatabaseTestcontainer;
import io.argorand.poc.dcpass.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        DcpassApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        io.argorand.poc.dcpass.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
