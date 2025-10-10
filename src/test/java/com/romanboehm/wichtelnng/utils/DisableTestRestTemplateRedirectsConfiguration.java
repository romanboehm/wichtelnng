package com.romanboehm.wichtelnng.utils;

import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

// Needed since Spring Boot 3.4.0, else `TestRestTemplate` will follow redirects by default, and
// we wouldn't be able to assert HTTP 302 FOUND responses anymore.
@TestConfiguration(proxyBeanMethods = false)
public class DisableTestRestTemplateRedirectsConfiguration {

    @Bean
    RestTemplateBuilder doNotFollowRedirects(RestTemplateBuilderConfigurer configurer) {
        return configurer.configure(new RestTemplateBuilder())
                .requestFactorySettings(settings -> settings.withRedirects(ClientHttpRequestFactorySettings.Redirects.DONT_FOLLOW));
    }
}
