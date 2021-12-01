# Wichteln
## What is it?
A demo/testbed for various technologies in form of a Secret Santa (German: "Wichteln") web application:
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA + Postgres  
- Testcontainers

## How to try it out?
### a) Web
Navigate to https://wichtelnng-app.herokuapp.com (N.B.: Initial load might take a while in case of cold starts with the Heroku free tier)
### b) On your machine
Make sure you have `docker` and `docker-compose` installed. Then
1) Clone this repo
2) Change to the repository's root directory
3) Run `./mvnw spring-boot:build-image`
4) Run `docker-compose -f docker-compose-local.yml up`
5) Navigate to http://localhost:8080
