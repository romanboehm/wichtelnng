package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.utils.DatabaseContainerConfiguration;
import com.romanboehm.wichtelnng.utils.MailContainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestWichtelnNgApplication {

    // `./mvnw spring-boot:test-run`.
    public static void main(String[] args) {
        SpringApplication.from(WichtelnNgApplication::main)
                .with(
                        DatabaseContainerConfiguration.class,
                        MailContainersConfiguration.class)
                .run(args);
    }
}
