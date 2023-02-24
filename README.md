# wichtelnng

## What is it?

The next generation of Secret Santa web apps. Based on Java, Spring Boot, Spring MVC, Thymeleaf, Spring Data, JPA, and
Postgres.

## How to try it out?

### On the Internet

Navigate to https://wichtelnng.fly.dev/.

### Locally

1) Run the `com.romanboehm.wichtelnng.TestWichtelnNgApplication` class (under src/test/java) from an IDE of your choice.
2) Navigate to http://localhost:8080 for the application's UI.

You can check mail via a local roundcube instance like so:

1) Navigate to roundcube's UI port by opening the output of the following command in your browser:
   ```shell
   docker port $(docker ps --filter ancestor=roundcube/roundcubemail --format "{{.Names}}") 80/tcp
   ```
2) Use \<mail-address-you-want-to-check\> as both username and password.
