package com.romanboehm.wichtelnng;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
public @interface CustomSpringBootTest {

    @AliasFor("properties")
    String[] value() default {};

    /**
     * Properties in form {@literal key=value} that should be added to the Spring
     * {@link Environment} before the test runs.
     * @return the properties to add
     */
    @AliasFor("value")
    String[] properties() default {};

    /**
     * Application arguments that should be passed to the application under test.
     * @return the application arguments to pass to the application under test.
     * @see ApplicationArguments
     * @see SpringApplication#run(String...)
     * @since 2.2.0
     */
    String[] args() default {};

    /**
     * The <em>component classes</em> to use for loading an
     * {@link org.springframework.context.ApplicationContext ApplicationContext}. Can also
     * be specified using
     * {@link ContextConfiguration#classes() @ContextConfiguration(classes=...)}. If no
     * explicit classes are defined the test will look for nested
     * {@link Configuration @Configuration} classes, before falling back to a
     * {@link SpringBootConfiguration @SpringBootConfiguration} search.
     * @see ContextConfiguration#classes()
     * @return the component classes used to load the application context
     */
    Class<?>[] classes() default {};

    /**
     * The type of web environment to create when applicable. Defaults to
     * {@link SpringBootTest.WebEnvironment#MOCK}.
     * @return the type of web environment
     */
    SpringBootTest.WebEnvironment webEnvironment() default SpringBootTest.WebEnvironment.MOCK;
}
