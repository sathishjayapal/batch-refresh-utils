package me.sathish.aws_refresh.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "batch.job")
@Getter
@Setter
public class BatchJobProperties {

    private int schedulerPoolSize = 10;
    private int executorPoolSize = 5;
    private int executorQueueCapacity = 100;
    private int maxRetryAttempts = 3;
    private long retryDelayMillis = 5000;
    private long jobTimeoutMinutes = 60;
    private boolean enableScheduling = true;
    private long schedulerRefreshIntervalMinutes = 5;
}
