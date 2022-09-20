# Build stage, where we just build the app
FROM maven:3.8.3-openjdk-17 AS build

# Copy the pom inside discography dir
COPY pom.xml /discography/pom.xml
WORKDIR /discography/

# Download dependencies so they can be cached if there weren't changes in the pom
RUN mvn dependency:go-offline dependency:resolve dependency:resolve-plugins

# Copy the source files inside discography dir
COPY src /discography/src

# Move to the wordle-backend dir and build the app
RUN mvn package

# Final stage, where we run the app
FROM openjdk:17 AS final

# Copy the jar file from previous stage into this one
COPY --from=build /discography/target/discography-RELEASE.jar .

# Expose the ports we are using
#EXPOSE 9090
# Run the app
CMD ["java", "-jar", "discography-RELEASE.jar"]