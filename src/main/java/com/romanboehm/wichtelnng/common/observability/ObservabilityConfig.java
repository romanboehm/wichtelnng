package com.romanboehm.wichtelnng.common.observability;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

@Configuration
public class ObservabilityConfig {

    // To have the @Observed support we need to register this aspect
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    // To have MDC entries automatically carried over to (and removed from) new threads, as long as the latter
    // are managed by a Spring thread pooling mechanism.
    @Bean
    public TaskDecorator taskDecorator() {
        return runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    MDC.setContextMap(contextMap);
                    runnable.run();
                }
                finally {
                    MDC.clear();
                }
            };
        };
    }
}
