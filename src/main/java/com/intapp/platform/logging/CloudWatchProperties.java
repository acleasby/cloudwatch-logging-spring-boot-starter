package com.intapp.platform.logging;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.core.env.Environment;

/**
 * Properties for CloudWatch
 */
@Getter @Setter @Builder
public class CloudWatchProperties {
    @NonNull
    private String logGroup;

    @NonNull
    private String logStream;

    private String region;

    @NonNull
    private String pattern;


    /**
     * Creates properties instance from Spring environment settings.
     * Parameters from group {@code logging.cloudwatch.*} are used
     * @param env Spring environment
     * @return CloudWatch properties instance
     */
    public static CloudWatchProperties from(Environment env) {
        String logGroup = env.getProperty("logging.cloudwatch.log-group");
        String logStream = env.getProperty("logging.cloudwatch.log-stream");
        String pattern = env.getProperty("logging.cloudwatch.pattern", "[%d %-5level --- [%15.15thread] %-40.40logger{40} : %msg%n");
        String region = env.getProperty("logging.cloudwatch.region");

        return CloudWatchProperties.builder()
                .logGroup(logGroup)
                .logStream(logStream)
                .pattern(pattern)
                .region(region)
                .build();
    }
}
