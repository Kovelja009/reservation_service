package com.komponente.reservation_service.user_sync_comm;

import com.komponente.reservation_service.exceptions.ForbiddenException;
import com.komponente.reservation_service.exceptions.NotFoundException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryConfiguration {

    @Bean
    public Retry serviceRetry() {
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(7).intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(2000), 2)).ignoreExceptions(ForbiddenException.class, IllegalArgumentException.class, NotFoundException.class).build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);

        return retryRegistry.retry("serviceRetry");
    }
}
