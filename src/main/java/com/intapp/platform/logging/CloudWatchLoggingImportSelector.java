package com.intapp.platform.logging;

import com.intapp.platform.logging.logback.CloudWatchLogbackConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Selects CloudWatch Configuration bean, which should be imported (if such is present)
 */
@Slf4j
public class CloudWatchLoggingImportSelector implements ImportSelector, EnvironmentAware {
    /**
     * Property to enable/disable CloudWatch logging
     */
    public static final String ENABLE_PROPERTY = "logging.cloudwatch.enabled";

    /**
     * Spring environment
     */
    private Environment environment;

    /**
     * Checks if CloudWatch is enabled in current run
     * @param attributes {@code @EnableCloudWatchLogging} annotation parameters
     * @return {@code true} if CloudWatch should be enabled; {@code false} otherwise
     */
    private boolean isEnabled(AnnotationAttributes attributes) {
        // global enable in config file
        if (!environment.getProperty(ENABLE_PROPERTY, Boolean.class, true)) {
            log.debug("CLOUDWATCH logging is disabled via configuration parameter. Skipping...");
            return false;
        }

        // profiles check
        String[] selectedProfiles = attributes.getStringArray("value");
        return environment.acceptsProfiles(selectedProfiles);
    }


    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableCloudWatchLogging.class.getName(), false)
        );

        if (isEnabled(attributes)) {
            return new String[] { CloudWatchLogbackConfiguration.class.getName() };
        }

        return new String[0];
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
