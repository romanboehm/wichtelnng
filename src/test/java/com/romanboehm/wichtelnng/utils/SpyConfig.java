package com.romanboehm.wichtelnng.utils;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

/**
 * This config is intended to globally register spies in order to not pollute the context cache by adding different
 * spies for each @SpringBootTest.
 */
@Configuration
public class SpyConfig {

    @Bean
    public BeanPostProcessor spyOnTaskScheduler() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof TaskScheduler taskScheduler) {
                    return Mockito.spy(taskScheduler);
                }
                return bean;
            }
        };
    }
}
