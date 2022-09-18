# Wichteln

## What is it?

A demo/testbed for various technologies in form of a Secret Santa (German: "Wichteln") web application:

- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA + Postgres
- Testcontainers

## How to try it out?

### On the Internet

Coming soon™️.

### Locally

1) Run the `com.romanboehm.wichtelnng.TestWichtelnNgApplication` class (under src/test/java) from an IDE of your choice.
2) Navigate to http://localhost:8080 for the application's UI.

You can check mail via a local roundcube instance like so:

1) Retrieve roundcube's UI port
   via `docker port $(docker ps | grep roundcube | tr -s ' ' | cut -d' ' -f1) | cut -d':' -f2` (this is a bit lengthy
   due to Testcontainers using dynamic container names and mapped ports).
2) Navigate to http://localhost:<roundcube-ui-port\>.
3) Use \<mail-address-you-want-to-check\> as both username and password.