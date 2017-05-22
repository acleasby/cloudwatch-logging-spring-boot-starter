package com.intapp.platform.logging.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import com.amazonaws.services.logs.AWSLogsClient;
import com.intapp.platform.logging.CloudWatchProperties;
import com.intapp.platform.logging.logback.appender.AmazonCloudWatchAppender;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

import static com.intapp.platform.logging.CloudWatchLoggingImportSelector.ENABLE_PROPERTY;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnProperty(name = ENABLE_PROPERTY, matchIfMissing = true)
public class CloudWatchLogbackConfiguration implements EnvironmentAware {
    private CloudWatchProperties cloudWatchProperties;

    public CloudWatchLogbackConfiguration() {
    }

    public CloudWatchLogbackConfiguration(CloudWatchProperties properties) {
        this.cloudWatchProperties = properties;
    }

    protected Layout<ILoggingEvent> createLayout(LoggerContext context) {
        PatternLayout patternLayout = new PatternLayout();

        patternLayout.setContext(context);
        patternLayout.setPattern(cloudWatchProperties.getPattern());
        patternLayout.start();

        return patternLayout;
    }

    @Bean
    @ConditionalOnMissingBean(AWSLogsClient.class)
    public AWSLogsClient awsLogsClient() {
        //TODO: use AWSLogs instead
        return new AWSLogsClient();
    }

    // TODO: don't treat as bean
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(AmazonCloudWatchAppender.class)
    public Appender<ILoggingEvent> cloudWatchAppender(AWSLogsClient awsLogsClient) {
        LoggerContext context = getLoggerContext();
        Layout<ILoggingEvent> layout = createLayout(context);

        AmazonCloudWatchAppender cloudWatchAppender = new AmazonCloudWatchAppender();

        cloudWatchAppender.setContext(context);
        cloudWatchAppender.setLogGroup(cloudWatchProperties.getLogGroup());
        cloudWatchAppender.setLogStream(cloudWatchProperties.getLogStream());
        cloudWatchAppender.setRegion(cloudWatchProperties.getRegion());

        cloudWatchAppender.setAwsLogsClient(awsLogsClient);
        cloudWatchAppender.setLayout(layout);

        cloudWatchAppender.start();

        return cloudWatchAppender;
    }

    @PostConstruct
    protected void addAppender() {
        AWSLogsClient client = awsLogsClient();
        Appender<ILoggingEvent> appender = cloudWatchAppender(client);

        LoggerContext loggerContext = getLoggerContext();

        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);

        rootLogger.warn("LOGBACK: [{}] APPENDER WAS ADDED!", appender.getName());
    }

    @Override
    public void setEnvironment(Environment environment) {
        // we can't guarantee, that @ComponentScan will be enabled for this package,
        // so building CloudWatchProperties from environment manually instead of @ConfigurationProperties/@Autowired

        if (cloudWatchProperties == null) {
            cloudWatchProperties = CloudWatchProperties.from(environment);
        }
    }

    protected LoggerContext getLoggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }
}
