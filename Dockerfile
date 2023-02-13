# Inspired by https://spring.io/guides/topicals/spring-boot-docker/, so we can make use of layers and caching thereof.
# Works because special layered jar is provided by spring-boot-maven-plugin.
# Cf. https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#packaging.layers.
# Requires layered jar to be present in target directory.
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

RUN mkdir -p target/dependency
COPY target/*.jar target
RUN cd target/dependency; jar -xf ../*.jar

FROM eclipse-temurin:17-jdk-alpine

# Provide non-root user.
RUN addgroup -S wichtelnng-user && adduser -S wichtelnng-user -G wichtelnng-user
USER wichtelnng-user

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.romanboehm.wichtelnng.WichtelnNgApplication"]