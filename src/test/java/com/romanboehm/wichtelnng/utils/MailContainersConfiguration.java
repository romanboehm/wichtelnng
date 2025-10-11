package com.romanboehm.wichtelnng.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class MailContainersConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MailContainersConfiguration.class);

    @Bean
    Network mailNetwork() {
        return Network.newNetwork();
    }

    @Bean
    GenericContainer<?> mailServerContainer(Network mailNetwork) {
        return new GenericContainer<>(DockerImageName.parse("greenmail/standalone:2.0.0"))
                // 3025 for STMP, 3143 for IMAP
                .withExposedPorts(3025, 3143)
                .withNetwork(mailNetwork)
                .withNetworkAliases("wichtelnng-mail-server");
    }

    @Bean
    DynamicPropertyRegistrar mailProperties(GenericContainer<?> mailServerContainer) {
        return registry -> registry.add(
                "spring.mail.port",
                () -> mailServerContainer.getMappedPort(3025));
    }

    @Bean
    GenericContainer<?> webmailContainer(Network mailNetwork) {
        return new GenericContainer<>(DockerImageName.parse("roundcube/roundcubemail:1.6.1-apache"))
                .withExposedPorts(80)
                .withNetwork(mailNetwork)
                .withNetworkAliases("wichtelnng-mail-ui")
                .withEnv("ROUNDCUBEMAIL_DEFAULT_PORT", "3143")
                .withEnv("ROUNDCUBEMAIL_DEFAULT_HOST", "wichtelnng-mail-server")
                .withEnv("ROUNDCUBEMAIL_SMTP_PORT", "3025")
                .withEnv("ROUNDCUBEMAIL_SMTP_SERVER", "wichtelnng-mail-server");
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> mailUiPortLogger(GenericContainer<?> webmailContainer) {
        return __ -> log.info("wichtelnng-mail-ui: http://localhost:{}", webmailContainer.getMappedPort(80));
    }

}
