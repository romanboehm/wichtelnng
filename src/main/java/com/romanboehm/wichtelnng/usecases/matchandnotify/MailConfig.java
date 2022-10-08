package com.romanboehm.wichtelnng.usecases.matchandnotify;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Configuration
class MailConfig {

    @Bean
    ClassLoaderTemplateResolver textTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(1);
        templateResolver.setResolvablePatterns(Set.of("*.txt"));
        templateResolver.setPrefix("/templates/");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
