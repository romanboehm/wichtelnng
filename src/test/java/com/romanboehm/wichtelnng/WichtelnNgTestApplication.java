package com.romanboehm.wichtelnng;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

public class WichtelnNgTestApplication {

    public static void main(String[] args) {
        var application = WichtelnNgApplication.createSpringApplication();

        // Here we add the same initializer as we were using in our tests...
        application.addInitializers(new Initializer());
        
        // ... and start it normally
        application.run(args);
    }
    
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

      static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>();

      public static Map<String, String> getProperties() {
         Startables.deepStart(Stream.of(postgres)).join();

         return Map.of(
               "spring.datasource.url", postgres.getJdbcUrl(),
               "spring.datasource.username", postgres.getUsername(),
               "spring.datasource.password",postgres.getPassword()
         );
        }

      @Override
      public void initialize(ConfigurableApplicationContext context) {
         var env = context.getEnvironment();
         env.getPropertySources().addFirst(new MapPropertySource("testcontainers", (Map) getProperties()));
      }
  }
    
}
