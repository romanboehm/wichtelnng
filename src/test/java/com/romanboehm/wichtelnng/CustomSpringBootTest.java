package com.romanboehm.wichtelnng;

import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
        webEnvironment = MOCK,
        properties = {
                "spring.mail.host=localhost",
                "spring.mail.port=3025",
                "spring.mail.username=testuser",
                "spring.mail.password=testpassword",
                "spring.mail.protocol=smtp",
                "spring.datasource.url=jdbc:tc:postgresql:latest:///postgres?TC_DAEMON=true",
                "com.romanboehm.wichtelnng.domain=http://localhost:8080",
                "com.romanboehm.wichtelnng.mail.from=wichteln@localhost.com"
        }
)
public @interface CustomSpringBootTest {

    /**
     * The type of web environment to create when applicable. Defaults to
     * {@link SpringBootTest.WebEnvironment#MOCK}.
     *
     * @return the type of web environment
     */
    SpringBootTest.WebEnvironment webEnvironment() default MOCK;
}
