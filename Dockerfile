FROM openjdk:21-jdk-oracle
WORKDIR /app
COPY target/movie-catalog.jar /app/movie-catalog.jar
#EXPOSE 8080
ENTRYPOINT ["java", "-jar", "movie-catalog.jar"]