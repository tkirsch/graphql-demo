FROM maven:3.5-jdk-11 as build
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn clean install
COPY . .
RUN mvn -o clean install

FROM openjdk:11-jre-slim as app
COPY --from=build /usr/src/app/target/app-fat.jar .
EXPOSE 8181
CMD java -jar app-fat.jar