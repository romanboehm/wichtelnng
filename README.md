# wichtelnng

## What is it?

The next generation of Secret Santa web apps. Based on Java, Spring Boot, Spring MVC, Thymeleaf, Spring Data, JPA, and
Postgres.

## How to try it out?

### On the Internet

Navigate to https://wichtelnng.romanboehm.com.

### Locally

Run the wichtelnng app with the _compose_ profile:
```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=compose
```
- Navigate to http://localhost:8080 for the application's UI.
- Check mail via a local roundcube instance running on http://localhost:8082 (use \<mail-address-you-want-to-check\> as both username and password).
- Spring Boot Actuator is available on http://localhost:8081/actuator.
- Postgres db available on localhost:5432.
