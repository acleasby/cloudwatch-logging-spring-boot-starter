package com.intapp.platform.logging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables CloudWatch logging for project.
 * <br/>
 * By default, logging will be enabled only in "cloud" profile.
 * CloudWatch logging can be enabled/disabled setting property {@code logging.cloudwatch.enabled}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CloudWatchLoggingImportSelector.class})
public @interface EnableCloudWatchLogging {
    /**
     * Profile, for which CloudWatch logging should be enabled
     */
    String value() default "cloud";

    /**
     * List of profiles, for which CloudWatch logging should be enabled
     */
    String[] profiles() default {};

    /**
     * Enables/disables CloudWatch logging
     */
    boolean enabled() default true;
}
