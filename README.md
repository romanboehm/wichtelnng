# wichtelnng

## What is it?

The next generation of Secret Santa web apps. Based on Java, Spring Boot, Spring MVC, Thymeleaf, Spring Data, JPA, and
Postgres.

## How to try it out?

### On the Internet

Navigate to https://wichtelnng.romanboehm.com.

### Locally

```shell
./mvnw spring-boot:test-run
```
- Navigate to http://localhost:8080 for the application's UI.
- Check mail via a local roundcube instance, the address of which will be logged to the console.
  - login: \<mail-address-you-want-to-check\>
  - username: \<mail-address-you-want-to-check\>
  - password: \<mail-address-you-want-to-check\> (yes, same as username)
- Spring Boot Actuator is available at http://localhost:8081/actuator.
