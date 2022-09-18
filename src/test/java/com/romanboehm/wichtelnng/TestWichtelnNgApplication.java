package com.romanboehm.wichtelnng;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class TestWichtelnNgApplication {
    public static void main(String[] args) {
        var application = WichtelnNgApplication.createSpringApplication();

        application.addInitializers(new DbInitializer(), new MailInitializer());

        application.run(args);
    }

    private static class DbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            postgres.start();

            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("db", Map.of(
                    "spring.datasource.url", postgres.getJdbcUrl(),
                    "spring.datasource.username", postgres.getUsername(),
                    "spring.datasource.password", postgres.getPassword()
            )));
        }
    }

    private static class MailInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static final GenericContainer<?> greenmail = new GenericContainer<>(DockerImageName.parse("greenmail/standalone:latest"));
        private static final GenericContainer<?> roundcube = new GenericContainer<>(DockerImageName.parse("roundcube/roundcubemail:latest"));

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            greenmail
                    .withExposedPorts(3025, 3143)
                    .start();
            Integer imap = greenmail.getMappedPort(3143);
            Integer smtp = greenmail.getMappedPort(3025);

            Testcontainers.exposeHostPorts(imap);
            Testcontainers.exposeHostPorts(smtp);

            roundcube
                    .withExposedPorts(80)
                    .withEnv("ROUNDCUBEMAIL_DEFAULT_HOST", "http://host.testcontainers.internal")
                    .withEnv("ROUNDCUBEMAIL_DEFAULT_PORT", imap.toString())
                    .withEnv("ROUNDCUBEMAIL_SMTP_SERVER", "http://host.testcontainers.internal")
                    .withEnv("ROUNDCUBEMAIL_SMTP_PORT", smtp.toString())
                    .start();

            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("mail", Map.of(
                    "spring.mail.host", greenmail.getHost(),
                    "spring.mail.port", smtp.toString(),
                    "spring.mail.protocol", "smtp"
            )));
        }
    }
}
