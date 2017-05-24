package com.intapp.platform.logging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables CloudWatch logging for project.
 * <br/>
 * By default, logging will be enabled for all profiles, but you can modify this behavior,
 * specifying profiles, for which logging should be enabled as annotation attribute(s).
 * CloudWatch logging can be enabled/disabled setting property {@code logging.cloudwatch.enabled}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CloudWatchLoggingImportSelector.class})
public @interface EnableCloudWatchLogging {
    /**
     * Profiles, for which CloudWatch logging should be enabled
     */
    String[] value();
}
